package com.github.unchama.seichistat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

//MySQL操作関数
public class Sql{
	private SeichiStat plugin;
	private final String url, db, id, pw;
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	public static String exc;

	//コンストラクタ
	Sql(SeichiStat plugin ,String url, String db, String id, String pw){
		this.plugin = plugin;
		this.url = url;
		this.db = db;
		this.id = id;
		this.pw = pw;
	}

	/**
	 * 接続関数
	 *
	 * @param url 接続先url
	 * @param id ユーザーID
	 * @param pw ユーザーPW
	 * @param db データベースネーム
	 * @param table テーブルネーム
	 * @return
	 */
	public boolean connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
			plugin.getLogger().info("Mysqlドライバーのインスタンス生成に失敗しました");
			return false;
		}
		//sql鯖への接続とdb作成
		if(!connectMySQL()){
			plugin.getLogger().info("SQL接続に失敗しました");
			return false;
		}
		if(!createDB()){
			plugin.getLogger().info("データベース作成に失敗しました");
			return false;
		}
		if(!connectDB()){
			plugin.getLogger().info("データベース接続に失敗しました");
			return false;
		}
		if(!createPlayerDataTable(SeichiStat.PLAYERDATA_TABLENAME)){
			plugin.getLogger().info("playerdataテーブル作成に失敗しました");
			return false;
		}

		return true;
	}

	private boolean connectMySQL(){
		try {
			if(stmt != null && !stmt.isClosed()){
				stmt.close();
				con.close();
			}
			con = (Connection) DriverManager.getConnection(url, id, pw);
			stmt = con.createStatement();
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    	return false;
		}
		return true;
	}

	/**
	 * コネクション切断処理
	 *
	 * @return 成否
	 */
	public boolean disconnect(){
	    if (con != null){
	    	try{
	    		stmt.close();
				con.close();
	    	}catch (SQLException e){
	    		e.printStackTrace();
	    		return false;
	    	}
	    }
	    return true;
	}

	//コマンド出力関数
	//@param command コマンド内容
	//@return 成否
	//@throws SQLException
	private boolean putCommand(String command){
		try {
			stmt.executeUpdate(command);
			return true;
		} catch (SQLException e) {
			//接続エラーの場合は、再度接続後、コマンド実行
			java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
			exc = e.getMessage();
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * データベース作成
	 * 失敗時には変数excにエラーメッセージを格納
	 *
	 * @param table テーブル名
	 * @return 成否
	 */
	public boolean createDB(){
		if(db==null){
			return false;
		}
		String command;
		command = "CREATE DATABASE IF NOT EXISTS " + db
				+ " character set utf8 collate utf8_general_ci";
		return putCommand(command);
	}

	private boolean connectDB() {
		String command;
		command = "use " + db;
		return putCommand(command);
	}

	/**
	 * テーブル作成
	 * 失敗時には変数excにエラーメッセージを格納
	 *
	 * @param table テーブル名
	 * @return 成否
	 */
	public boolean createPlayerDataTable(String table){
		if(table==null){
			return false;
		}
		//テーブルが存在しないときテーブルを新規作成
		String command =
				"CREATE TABLE IF NOT EXISTS " + table +
				"(name varchar(30) unique," +
				"uuid varchar(128) unique)";
		if(!putCommand(command)){
			return false;
		}
		//必要なcolumnを随時追加
		command =
				"alter table " + table +
				" add column if not exists num_rgbreak int default 0" +
				",add column if not exists lastquit datetime default null" +
				",add column if not exists playtick int default 0" +
				",add column if not exists loginflag boolean default false" +
				",add column if not exists num_magmadabaa int default 0" +
				",add index if not exists name_index(name)" +
				"";
		return putCommand(command);
	}

	public PlayerData loadPlayerData(Player p) {
		String name = Util.getName(p);
		UUID uuid = p.getUniqueId();
		String struuid = uuid.toString().toLowerCase();
		String command = "";
		String table = SeichiStat.PLAYERDATA_TABLENAME;
 		int count = -1;
 		//uuidがsqlデータ内に存在するか検索
 		//command:
 		//select count(*) from playerdata where uuid = 'struuid'
 		command = "select count(*) as count from " + table
 				+ " where uuid = '" + struuid + "'";
 		try{
			rs = stmt.executeQuery(command);
			while (rs.next()) {
				   count = rs.getInt("count");
				  }
			rs.close();
		} catch (SQLException e) {
			exc = e.getMessage();
			return null;
		}

 		if(count == 0){
 			//uuidが存在しない時の処理

 			//新しくuuidとnameを設定し行を作成
 			//insert into playerdata (name,uuid) VALUES('unchima','UNCHAMA')
 			command = "insert into " + table
 	 				+ " (name,uuid) values('" + name
 	 				+ "','" + struuid + "')";
 			if(!putCommand(command)){
 				return null;
 			}
 			//PlayerDataを新規作成
 			return new PlayerData(p);

 		}else if(count == 1){
 			//uuidが存在するときの処理
 			if(SeichiStat.DEBUG){
 				p.sendMessage("sqlにデータが保存されています。");
 			}

 			//loginflag判別処理
 			Boolean flag = true;
 			int i = 0;
 			//flagがfalseになるまで繰り返す
 			while(flag){
	 	 		command = "select loginflag from " + table
	 	 				+ " where uuid = '" + struuid + "'";
	 	 		try{
	 				rs = stmt.executeQuery(command);
	 				while (rs.next()) {
	 					   flag = rs.getBoolean("loginflag");
	 					  }
	 				rs.close();
	 			} catch (SQLException e) {
	 				java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
	 				exc = e.getMessage();
	 				e.printStackTrace();
	 				return null;
	 			}
	 	 		if(i < 5&&flag){
	 	 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + p.getName() + "のloginflag=false待機…(" + (i+1) + "回目)(SeichiStat)");
	 	 			//次のリクエストまで1000ms待つ
	 	 			try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
	 	 		}
	 	 		if(i > 5&&flag){
	 	 			//諦める
	 	 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + p.getName() + "のloginflagがtrueの為、プレイヤーデータが取得できませんでした(SeichiStat)");
	 	 			return null;
	 	 		}
	 	 		i++;
 			}
 			//loginflag書き換え処理
 			command = "update " + table
						+ " set loginflag = true"
						+ " where uuid like '" + struuid + "'";
 			if(!putCommand(command)){
 				return null;
 			}


 			//PlayerDataを新規作成
 			PlayerData playerdata = new PlayerData(p);

 			//sqlデータから得られた値で更新

 			command = "select * from " + table
 					+ " where uuid like '" + struuid + "'";
 			try{
 				rs = stmt.executeQuery(command);
 				while (rs.next()) {

 					//各種数値
 	 				playerdata.num_rgbreak = rs.getInt("num_rgbreak");
 	 				playerdata.playtick = rs.getInt("playtick");
 	 				playerdata.num_magmadabaa = rs.getInt("num_magmadabaa");

 				  }
 				rs.close();
 			} catch (SQLException e) {
 				java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
 				exc = e.getMessage();
 				e.printStackTrace();
 				return null;
 			}
 			if(SeichiStat.DEBUG){
 				p.sendMessage("sqlデータで更新しました。");
 			}
 			//更新したplayerdataを返す
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + p.getName() + "のSeichiStat読込完了");
 			//更新したplayerdataを返す
 			return playerdata;
 		}else{
 			//mysqlに該当するplayerdataが2個以上ある時エラーを吐く
 			Bukkit.getLogger().info(Util.getName(p) + "のplayerdataがmysqlに2個以上ある為、正常にロード出来ませんでした");
 			p.sendMessage("独自機能のロードに失敗しました。管理人に報告して下さい");
 			return null;
 		}
	}
	public boolean savePlayerData(PlayerData playerdata) {
		//引数のplayerdataをsqlにデータを送信

		String table = SeichiStat.PLAYERDATA_TABLENAME;
		String struuid = playerdata.uuid.toString();
		String command = "";

		command = "update " + table
				+ " set"

				//名前更新処理
				+ " name = '" + playerdata.name + "'"

				//各種数値更新処理
				+ ",lastquit = cast( now() as datetime )"
				+ ",num_rgbreak = " + Integer.toString(playerdata.num_rgbreak)
				+ ",playtick = " + Integer.toString(playerdata.playtick)
				+ ",num_magmadabaa = " + Integer.toString(playerdata.num_magmadabaa)

				+ " where uuid like '" + struuid + "'";

		return putCommand(command);
	}

	//loginflagのフラグ折る処理(ondisable時とquit時に実行させる)
	public boolean logoutPlayerData(PlayerData playerdata) {
		String table = SeichiStat.PLAYERDATA_TABLENAME;
		String struuid = playerdata.uuid.toString();
		String command = "";

		command = "update " + table
				+ " set"

				//ログインフラグ折る
				+ " loginflag = false"

				+ " where uuid like '" + struuid + "'";

		return putCommand(command);

	}

}
package com.github.unchama.seichistat.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

public class PlayerDataLoadTaskRunnable extends BukkitRunnable {
	private SeichiStat plugin = SeichiStat.plugin;
	private HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;
	private Sql sql = SeichiStat.plugin.sql;

	final String table = SeichiStat.PLAYERDATA_TABLENAME;

	String name;
	Player p;
	final UUID uuid;
	final String struuid;
	String command;
	public static String exc;
	Boolean flag;
	int i;
	Statement stmt = null;
	ResultSet rs = null;
	String db;

	public PlayerDataLoadTaskRunnable(Player _p) {
		db = SeichiStat.config.getDB();
		p = _p;
		name = Util.getName(p);
		uuid = p.getUniqueId();
		struuid = uuid.toString().toLowerCase();
		command = "";
		flag = true;
		i = 0;
	}

	@Override
	public void run() {
		//対象プレイヤーがオフラインなら処理終了
		if(SeichiStat.plugin.getServer().getPlayer(uuid) == null){
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + p.getName() + "はオフラインの為取得処理を中断");
			cancel();
			return;
		}
		//同ステートメントだとmysqlの処理がバッティングした時に止まってしまうので別ステートメントを作成する
		//sqlコネクションチェック
		sql.checkConnection();
		try {
			stmt = sql.con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

 		command = "select loginflag from " + db + "." + table
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
			cancel();
			return;
		}

 		if(i >= 4&&flag){
 			//強制取得実行
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + p.getName() + " -> force SeichiStat load.");
 			cancel();
 		}else if(!flag){
 			//flagが折れてたので普通に取得実行
 			cancel();
 		}else{
 			//再試行
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + p.getName() + " -> waiting LoginFlag... (" + (i+1) + ")");
 			i++;
 			return;
 		}

		//loginflag書き換え&lastquit更新処理
		command = "update " + db + "." + table
				+ " set loginflag = true"
				+ ",lastquit = cast( now() as datetime )"
				+ " where uuid like '" + struuid + "'";
		try {
			stmt.executeUpdate(command);
		} catch (SQLException e) {
			java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
			exc = e.getMessage();
			e.printStackTrace();
			cancel();
			return;
		}

		//PlayerDataを新規作成
		PlayerData playerdata = new PlayerData(p);

		//playerdataをsqlデータから得られた値で更新
		command = "select * from " + db + "." + table
				+ " where uuid like '" + struuid + "'";
		try{
			rs = stmt.executeQuery(command);
			while (rs.next()) {
					//各種数値
	 				playerdata.num_rgbreak = rs.getInt("num_rgbreak");
	 				playerdata.playtick = rs.getInt("playtick");
	 				playerdata.num_magmadabaa = rs.getInt("num_magmadabaa");
	 				playerdata.num_chat = rs.getInt("num_chat");
	 				playerdata.num_cheatdabaa = rs.getInt("num_cheatdabaa");
	 				playerdata.num_command = rs.getInt("num_command");
	 				playerdata.num_cpbreak = rs.getInt("num_cpbreak");
			  }
			rs.close();
		} catch (SQLException e) {
			java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
			exc = e.getMessage();
			e.printStackTrace();

			//コネクション復活後にnewインスタンスのデータで上書きされるのを防止する為削除しておく
			playermap.remove(uuid);

			//既に宣言済み
			//cancel();
			return;
		}
		//念のためstatement閉じておく
		try {
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(SeichiStat.DEBUG){
			p.sendMessage("sqlデータで更新しました");
		}
		//更新したplayerdataをplayermapに追加
		playermap.put(uuid, playerdata);
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + p.getName() + " -> SeichiStat OK.");

		return;
	}

}

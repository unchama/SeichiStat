package com.github.unchama.seichistat.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;
import com.github.unchama.seichistat.data.PlayerData;

public class PlayerDataSaveTaskRunnable extends BukkitRunnable {

	private SeichiStat plugin = SeichiStat.plugin;
	private HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;
	private Sql sql = SeichiStat.plugin.sql;

	final String table = SeichiStat.PLAYERDATA_TABLENAME;

	PlayerData playerdata;
	String command;
	int i;
	//ondisableからの呼び出し時のみtrueにしておくフラグ
	boolean isOnDisable;
	//loginflag折る時にtrueにしておくフラグ
	boolean logoutflag;
	public static String exc;
	String db;
	Statement stmt = null;
	ResultSet rs = null;

	public PlayerDataSaveTaskRunnable(PlayerData _playerdata,boolean _isondisable,boolean _logoutflag) {
		db = SeichiStat.config.getDB();
		command = "";
		i = 0;
		playerdata = _playerdata;
		//ondisableからの呼び出し時のみtrueにしておくフラグ
		isOnDisable = _isondisable;
		//loginflag折る時にtrueにしておくフラグ
		logoutflag = _logoutflag;
	}

	@Override
	public void run() {
		//同ステートメントだとmysqlの処理がバッティングした時に止まってしまうので別ステートメントを作成する
		//sqlコネクションチェック
		sql.checkConnection();
		try {
			stmt = sql.con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

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
				+ ",num_chat = " + Integer.toString(playerdata.num_chat)
				+ ",num_cheatdabaa = " + Integer.toString(playerdata.num_cheatdabaa)
				+ ",num_command = " + Integer.toString(playerdata.num_command)
				+ ",num_cpbreak = " + Integer.toString(playerdata.num_cpbreak)
				;

		//loginflag折る処理
		if(logoutflag){
			command = command +
					",loginflag = false";
		}

		//最後の処理
		command = command + " where uuid like '" + struuid + "'";

		boolean result;

		try {
			stmt.executeUpdate(command);
			result = true;
		}catch (SQLException e) {
			java.lang.System.out.println("sqlクエリの実行に失敗しました。以下にエラーを表示します");
			exc = e.getMessage();
			e.printStackTrace();
			result = false;
		}

 		if(isOnDisable){
 			//ondisableメソッドからの呼び出しの時の処理
 			if(result){
 				plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + playerdata.name + " -> SeichiStat Saved.");
 			}else{
 				plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + playerdata.name + " -> SeichiStat Save Failed.");
 			}
 			return;
 		}else if(/*i >= 4&&*/!result){
 			//諦める
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + playerdata.name + " -> SeichiStat Save Failed.");
 			cancel();
 			return;
 		}else if(result){
 			//処理完了
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + playerdata.name + " -> SeichiStat Saved.");
 			cancel();
 			return;
 		}/*else{
 			//再試行
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + playerdata.name + "のプレイヤーデータ保存再試行(" + (i+1) + "回目)");
 			i++;
 			return;
 		}*/
	}

}

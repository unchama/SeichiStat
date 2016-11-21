package com.github.unchama.seichistat.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;
import com.github.unchama.seichistat.data.LogPlayerData;

public class SendLogPlayerDataTaskRunnable extends BukkitRunnable{

	private SeichiStat plugin = SeichiStat.plugin;
	private Sql sql = SeichiStat.plugin.sql;
	final String table = SeichiStat.STATICDATA_GENERAL_TABLENAME;

	String exc;
	Statement stmt = null;
	ResultSet rs = null;

	LogPlayerData logplayerdata;

	public SendLogPlayerDataTaskRunnable(LogPlayerData _logplayerdata) {
		logplayerdata = _logplayerdata;
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

		//引数を元にsql文を作成して送信する
		String struuid = logplayerdata.uuid.toString();
		String command = "";

		command = "insert into " + table + " ("
				+ "name,uuid,date"
				+ ",all_drop,all_pickup,all_mine_block,all_use_item"
				+ ",all_break_item,all_craft_item,nowplace_world"
				+ ",nowplace_x,nowplace_y,nowplace_z"
				+ ") values("
				+ "'" + logplayerdata.name+ "','" + struuid + "',cast( now() as datetime )"
				+ ",'" + logplayerdata.all_drop + "'"
				+ ",'" + logplayerdata.all_pickup + "'"
				+ ",'" + logplayerdata.all_mine_block + "'"
				+ ",'" + logplayerdata.all_use_item + "'"
				+ ",'" + logplayerdata.all_break_item + "'"
				+ ",'" + logplayerdata.all_craft_item + "'"
				+ ",'" + logplayerdata.nowplace_world + "'"
				+ ",'" + logplayerdata.nowplace_x + "'"
				+ ",'" + logplayerdata.nowplace_y + "'"
				+ ",'" + logplayerdata.nowplace_z + "'"
				+ ")";

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

 		if(/*i >= 4&&*/!result){
 			//諦める
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + logplayerdata.name + "のStatic_Genralデータ送信失敗");
 			cancel();
 			return;
 		}else if(result){
 			//処理完了
 			//plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + name + "のStaticデータ送信完了");
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

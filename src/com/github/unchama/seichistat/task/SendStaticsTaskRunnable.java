package com.github.unchama.seichistat.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;

public class SendStaticsTaskRunnable extends BukkitRunnable{

	private SeichiStat plugin = SeichiStat.plugin;
	private Sql sql = SeichiStat.plugin.sql;
	final String table = SeichiStat.STATICDATA_TABLENAME;

	String exc;
	Statement stmt = null;
	ResultSet rs = null;

	String name;
	UUID uuid;
	HashMap<String,Integer> statmap;

	public SendStaticsTaskRunnable(String _name,UUID _uuid,HashMap<String,Integer> _statmap) {
		name = _name;
		uuid = _uuid;
		statmap = _statmap;
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
		String table = SeichiStat.STATICDATA_TABLENAME;
		String struuid = uuid.toString();
		String command = "";

		command = "insert into " + table + " (name,uuid";

		//static内の数値をすべて列挙
		for(Statistic statistic : Statistic.values()){
			command = command +
					",`" + statistic.name() + "`";
		}

		command = command +
				",date) values('" + name+ "','" + struuid + "'";

		//static内の数値をすべて列挙
		for(Statistic statistic : Statistic.values()){
			command = command +
					",'" + statmap.get(statistic.name()) + "'";
		}

		command = command +
				",cast( now() as datetime ))";

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
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + name + "のStaticデータ送信失敗");
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

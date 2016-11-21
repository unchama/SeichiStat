package com.github.unchama.seichistat.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;

public class SendMineStaticsTaskRunnable extends BukkitRunnable {

	private SeichiStat plugin = SeichiStat.plugin;
	private Sql sql = SeichiStat.plugin.sql;
	final String table = SeichiStat.STATICDATA_MINE_TABLENAME;

	String exc;
	Statement stmt = null;
	ResultSet rs = null;

	String name;
	UUID uuid;
	HashMap<String,Integer> minestatmap;

	public SendMineStaticsTaskRunnable(String _name,UUID _uuid,HashMap<String,Integer> _minestatmap) {
		name = _name;
		uuid = _uuid;
		minestatmap = _minestatmap;
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
		String struuid = uuid.toString();
		String command = "";

		command = "insert into " + table + " (name,uuid";

		//material内の数値をすべて列挙
		for(Material material : Material.values()){
			command = command +
					",`" + material.name() + "`";
		}

		command = command +
				",date) values('" + name+ "','" + struuid + "'";

		//static内の数値をすべて列挙
		for(Material material : Material.values()){
			int n = -2;
			if(!(minestatmap.get(material.name()) == null)){
				n = minestatmap.get(material.name());
			}
			command = command +
					",'" + n + "'";
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
 			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + name + "のStatic_Mineデータ送信失敗");
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

package com.github.unchama.seichistat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.unchama.seichistat.commands.seichistatCommand;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.listener.PlayerBlockBreakListener;
import com.github.unchama.seichistat.listener.PlayerBucketListener;
import com.github.unchama.seichistat.listener.PlayerJoinListener;
import com.github.unchama.seichistat.listener.PlayerQuitListener;
import com.github.unchama.seichistat.task.MinuteTaskRunnable;
import com.github.unchama.seichistat.util.Util;



public class SeichiStat  extends JavaPlugin {

	public static SeichiStat plugin;

	//デバッグフラグ
	public static Boolean DEBUG = false;

	public static final String PLAYERDATA_TABLENAME = "playerdata";

	private HashMap<String, TabExecutor> commandlist;

	//起動するタスクリスト
	private List<BukkitTask> tasklist = new ArrayList<BukkitTask>();

	public Sql sql;
	public static Config config;

	//Playerdataに依存するデータリスト
	public static final HashMap<UUID,PlayerData> playermap = new HashMap<UUID,PlayerData>();

	@Override
	public void onEnable(){
		plugin = this;

		//コンフィグ系の設定は全てConfig.javaに移動
		config = new Config(this);
		config.loadConfig();

		//MySQL系の設定はすべてSql.javaに移動
		sql = new Sql(this,config.getURL(), config.getDB(), config.getID(), config.getPW());
		if(!sql.connect()){
			getLogger().info("データベース初期処理にエラーが発生しました");
		}

		//コマンドの登録
		commandlist = new HashMap<String, TabExecutor>();
		commandlist.put("seichistat",new seichistatCommand(plugin));

		//リスナーの登録
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerBlockBreakListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerBucketListener(), this);

		//オンラインの全てのプレイヤーを処理
		for(Player p : getServer().getOnlinePlayers()){
			//UUIDを取得
			UUID uuid = p.getUniqueId();
			//プレイヤーデータを生成
			PlayerData playerdata = sql.loadPlayerData(p);
			if(playerdata==null){
				p.sendMessage("playerdataの読み込みエラーです。管理者に報告してください。");
				continue;
			}
			//プレイヤーマップにプレイヤーを追加
			playermap.put(uuid,playerdata);
		}

		//タスクスタート
		startTaskRunnable();

		getLogger().info("SeichiStat is Enabled!");

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		return commandlist.get(cmd.getName()).onCommand(sender, cmd, label, args);
	}

	@Override
	public void onDisable(){
		//全てのタスクをキャンセル
		stopAllTaskRunnable();

		for(Player p : getServer().getOnlinePlayers()){
			//UUIDを取得
			UUID uuid = p.getUniqueId();
			//プレイヤーデータ取得
			PlayerData playerdata = playermap.get(uuid);
			//念のためエラー分岐
			if(playerdata == null){
				p.sendMessage(ChatColor.RED + "playerdataの保存に失敗しました。管理者に報告してください");
				getServer().getConsoleSender().sendMessage(ChatColor.RED + "SeichiAssist[Ondisable処理]でエラー発生");
				getLogger().warning(Util.getName(p)+ "のplayerdataの保存失敗。開発者に報告してください");
				continue;
			}
			//quit時とondisable時、プレイヤーデータを最新の状態に更新
			playerdata.UpdateonQuit(p);

			//mysqlに送信
			if(!sql.savePlayerData(playerdata)){
				getLogger().info(playerdata.name + "のデータ保存に失敗しました");
			}else{
				getServer().getConsoleSender().sendMessage(ChatColor.GREEN + p.getName() + "のプレイヤーデータ保存完了");
			}
			//ログインフラグ折る
			if(!sql.logoutPlayerData(playerdata)){
				getLogger().warning(playerdata.name + "のloginflag->false化に失敗しました");
			}else{
				getServer().getConsoleSender().sendMessage(ChatColor.GREEN + p.getName() + "のloginflag回収完了");
			}
		}

		if(!sql.disconnect()){
			getLogger().info("データベース切断に失敗しました");
		}
		getLogger().info("SeichiStat is Disabled!");
	}

	public void startTaskRunnable(){
		//一定時間おきに処理を実行するタスク
		if(DEBUG){
			tasklist.add(new MinuteTaskRunnable().runTaskTimer(this,0,300));
		}else{
			tasklist.add(new MinuteTaskRunnable().runTaskTimer(this,0,1200));
		}
	}

	public void stopAllTaskRunnable(){
		for(BukkitTask task:tasklist){
			task.cancel();
		}
	}
}

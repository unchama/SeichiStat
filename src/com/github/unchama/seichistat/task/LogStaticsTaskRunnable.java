package com.github.unchama.seichistat.task;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.Config;
import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.LogPlayerData;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

public class LogStaticsTaskRunnable extends BukkitRunnable{
	private SeichiStat plugin = SeichiStat.plugin;
	private HashMap<UUID, PlayerData> playermap = SeichiStat.playermap;
	private Config config = SeichiStat.config;

	//newインスタンスが立ち上がる際に変数を初期化したり代入したりする処理
	public LogStaticsTaskRunnable() {

	}

	@Override
	public void run() {
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("ログ出力処理開始");
		}

		for(Player p : plugin.getServer().getOnlinePlayers()){
			//オンラインプレイヤーの処理
			//playernameを取得
			String name = p.getName().toLowerCase();
			//UUIDを取得
			UUID uuid = p.getUniqueId();

			//map作成
			final HashMap<String,Integer> statmap = new HashMap<String,Integer>();

			//static内の数値をすべてmapに詰め込む
			for(Statistic statistic : Statistic.values()){
				//int n = p.getStatistic(org.bukkit.Statistic.valueOf("PLAY_ONE_TICK"));
				int n = -1;
				try{
					n = p.getStatistic(statistic);
				}catch(IllegalArgumentException e){}
				statmap.put(statistic.name(), n);
			}

			//mysqlへ送信
			new SendStaticsTaskRunnable(name,uuid,statmap).runTaskAsynchronously(plugin);


			//map作成
			final HashMap<String,Integer> minestatmap = new HashMap<String,Integer>();
			final HashMap<String,Integer> usestatmap = new HashMap<String,Integer>();
			//material
			for(Material material : Material.values()){
				int n = -1;
				try{
					n = p.getStatistic(Statistic.MINE_BLOCK, material);
				}catch(IllegalArgumentException e){}
				minestatmap.put(material.name(),n);

				//nの値初期化
				n = -1;
				try{
					n = p.getStatistic(Statistic.USE_ITEM, material);
				}catch(IllegalArgumentException e){}
				usestatmap.put(material.name(),n);
			}

			//mysqlへ送信
			new SendMineStaticsTaskRunnable(name,uuid,minestatmap,SeichiStat.STATICDATA_MINE_TABLENAME).runTaskAsynchronously(plugin);
			new SendMineStaticsTaskRunnable(name,uuid,usestatmap,SeichiStat.STATICDATA_USE_TABLENAME).runTaskAsynchronously(plugin);

			new SendLogPlayerDataTaskRunnable(new LogPlayerData(p)).runTaskAsynchronously(plugin);

		}

	}
}

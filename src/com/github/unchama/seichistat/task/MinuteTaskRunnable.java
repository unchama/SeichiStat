package com.github.unchama.seichistat.task;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.unchama.seichistat.Config;
import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

public class MinuteTaskRunnable extends BukkitRunnable{
	private SeichiStat plugin = SeichiStat.plugin;
	private HashMap<UUID, PlayerData> playermap = SeichiStat.playermap;
	private Config config = SeichiStat.config;

	//newインスタンスが立ち上がる際に変数を初期化したり代入したりする処理
	public MinuteTaskRunnable() {

	}

	@Override
	public void run() {
		//playermap = SeichiStat.playermap;
		//plugin = SeichiStat.plugin;
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("プレイヤーの１分間の処理を実行");
		}

		//playermapが空の時return
		if(playermap.isEmpty()){
			return;
		}
		for(Player p : plugin.getServer().getOnlinePlayers()){
			//プレイﾔｰが必ずオンラインと分かっている処理
			//UUIDを取得
			UUID uuid = p.getUniqueId();
			//プレイヤーデータ取得
			PlayerData playerdata = playermap.get(uuid);
			//念のためエラー分岐
			if(playerdata == null){
				Bukkit.getLogger().warning(p.getName() + " -> PlayerData not found.");
				Bukkit.getLogger().warning("MinuteTaskRunnable");
				continue;
			}
			//総プレイ時間更新
			playerdata.calcPlayTick(p);
		}

	}
}
package com.github.unchama.seichistat.task;


import java.util.HashMap;
import java.util.UUID;

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
		playermap = SeichiStat.playermap;
		plugin = SeichiStat.plugin;
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("プレイヤーの１分間の処理を実行");
		}

		//playermapが空の時return
		if(playermap.isEmpty()){
			return;
		}
		//プレイヤーマップに記録されているすべてのplayerdataについての処理
		for(PlayerData playerdata : playermap.values()){
			//プレイヤーのオンラインオフラインに関係なく処理

			//プレイヤーがオフラインの時処理を終了、次のプレイヤーへ
			if(playerdata.isOffline()){
				if(SeichiStat.DEBUG){
					Util.sendEveryMessage(playerdata.name + "は不在により処理中止");
				}
				continue;
			}
			//プレイﾔｰが必ずオンラインと分かっている処理
			//プレイヤーを取得
			Player player = plugin.getServer().getPlayer(playerdata.uuid);
			//プレイヤー名を取得
			String name = Util.getName(player);
			//総プレイ時間更新
			playerdata.calcPlayTick(player);



		}

	}
}
package com.github.unchama.seichistat.data;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.util.Util;


public class PlayerData {
	//プレイヤー名
	public String name;
	//UUID
	public UUID uuid;
	//保護ブロックの破壊試行回数
	public int num_rgbreak;
	//プレイ時間
	public int playtick;


	public PlayerData(Player player){
		//初期値を設定
		name = Util.getName(player);
		uuid = player.getUniqueId();
		num_rgbreak = 0;
		playtick = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
	}

	//オフラインかどうか
	public boolean isOffline() {
		return SeichiStat.plugin.getServer().getPlayer(uuid) == null;
	}
}

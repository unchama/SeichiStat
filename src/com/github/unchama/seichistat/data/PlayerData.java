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
	//プレイ時間差分計算用int
	public int servertick;
	//プレイ時間
	public int playtick;
	//マグマダバア回数
	public int num_magmadabaa;


	public PlayerData(Player player){
		//初期値を設定
		name = Util.getName(player);
		uuid = player.getUniqueId();
		num_rgbreak = 0;
		servertick = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
		playtick = 0;
		num_magmadabaa = 0;
	}

	//オフラインかどうか
	public boolean isOffline() {
		return SeichiStat.plugin.getServer().getPlayer(uuid) == null;
	}

	//総プレイ時間を更新する
	public void calcPlayTick(Player p){
		int getservertick = p.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
		int getincrease = getservertick - servertick;
		servertick = getservertick;
		if(SeichiStat.DEBUG){
			p.sendMessage("総プレイ時間に追加したtick:" + getincrease);
		}
		playtick += getincrease;
	}
	//quit時とondisable時、プレイヤーデータを最新の状態に更新
	public void UpdateonQuit(Player player){
		//総プレイ時間更新
		calcPlayTick(player);
	}
}

package com.github.unchama.seichistat.data;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.unchama.seichistat.util.Util;


public class PlayerData {
	//プレイヤー名
	public String name;
	//UUID
	public UUID uuid;
	public PlayerData(Player player){
		//初期値を設定
		name = Util.getName(player);
		uuid = player.getUniqueId();
	}
}

package com.github.unchama.seichistat.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;
import com.github.unchama.seichistat.data.PlayerData;

public class PlayerQuitListener implements Listener {
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;
	Sql sql = SeichiStat.plugin.sql;

	//プレイヤーがjoinした時に実行
	@EventHandler
	public void onplayerQuitEvent(PlayerQuitEvent event){
		//退出したplayerを取得
		Player player = event.getPlayer();
		//プレイヤーのuuidを取得
		UUID uuid = player.getUniqueId();
		//プレイヤーデータ取得
		PlayerData playerdata = playermap.get(uuid);
		if(!sql.savePlayerData(playerdata)){
			Bukkit.getLogger().info(playerdata.name + "のデータ保存に失敗しました。");
		}

	}

}

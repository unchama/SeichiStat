package com.github.unchama.seichistat.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;
import com.github.unchama.seichistat.data.PlayerData;

public class PlayerJoinListener implements Listener {
	private SeichiStat plugin = SeichiStat.plugin;
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;
	Sql sql = SeichiStat.plugin.sql;

	//プレイヤーがjoinした時に実行
	@EventHandler
	public void onplayerJoinEvent(PlayerJoinEvent event){
		//ジョインしたplayerを取得
		Player player = event.getPlayer();
		sql.loadPlayerData(player);
	}
}

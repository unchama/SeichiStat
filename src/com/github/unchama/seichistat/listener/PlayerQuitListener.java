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
	SeichiStat plugin = SeichiStat.plugin;
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
		//念のためエラー分岐
		if(playerdata == null){
			Bukkit.getLogger().warning(player.getName() + " -> PlayerData not found.");
			Bukkit.getLogger().warning("PlayerQuitListener");
			return;
		}

		//quit時とondisable時、プレイヤーデータを最新の状態に更新
		playerdata.UpdateonQuit(player);

		//saveplayerdata
		sql.saveQuitPlayerData(playerdata);

		//マルチサーバー対応の為の処理
		//不要なplayerdataを削除
		playermap.remove(uuid);

	}

}

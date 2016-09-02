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
import com.github.unchama.seichistat.util.Util;

public class PlayerJoinListener implements Listener {
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;
	Sql sql = SeichiStat.plugin.sql;

	//プレイヤーがjoinした時に実行
	@EventHandler
	public void onplayerJoinEvent(PlayerJoinEvent event){
		//ジョインしたplayerを取得
		Player player = event.getPlayer();
		//プレイヤーのuuidを取得
		UUID uuid = player.getUniqueId();
		//プレイヤーデータを宣言
		PlayerData playerdata = null;
		//ログインしたプレイヤーのデータが残っていなかった時にPlayerData作成
		if(!playermap.containsKey(uuid)){
			//新しいplayerdataを作成
			playerdata = sql.loadPlayerData(player);
			//playermapに追加
			playermap.put(uuid, playerdata);
		}else{
			playerdata = playermap.get(uuid);
			//もし名前変更されていたら
			if(!Util.getName(player).equals(playerdata.name)){
				//すでにあるプレイヤーデータの名前を更新しておく
				playerdata.name = Util.getName(player);
				playermap.put(uuid, playerdata);
			}
		}
	}
}

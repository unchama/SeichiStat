package com.github.unchama.seichistat.listener;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;


public class PlayerBlockBreakListener implements Listener {
	@EventHandler
	public void onPlayerWGEvent(BlockBreakEvent event){
		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//もしサバイバルでなければ処理を終了
		if(!player.getGameMode().equals(GameMode.SURVIVAL)){
			return;
		}
		//壊されるブロックを取得
		Block block = event.getBlock();
		//ブロックのタイプを取得
		Material material = block.getType();
		//UUIDを取得
		UUID uuid = player.getUniqueId();
		//UUIDを基にプレイヤーデータ取得
		PlayerData playerdata = SeichiStat.playermap.get(uuid);

		//壊されるブロックがワールドガード範囲だった場合数値をプラス1して処理を終了
		if(!Util.getWorldGuard().canBuild(player, block.getLocation())){
			playerdata.num_rgbreak ++;
 			if(SeichiStat.DEBUG){
 				player.sendMessage("num_rgblockの値:" + playerdata.num_rgbreak);
 			}
			return;
		}

	}

	@EventHandler
	public void onPlayerSeichiEvent(BlockBreakEvent event){
		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//もしサバイバルでなければ処理を終了
		if(!player.getGameMode().equals(GameMode.SURVIVAL)){
			return;
		}
		//整地ワールド外なら処理を終了
		if(!player.getWorld().getName().equals("world_SW")){
			return;
		}

	}
}

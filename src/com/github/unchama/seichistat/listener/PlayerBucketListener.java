package com.github.unchama.seichistat.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

public class PlayerBucketListener implements Listener {
	private SeichiStat plugin = SeichiStat.plugin;
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;

	//おんぷれいやーまぐまだばぁイベント改
	@EventHandler
	public void onPlayerActiveSkillToggleEvent(PlayerInteractEvent event){
		//プレイヤーを取得
		Player player = event.getPlayer();
		//プレイヤーの起こしたアクションの取得
		Action action = event.getAction();
		//アクションを起こした手を取得
		EquipmentSlot equipmentslot = event.getHand();

		if(action.equals(Action.RIGHT_CLICK_BLOCK)){

			if(player.getInventory().getItemInMainHand().getType().equals(Material.LAVA_BUCKET)){
				//メインハンドで指定ツールを持っていた時の処理

				//UUIDを取得
				UUID uuid = player.getUniqueId();
				//playerdataを取得
				PlayerData playerdata = playermap.get(uuid);
				//念のためエラー分岐
				if(playerdata == null){
					Bukkit.getLogger().warning(player.getName() + " -> PlayerData not found.");
					Bukkit.getLogger().warning("PlayerBucketListener");
					return;
				}
				playerdata.num_magmadabaa++;
				//プレイヤー座標を取得
				Location loc = player.getLocation();
				Util.sendAdminMessage(ChatColor.RED + player.getName() + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")付近で溶岩バケツを使用しました");
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + player.getName() + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")付近で溶岩バケツを使用しました");
				player.sendMessage(ChatColor.DARK_GRAY + "(β)マグマの設置を検知したゾ。ダバァしないようにネ");
				return;

			}
		}
	}







	//おんぷれいやーまぐまだばぁいべんと
	/*
	@EventHandler
	public void onPlayerMagmaDabaaEvent(PlayerBucketEvent event){
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("プレイヤーバケツイベントが呼び出された！");
		}

		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//もしサバイバルでなければ処理を終了
		if(!player.getGameMode().equals(GameMode.SURVIVAL)){
			return;
		}
		//プレイヤー名を取得
		String name = player.getName().toLowerCase();
		//プレイヤー座標を取得
		Location loc = player.getLocation();
		//バケツの中身を取得
		Material material = event.getBucket();
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("getBucketの結果は" + material);
		}
		switch(material){
		case LAVA:
			Util.sendAdminMessage(ChatColor.RED + name + "が(X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")付近にLAVAを設置しました");
			break;
		case STATIONARY_LAVA:
			Util.sendAdminMessage(ChatColor.RED + name + "が(X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")付近にSTATIONARY_LAVAを設置しました");
			break;
		default:
			break;
		}
	}
	*/
}

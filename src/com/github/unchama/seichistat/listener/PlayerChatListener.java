package com.github.unchama.seichistat.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;

public class PlayerChatListener implements Listener {
	private SeichiStat plugin = SeichiStat.plugin;
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event){
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("AsyncPlayerChatEventが呼び出された！");
		}

		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//UUIDを取得
		UUID uuid = player.getUniqueId();
		//playerdataを取得
		PlayerData playerdata = playermap.get(uuid);
		//念のためエラー分岐
		if(playerdata == null){
			player.sendMessage(ChatColor.RED + "playerdataがありません。管理者に報告してください");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "SeichiStat[チャット処理]でエラー発生");
			plugin.getLogger().warning(player.getName() + "のplayerdataがありません。開発者に報告してください");
			return;
		}
		//回数+1
		playerdata.num_chat++;
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("getformat:" + event.getFormat());
			Util.sendEveryMessage("getmessage" + event.getMessage());
			Util.sendEveryMessage(player.getName().toLowerCase() + "のnumchat:" + playerdata.num_chat);
		}
	}

	@EventHandler
	public void onPlayerCommandEvent(PlayerCommandPreprocessEvent event){
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("PlayerCommandPreprocessEventが呼び出された！");
		}

		//コマンド文取得
		String cmd = event.getMessage();
		if(cmd.equalsIgnoreCase("/spawn")
				|| cmd.equalsIgnoreCase("/home")
				|| cmd.equalsIgnoreCase("/hub")){
			return;
		}
		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//UUIDを取得
		UUID uuid = player.getUniqueId();
		//playerdataを取得
		PlayerData playerdata = playermap.get(uuid);
		//念のためエラー分岐
		if(playerdata == null){
			player.sendMessage(ChatColor.RED + "playerdataがありません。管理者に報告してください");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "SeichiStat[チャット処理]でエラー発生");
			plugin.getLogger().warning(player.getName() + "のplayerdataがありません。開発者に報告してください");
			return;
		}
		//回数+1
		playerdata.num_command++;
		Util.sendAdminMessage(ChatColor.DARK_GRAY + "COMMAND > " + player.getName() + ": " + cmd);
	}
}

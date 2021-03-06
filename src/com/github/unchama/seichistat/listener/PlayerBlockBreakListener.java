package com.github.unchama.seichistat.listener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.util.Util;
import com.sk89q.worldguard.LocalPlayer;


public class PlayerBlockBreakListener implements Listener {
	private SeichiStat plugin = SeichiStat.plugin;
	HashMap<UUID,PlayerData> playermap = SeichiStat.playermap;

	@EventHandler
	public void onPlayerWGEvent(BlockBreakEvent event){
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("ブロックブレイクイベントが呼び出された！");
		}
		//実行したプレイヤーを取得
		Player player = event.getPlayer();
		//もしサバイバルでなければ処理を終了
		//if(!player.getGameMode().equals(GameMode.SURVIVAL)){
		//	return;
		//}
		//プレイヤー名を取得
		String name = player.getName().toLowerCase();
		//壊されるブロックを取得
		Block block = event.getBlock();
		//ブロックのタイプを取得
		Material material = block.getType();
		//ブロックの座標を取得
		Location loc = block.getLocation();
		//UUIDを取得
		UUID uuid = player.getUniqueId();
		//UUIDを基にプレイヤーデータ取得
		PlayerData playerdata = playermap.get(uuid);
		//念のためエラー分岐
		if(playerdata == null){
			Bukkit.getLogger().warning(player.getName() + " -> PlayerData not found.");
			Bukkit.getLogger().warning("PlayerBlockBreakListener.onPlayerWGEvent");
			return;
		}

		//壊されるブロックがワールドガードによる建築不可範囲だった場合数値をプラス1して処理を終了
		//必ずしも保護が無いとは限らない(build=allowのところとかはこのif文はスルーする)
		if(!Util.getWorldGuard().canBuild(player, block.getLocation())){
			playerdata.num_rgbreak ++;
 			if(SeichiStat.DEBUG){
 				player.sendMessage("num_rgblockの値:" + playerdata.num_rgbreak);
 			}
			return;
		}
		LocalPlayer localPlayer = Util.getWorldGuard().wrapPlayer(player);
		/*
		Set<ProtectedRegion> protectedRegion = Util.getWorldGuard()
		.getRegionManager(player.getWorld())
		.getApplicableRegions(player.getLocation()).getRegions();
		*/

		//1個でも保護がかかってたらreturn
		if(Util.getWorldGuard()
		.getRegionManager(player.getWorld())
		.getApplicableRegions(player.getLocation()).size() > 0){
 			if(SeichiStat.DEBUG){
 				player.sendMessage(ChatColor.DARK_GRAY + "保護が1個以上かかってるのでreturn");
 			}
			return;
		}

		/*
		for(ProtectedRegion protectedRegion : Util.getWorldGuard()
				.getRegionManager(player.getWorld())
				.getApplicableRegions(player.getLocation()).getRegions()){
			/*
			 * ここでやりたいこと
			 * buildflagがallowなら判定しない
			 * 自身がOwnerの土地内では判定しない
			 *//*
			if(protectedRegion.isOwner(localPlayer)){
	 			if(SeichiStat.DEBUG){
	 				player.sendMessage(ChatColor.DARK_GRAY + "自身がownerの土地なのでreturn");
	 			}
				return;
			}
			if(protectedRegion.isMember(localPlayer)){
	 			if(SeichiStat.DEBUG){
	 				player.sendMessage(ChatColor.DARK_GRAY + "自身がmemberの土地なのでreturn");
	 			}
	 			return;
			}

			for(Object obj :protectedRegion.getFlags().values()){
				player.sendMessage(ChatColor.DARK_GRAY + obj.toString());
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + obj.toString());
			}

		}
		*/

		/*
		 * これより下、ワールドガードによる保護がないところでブロック破壊した時の処理
		 */

		//壊した場所のブロック設置破壊履歴を取得
		//Util.getCoreProtect().blockLookup(block, 1000);
		List<String[]> cresult = Util.getCoreProtect().blockLookup(block, 2592000);//604800秒=過去7日分,2592000=30日分

		//debug時、ブロックが破壊された地点に存在するcoreprotectのログを全出力する
		if(SeichiStat.DEBUG){
			player.sendMessage(ChatColor.RED + "表示...");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "表示...");
			if(cresult != null){
				for(String[] n : cresult){
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < n.length; i++) {
					buf.append(" " + n[i]);
					}
					plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "" + buf.toString());
					player.sendMessage(ChatColor.YELLOW + "" + buf.toString());
					//s = s + "," + buf.toString();
				}
			}
		}

		//他人の設置したブロック破壊を検出する部分ここから
		if(cresult != null){
			//新しいログから順に精査していくfor文
			for(String[] n : cresult){

				//useログはスルー
				if(n[7].equals("2")){
					continue;
				}

				//ロールバックされてたらスルー
				if(n[8].equals("1")){
					continue;
				}

				//"自分以外"が"設置"したログの場合に通知
				if(n[7].equals("1")
						&& !n[1].equalsIgnoreCase(name)){
					Util.sendAdminMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")で他人の設置したブロックを破壊しました");
					plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")で他人の設置したブロックを破壊しました");
					playerdata.num_cpbreak ++;
					//player.sendMessage(ChatColor.DARK_GRAY + "(β)他人が設置したブロックの破壊を検知したゾ");
				}
				//直前のログさえ確認できればこれ以上の詮索は必要ないのでbreak
				break;
			}
		}

	}

	//おんぷれいやーちぃたぁいべんと
	@EventHandler
	public void onPlayerCheaterEvent(BlockPlaceEvent event){
		if(SeichiStat.DEBUG){
			Util.sendEveryMessage("ブロックプレイスイベントが呼び出された！");
		}

		//実行したプレイヤーを取得
		Player player = event.getPlayer();

		//UUIDを取得
		UUID uuid = player.getUniqueId();
		//playerdataを取得
		PlayerData playerdata = playermap.get(uuid);
		//念のためエラー分岐
		if(playerdata == null){
			Bukkit.getLogger().warning(player.getName() + " -> PlayerData not found.");
			Bukkit.getLogger().warning("PlayerBlockBreakListener.onPlayerCheaterEvent");
			return;
		}

		//プレイヤー名を取得
		String name = player.getName().toLowerCase();

		//設置されるブロックを取得
		Block block = event.getBlock();
		//ブロックの座標を取得
		Location loc = block.getLocation();
		//ブロックのタイプを取得
		Material material = block.getType();
		switch(material){
		case LAVA:
			Util.sendAdminMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")にLAVAを直接設置しました");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")にLAVAを直接設置しました");
			playerdata.num_cheatdabaa++;
			break;
		case STATIONARY_LAVA:
			Util.sendAdminMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")にSTATIONARY_LAVAを直接設置しました");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + name + "が(" + player.getWorld().getName() + " X:" + loc.getBlockX() + "/Y:" + loc.getBlockY() + "/Z:" + loc.getBlockZ() + ")にSTATIONARY_LAVAを直接設置しました");
			playerdata.num_cheatdabaa++;
			break;
		default:
			break;
		}

	}


}


/*
//整地ワールド外なら処理を終了
if(!player.getWorld().getName().equals("world_SW")){
	return;
}
*/

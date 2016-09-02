package com.github.unchama.seichistat.util;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.unchama.seichistat.SeichiStat;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Util {
	//プレイヤーネームを格納（toLowerCaseで全て小文字にする。)
	public static String getName(Player p) {
		return p.getName().toLowerCase();
	}
	public static String getName(String name) {
		return name.toLowerCase();
	}
	public static double toDouble(String s){
		return Double.parseDouble(s);
	}
	public static int toInt(String s) {
		return Integer.parseInt(s);
	}

	public static void sendEveryMessage(String str){
		SeichiStat plugin = SeichiStat.plugin;
		for ( Player player : plugin.getServer().getOnlinePlayers() ) {
			player.sendMessage(str);
		}
	}

	public static CoreProtectAPI getCoreProtect() {
		Plugin plugin = SeichiStat.plugin.getServer().getPluginManager().getPlugin("CoreProtect");

		// Check that CoreProtect is loaded
		if (plugin == null || !(plugin instanceof CoreProtect)) {
		    return null;
		}

		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect)plugin).getAPI();
		if (CoreProtect.isEnabled()==false){
		    return null;
		}

		// Check that a compatible version of the API is loaded
		if (CoreProtect.APIVersion() < 4){
		    return null;
		}

		return CoreProtect;
	}
	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = SeichiStat.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }

	    return (WorldGuardPlugin) plugin;
	}
	public static WorldEditPlugin getWorldEdit() {
        Plugin pl = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(pl instanceof WorldEditPlugin)
            return (WorldEditPlugin)pl;
        else return null;
    }
}

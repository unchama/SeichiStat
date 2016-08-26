package com.github.unchama.seichistat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.unchama.seichistat.data.PlayerData;
import com.github.unchama.seichistat.listener.PlayerBlockBreakListener;



public class SeichiStat  extends JavaPlugin {

	public static SeichiStat plugin;

	//Playerdataに依存するデータリスト
	public static final HashMap<UUID,PlayerData> playermap = new HashMap<UUID,PlayerData>();

	@Override
	public void onEnable(){
		plugin = this;

		//リスナーの登録
		getServer().getPluginManager().registerEvents(new PlayerBlockBreakListener(), this);

	}
	@Override
	public void onDisable(){

	}
}

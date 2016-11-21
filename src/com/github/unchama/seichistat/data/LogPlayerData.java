package com.github.unchama.seichistat.data;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class LogPlayerData {
	public String name;
	public UUID uuid;

	public int all_drop;
	public int all_pickup;
	public int all_mine_block;//総ブロック破壊量
	public int all_use_item;//総ブロック設置量
	public int all_break_item;
	public int all_craft_item;
	//public int all_kill_entity;
	//public int all_entity_killed_by;
	public String nowplace_world;//どのワールドにいるか
	public int nowplace_x;
	public int nowplace_y;
	public int nowplace_z;

	public LogPlayerData(Player player){
		//playernameを取得
		name = player.getName().toLowerCase();
		//UUIDを取得
		uuid = player.getUniqueId();

		all_drop = 0;
		all_pickup = 0;
		all_mine_block = 0;
		all_use_item = 0;
		all_break_item = 0;
		all_craft_item = 0;
		//all_kill_entity = 0;
		//all_entity_killed_by = 0;

		for(Material material : Material.values()){

			try{
				all_drop += player.getStatistic(Statistic.DROP, material);
			}catch(IllegalArgumentException e){}

			try{
				all_pickup += player.getStatistic(Statistic.PICKUP, material);
			}catch(IllegalArgumentException e){}

			try{
				all_mine_block += player.getStatistic(Statistic.MINE_BLOCK, material);
			}catch(IllegalArgumentException e){}

			try{
				all_use_item += player.getStatistic(Statistic.USE_ITEM, material);
			}catch(IllegalArgumentException e){}

			try{
				all_break_item += player.getStatistic(Statistic.BREAK_ITEM, material);
			}catch(IllegalArgumentException e){}

			try{
				all_craft_item += player.getStatistic(Statistic.CRAFT_ITEM, material);
			}catch(IllegalArgumentException e){}

		}

		nowplace_world = player.getWorld().getName().toLowerCase();
		nowplace_x = player.getLocation().getBlockX();
		nowplace_y = player.getLocation().getBlockY();
		nowplace_z = player.getLocation().getBlockZ();
	}
}
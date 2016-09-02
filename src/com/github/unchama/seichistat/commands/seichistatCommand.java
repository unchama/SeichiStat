package com.github.unchama.seichistat.commands;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.github.unchama.seichistat.SeichiStat;
import com.github.unchama.seichistat.Sql;

public class seichistatCommand implements TabExecutor {
	SeichiStat plugin;
	Sql sql = SeichiStat.plugin.sql;

	public seichistatCommand(SeichiStat _plugin){
		plugin = _plugin;
	}
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1,
			String arg2, String[] arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if(args.length == 0){
			return false;

		}else if(args[0].equalsIgnoreCase("help")){

			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +"[コマンドリファレンス]");
			sender.sendMessage(ChatColor.RED + "/seichistat reload");
			sender.sendMessage("config.ymlの設定値を再読み込みします");
			sender.sendMessage(ChatColor.RED + "/seichistat debugmode");
			sender.sendMessage("デバッグモードのON,OFFを切り替えます");
			sender.sendMessage(ChatColor.RED + "/seichistat ?");
			sender.sendMessage("???");

			return true;

		}else if(args[0].equalsIgnoreCase("reload")){
			//gacha reload
			SeichiStat.config.reloadConfig();
			sender.sendMessage("config.ymlの設定値を再読み込みしました");
			return true;
		}else if(args[0].equalsIgnoreCase("debugmode")){
			//debugフラグ反転処理

			//メッセージフラグを反転
			SeichiStat.DEBUG = !SeichiStat.DEBUG;
			if (SeichiStat.DEBUG){
				sender.sendMessage(ChatColor.GREEN + "デバッグモードを有効にしました");
			}else{
				sender.sendMessage(ChatColor.GREEN + "デバッグモードを無効にしました");
			}
			plugin.stopAllTaskRunnable();
			plugin.startTaskRunnable();

			return true;
		}
		return false;
	}
}

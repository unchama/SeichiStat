package com.github.unchama.seichistat.util;

import org.bukkit.entity.Player;

public class Util {
	//プレイヤーネームを格納（toLowerCaseで全て小文字にする。)
	public static String getName(Player p) {
		return p.getName().toLowerCase();
	}
	public static String getName(String name) {
		return name.toLowerCase();
	}
}

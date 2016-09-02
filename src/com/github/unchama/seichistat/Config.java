package com.github.unchama.seichistat;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.unchama.seichistat.util.Util;

public class Config{
	private static FileConfiguration config;
	private SeichiStat plugin;

	//コンストラクタ
	Config(SeichiStat _plugin){
		plugin = _plugin;
		saveDefaultConfig();
	}

	//コンフィグのロード
	public void loadConfig(){
		config = getConfig();
	}

	//コンフィグのリロード
	public void reloadConfig(){
		plugin.reloadConfig();
		config = getConfig();
	}

	//コンフィグのセーブ
	public void saveConfig(){
		plugin.saveConfig();
	}

	//plugin.ymlがない時にDefaultのファイルを生成
	public void saveDefaultConfig(){
		plugin.saveDefaultConfig();
	}

	//plugin.ymlファイルからの読み込み
	public FileConfiguration getConfig(){
		return plugin.getConfig();
	}


	public double getMinuteMineSpeed(){
		return Util.toDouble(config.getString("minutespeedamount"));
	}
	public double getLoginPlayerMineSpeed(){
		return Util.toDouble(config.getString("onlineplayersamount"));
	}
	public int getGachaPresentInterval(){
		return Util.toInt(config.getString("presentinterval"));
	}
	public int getDefaultMineAmount(){
		return Util.toInt(config.getString("defaultmineamount"));
	}
	public int getDualBreaklevel(){
		return Util.toInt(config.getString("dualbreaklevel"));
	}
	public int getTrialBreaklevel(){
		return Util.toInt(config.getString("trialbreaklevel"));
	}
	public int getExplosionlevel(){
		return Util.toInt(config.getString("explosionlevel"));
	}
	public int getThunderStormlevel() {
		return Util.toInt(config.getString("thunderstormlevel"));
	}
	public int getBlizzardlevel() {
		return Util.toInt(config.getString("blizzardlevel"));
	}
	public int getMeteolevel() {
		return Util.toInt(config.getString("meteolevel"));
	}
	public int getGravitylevel() {
		return Util.toInt(config.getString("gravitylevel"));
	}

	public int getDropExplevel(int i){
		return Util.toInt(config.getString("dropexplevel" + i,""));
	}

	public int getPassivePortalInventorylevel() {
		return Util.toInt(config.getString("passiveportalinventorylevel"));
	}
	public int getDokodemoEnderlevel() {
		return Util.toInt(config.getString("dokodemoenderlevel"));
	}
	public int getMineStacklevel() {
		return Util.toInt(config.getString("minestacklevel"));
	}
	public String getDB(){
		return config.getString("db");
	}
	public String getTable() {
		return config.getString("table");
	}
	public String getID(){
		return config.getString("id");
	}
	public String getPW(){
		return config.getString("pw");
	}
	public String getURL(){
		String url = "jdbc:mysql://";
		url += config.getString("host");
		if(!config.getString("port").isEmpty()){
			url += ":" + config.getString("port");
		}
		return url;
	}

	public String getLvMessage(int i) {
		return config.getString("lv" + i + "message","");
	}


}
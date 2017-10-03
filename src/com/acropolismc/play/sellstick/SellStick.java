package com.acropolismc.play.sellstick;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.acropolismc.play.sellstick.Configs.PriceConfig;
import com.acropolismc.play.sellstick.Configs.StickConfig;

import net.milkbowl.vault.economy.Economy;

// @author shmkane
public class SellStick extends JavaPlugin {

	private static Economy econ = null;
	private static final Logger log = Logger.getLogger("Minecraft");

	public boolean mcore = false;
	public boolean legacy = false;
	public boolean facs = false;

	public void onEnable() {
		// Commands
		this.getCommand("sellstick").setExecutor(new SellStickCommand(this));
		// Listeners
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		// Safe the default config
		this.saveDefaultConfig();
		// Setup the other configs.
		StickConfig.instance.setup(getDataFolder());
		PriceConfig.instance.setup(getDataFolder());
		// Vault
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Plugin factions = getServer().getPluginManager().getPlugin("Factions");
		Plugin mcore = getServer().getPluginManager().getPlugin("MassiveCore");
		Plugin legacy = getServer().getPluginManager().getPlugin("LegacyFactions");

		if (mcore != null && mcore.isEnabled()) {
			this.mcore = true;
			log.info("[Sellstick] MCore Factions found! Hooking into MCore");
		}
		else if (legacy != null && legacy.isEnabled()) {
			this.legacy = true;
			log.info("[Sellstick] Legacy Factions found! Hooking into Legacy");
		}
		else if (factions != null && factions.isEnabled()) {
			this.facs = true;
			log.info("[Sellstick] UUID/Other Factions found! Hooking into Factions");
		}
		else {
			log.warning("[SellStick] FACTIONS WAS NOT FOUND! SOME FEATURES MAY NOT WORK!");
			log.warning("SellStick is designed to work with factions so you may encounter");
			log.warning("bugs when not using. I highly suggest using");
			log.warning("Factions UUID / Legacy Factions / MassiveCore Factions");


		}
		// if(factions.isEnabled()) {
		// if(mcore != null){
		// this.mcore = true;
		// System.out.println("[Sellstick] Factions Massive Core found");
		//
		// }else{
		// this.mcore = false;
		// System.out.println("[Sellstick] FactionsUUID or similar found");
		//
		// }
		// }

	}

	public Economy getEcon() {
		// Return instance of economy.
		return SellStick.econ;
	}

	// Setup Vault
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
}
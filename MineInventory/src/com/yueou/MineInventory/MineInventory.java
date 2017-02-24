package com.yueou.MineInventory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class MineInventory extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	private File files = new File("plugins" + File.separatorChar
			+ "MineInventory");
	private File configFile = new File(getDataFolder(), "config.yml");

	private MineInventoryConfigReader reader;

	private MineInventoryDataBaser databaseHandler;

	private MineInventoryCommandExecutor executor;

	private MineInventoryHash mineinventorymap;
	private MineInventoryListener mineinventorylistener;
	private static Permission permHandler;
	private MineInventoryCommandHash commandmap;
	private MineInventoryChannel channel;
	private Economy economy;
	public static MineInventory instance;
	public static YamlConfiguration config = null;

	public void onEnable() {
		instance = this;
		if (!(configFile.exists())) {
			log.info("[MineInventory] Config file does not exist,created.");
			createFile(configFile, "config.yml");
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		this.reader = new MineInventoryConfigReader();

		this.databaseHandler = new MineInventoryDataBaser(
				this.reader.getHostname(), this.reader.getDataBase(),
				this.reader.getUsername(), this.reader.getPassword(),
				this.reader.getPort(), this.reader.getPrefix(), this);

		this.mineinventorymap = new MineInventoryHash(this);

		this.executor = new MineInventoryCommandExecutor(this);

		this.mineinventorylistener = new MineInventoryListener(this);

		this.commandmap = new MineInventoryCommandHash();

		this.channel = new MineInventoryChannel(this);

		setupEconomy();
		setupPerms();

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.mineinventorylistener, this);

		getCommand("mi").setExecutor(this.executor);

		PluginDescriptionFile pdfFile = getDescription();

		this.log.info("MineInventory v" + pdfFile.getVersion()
				+ " by yueou loaded.");
	}

	public void onDisable() {
		getMap().saveAll();
		this.log.info("[MineInventory]MineInventory disabled");
	}

	public void onReload() {
		if (!this.configFile.exists()) {
			this.log.info("[MineInventory] Config file does not exist,created.");
			createFile(configFile, "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		this.mineinventorymap.saveAndRemoveAll();

		this.channel = new MineInventoryChannel(this);
		this.reader = new MineInventoryConfigReader();
		this.databaseHandler = new MineInventoryDataBaser(
				this.reader.getHostname(), this.reader.getDataBase(),
				this.reader.getUsername(), this.reader.getPassword(),
				this.reader.getPort(), this.reader.getPrefix(), this);
		this.mineinventorymap = new MineInventoryHash(this);
		this.executor = new MineInventoryCommandExecutor(this);
		this.mineinventorylistener = new MineInventoryListener(this);
		this.commandmap = new MineInventoryCommandHash();

		setupPerms();
		setupEconomy();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.mineinventorylistener, this);
		getCommand("mi").setExecutor(this.executor);
		PluginDescriptionFile pdfFile = getDescription();
		loadAllInventory();
		this.log.info("MineInventory v" + pdfFile.getVersion()
				+ " by yueou reloaded.");
	}

	private void createFile(File file, String source) {
		this.files.mkdirs();

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream is = getClass().getResourceAsStream("/" + source);
		BufferedInputStream buffIn = new BufferedInputStream(is);

		BufferedOutputStream bufOut = null;

		try {
			bufOut = new BufferedOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		byte[] inByte = new byte[4096];
		int count = -1;

		try {
			while ((count = buffIn.read(inByte)) != -1) {
				bufOut.write(inByte, 0, count);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			bufOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			buffIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean setupPerms() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(Permission.class);
		if (permissionProvider != null) {
			permHandler = (Permission) permissionProvider.getProvider();
		}
		return permHandler != null;
	}

	private boolean setupEconomy() {
		this.log.info("[MineInventory] Loading economy plugins...");
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			this.economy = ((Economy) economyProvider.getProvider());
			this.log.fine("[MineInventory] Successed to get Vault economy instance!");
		} else {
			this.log.warning("[MineInventory] Failed to get Vault economy instance!");
		}

		return this.economy != null;
	}

	public MineInventoryHash getMap() {
		return this.mineinventorymap;
	}

	public String registerInventory(String player, MineInventoryInventory inv,
			int amount) {
		if (hasInventory(player))
			return null;
		this.databaseHandler.registerInventory(player);
		String invdata = "";
		int stackid = 0;
		for (int i = 0; i < amount; i++) {
			stackid = this.databaseHandler.addItemStack();
			inv.addStackIDs(stackid);
			invdata = invdata + stackid;
			invdata = invdata + ";";
		}

		updateInventory(player, "ItemData = '" + invdata + "'");

		return invdata;
	}

	public String levelUpInventory(String player) {
		MineInventoryInventory inv = this.mineinventorymap.getInventory(player);

		String invdata = "";
		List<Integer> stackids = inv.getStackIDs();
		for (Iterator<Integer> localIterator = stackids.iterator(); localIterator
				.hasNext();) {
			int t = ((Integer) localIterator.next()).intValue();
			invdata = invdata + t;
			invdata = invdata + ";";
		}

		if (inv.getInventory().getSize() == 54) {
			return invdata;
		}
		inv.levelUp();

		int stackid = 0;
		for (int i = 0; i < 9; i++) {
			stackid = this.databaseHandler.addItemStack();
			inv.addStackIDs(stackid);
			invdata = invdata + stackid;
			invdata = invdata + ";";
		}

		updateInventory(player, "Amount = " + inv.getInventory().getSize()
				+ " , ItemData = '" + invdata + "'");

		// this.mineinventorymap.saveInventory(player);

		return invdata;
	}

	public String levelUpInventoryToLargest(String player) {
		MineInventoryInventory inv = this.mineinventorymap.getInventory(player);

		int oldsize = inv.getInventory().getSize();
		String invdata = "";
		List<Integer> stackids = inv.getStackIDs();
		for (Iterator<Integer> localIterator = stackids.iterator(); localIterator
				.hasNext();) {
			int t = ((Integer) localIterator.next()).intValue();

			invdata = invdata + t;
			invdata = invdata + ";";
		}

		if (inv.getInventory().getSize() == 54) {
			return invdata;
		}
		inv.levelUpToLargest();

		int stackid = 0;
		for (int i = 0; i < 54 - oldsize; i++) {
			stackid = this.databaseHandler.addItemStack();
			inv.addStackIDs(stackid);
			invdata = invdata + stackid;
			invdata = invdata + ";";
		}

		updateInventory(player, "ItemData = '" + invdata + "'");

		// this.mineinventorymap.saveInventory(player);

		return invdata;
	}

	public boolean hasInventory(String player) {
		ResultSet res = this.databaseHandler.getInventory(player);
		try {
			if (res.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateInventory(String player, String updatestr) {
		if (!hasInventory(player))
			return;
		this.databaseHandler.updateInventory(player, updatestr);
	}

	public boolean removeInventory(String player) {
		if (!hasInventory(player)) {
			return false;
		}
		this.databaseHandler.removeInventory(player);

		List<Integer> stackids = this.mineinventorymap.getInventory(player)
				.getStackIDs();

		for (Iterator<Integer> localIterator = stackids.iterator(); localIterator
				.hasNext();) {
			int i = ((Integer) localIterator.next()).intValue();

			this.databaseHandler.removeItemStack(String.valueOf(i));
		}
		this.mineinventorymap.removeInventory(player);
		return true;
	}

	public ResultSet getInventory(String player) {
		ResultSet res = this.databaseHandler.getInventory(player);
		try {
			if (!res.next()) {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public ResultSet getItemStack(int id) {
		ResultSet res = null;

		String sid = String.valueOf(id);
		res = this.databaseHandler.getItemStack(sid);

		return res;
	}

	public void updateItemStack(int id, String updatestr) {
		String sid = String.valueOf(id);
		this.databaseHandler.updateItemstack(sid, updatestr);
	}

	public Permission getPermissionHandler() {
		return permHandler;
	}

	public Economy getEconomy() {
		return this.economy;
	}

	public MineInventoryConfigReader getreader() {
		return this.reader;
	}

	public MineInventoryCommandHash getCommandMap() {
		return this.commandmap;
	}

	public void loadAllInventory() {
		Iterator<? extends Player> players = getServer().getOnlinePlayers()
				.iterator();

		while (players.hasNext()) {
			loadInventory(players.next().getName());
		}
	}

	public MineInventoryInventory loadInventory(String player) {
		if (isDebug()) {
			System.out.println("[MineInventory]Loading inventory of " + player
					+ ",");
		}
		int inventorynumber = 0;
		String inventorydata = null;
		int[] tool = new int[15];

		MineInventoryInventory minv = null;

		ResultSet result = getInventory(player);
		if (result == null) {
			if (isDebug())
				System.out.println("[MineInventory]Get inventory of " + player
						+ " from database failed");
			return null;
		}
		try {
			if (isDebug()) {
				System.out.println("[MineInventory]Get inventory of " + player
						+ " from database successed, loading....");
			}
			inventorynumber = result.getShort("Amount");
			inventorydata = result.getString("ItemData");
			tool[0] = result.getShort("Pre2");

			int locked = result.getInt("Locked");
			minv = this.mineinventorymap.loadInventory(player.toLowerCase(),
					inventorynumber, inventorydata, locked, tool);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (isDebug()) {
			System.out.println("[MineInventory] inventory of " + player
					+ " loaded.");
		}
		return minv;
	}

	public MineInventoryChannel getChannel() {
		return this.channel;
	}

	public boolean isDebug() {
		return this.reader.isDebug();
	}
}

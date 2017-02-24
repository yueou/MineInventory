package com.yueou.MineInventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MineInventoryDataBaser {
	private static Connection conn = null;

	private static String host;
	private static String port;
	private static String db;
	private static String dbuser;
	private static String dbpw;
	private static String prefix;
	private static MineInventory plugin;
	private static String selectInventory = "Select * From %prefix%PlayerInventories Where Player = '%player%';";
	private static String selectAllInventory = "Select * From %prefix%PlayerInventories;";
	private static String createInventory = "Insert Into %prefix%PlayerInventories (Player) values ('%player%');";
	private static String updateInventory = "Update %prefix%PlayerInventories set %update% where Player = '%player%';";
	private static String deleteInventory = "Delete From %prefix%PlayerInventories where Player = '%player%'";

	private static String selectItemStack = "Select * From %prefix%ItemStacks Where ID = %stackid%;";
	private static String selectAllItemStack = "Select * From %prefix%ItemStacks order by ID ASC;";
	private static String addItemStack = "Insert Into %prefix%ItemStacks (ID, DURABILITY, ENCHANTDATA, STOREDENCHANT) values (%stackid%, 0, '', '')";
	private static String updateItemStack = "Update %prefix%ItemStacks set %update% where ID = %stackid%";
	private static String deleteItemStack = "Delete From %prefix%ItemStacks where  ID = %stackid%";

	public MineInventoryDataBaser(String phost, String database, String puser,
			String ppw, String pport, String pprefix, MineInventory pplugin) {
		host = phost;
		dbuser = puser;
		db = database;
		dbpw = ppw;
		port = pport;
		prefix = pprefix;

		plugin = pplugin;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			DriverManager.setLoginTimeout(3);
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":"
					+ port + "/" + db + "?" + "user=" + dbuser + "&"
					+ "password=" + dbpw);
		} catch (ClassNotFoundException e) {
			System.out.println("[MineInvnetory]: 没有找到数据库");
			plugin.onDisable();
			return;
		} catch (java.sql.SQLException e) {
			System.out.println("[MineInventory]: 连接数据库发生错误,请检查你的配置文件");
			plugin.onDisable();
			return;
		}
		createTable();

		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				MineInventoryDataBaser.this.checkConnection();
				MineInventoryDataBaser.this.holdConnection();
			}
		}, 60L, 72000L);
	}

	public void createTable() {
		if (conn != null) {
			checkConnection();
			try {
				Statement query = conn.createStatement();
				String sql = "Show Tables like '" + prefix
						+ "PlayerInventories';";
				java.sql.ResultSet result = query.executeQuery(sql);
				if (result.next()) {
					return;
				}

				String sqlcre = "Create Table "
						+ prefix
						+ "PlayerInventories (ID Integer Primary Key NOT NULL AUTO_INCREMENT, "
						+ "Player Varchar (20) NOT NULL, "
						+ "Amount Tinyint DEFAULT 9, " +

						"Locked Tinyint DEFAULT 0, "
						+ "Pre2 Tinyint DEFAULT 0, "
						+ "ItemData Varchar(800) DEFAULT '0');";
				query.executeUpdate(sqlcre);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
			try {
				Statement query = conn.createStatement();
				String sql = "Show Tables like '" + prefix + "ItemStacks';";
				java.sql.ResultSet result = query.executeQuery(sql);
				if (result.next()) {
					return;
				}

				String sqlcre = "Create Table " + prefix
						+ "ItemStacks (ID Integer Primary Key NOT NULL, "
						+ "ItemType Integer DEFAULT 0, "
						+ "SubType Integer DEFAULT 0, "
						+ "DisplayName varchar(100) DEFAULT '', "
						+ "Lore varchar(200) DEFAULT '', "
						+ "Amount Tinyint DEFAULT 0, "
						+ "HeadOwner varchar(30) DEFAULT '', "
						+ "Durability Integer DEFAULT 1, "
						+ "Enchanted Tinyint DEFAULT 0, "
						+ "EnchantData Varchar(100) DEFAULT '0', "
						+ "StoredEnchant Varchar(500) DEFAULT '0', "
						+ "BookTitle Varchar(200) DEFAULT '', "
						+ "BookAuthor Varchar(30) DEFAULT '', "
						+ "BookPageData Varchar(1000) DEFAULT '');";
				query.executeUpdate(sqlcre);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public java.sql.ResultSet executeQuery(String sql) {
		checkConnection();

		java.sql.ResultSet result = null;
		sql = sql.replace("%prefix%", prefix);
		try {
			Statement query = conn.createStatement();
			result = query.executeQuery(sql);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int updateQuery(String sql) {
		checkConnection();

		int result = 0;
		sql = sql.replace("%prefix%", prefix);
		try {
			Statement query = conn.createStatement();
			result = query.executeUpdate(sql);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void checkConnection() {
		try {
			if (conn.isClosed()) {
				conn = null;
				conn = DriverManager.getConnection("jdbc:mysql://" + host + ":"
						+ port + "/" + db + "?" + "user=" + dbuser + "&"
						+ "password=" + dbpw);
			}
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	private void holdConnection() {
		String sql = selectAllInventory;
		sql = sql.replace("%prefix%", prefix);
		try {
			Statement query = conn.createStatement();
			query.executeQuery(sql);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean registerInventory(String player) {
		int result = 0;

		String sql = createInventory;
		sql = sql.replace("%player%", player);

		result = updateQuery(sql);

		if (result != 0) {
			return true;
		}
		return false;
	}

	public java.sql.ResultSet getAll() {
		String sql = selectAllInventory;
		java.sql.ResultSet result = executeQuery(sql);

		return result;
	}

	public java.sql.ResultSet getInventory(String player) {
		String sql = selectInventory;
		sql = sql.replace("%player%", player);
		java.sql.ResultSet result = executeQuery(sql);

		return result;
	}

	public boolean updateInventory(String player, String updatestr) {
		int result = 0;

		String sql = updateInventory;
		sql = sql.replace("%player%", player);
		sql = sql.replace("%update%", updatestr);
		result = updateQuery(sql);

		if (result != 0) {
			return true;
		}
		return false;
	}

	public boolean removeInventory(String player) {
		int result = 0;

		String sql = deleteInventory;
		sql = sql.replace("%player%", player);
		result = updateQuery(sql);

		if (result != 0) {
			return true;
		}
		return false;
	}

	public int addItemStack() {
		int id = getLastItemStackID() + 1;

		String sql = addItemStack;
		sql = sql.replace("%stackid%", id + "");
		int result = updateQuery(sql);
		if (result != 0)
			return id;
		return 0;
	}

	public java.sql.ResultSet getItemStack(String id) {
		String sql = selectItemStack;
		sql = sql.replace("%stackid%", id);
		java.sql.ResultSet result = executeQuery(sql);

		return result;
	}

	public boolean updateItemstack(String id, String updatestr) {
		int result = 0;

		String sql = updateItemStack;
		sql = sql.replace("%stackid%", id);
		sql = sql.replace("%update%", updatestr);
		result = updateQuery(sql);

		if (result != 0) {
			return true;
		}
		return false;
	}

	public boolean removeItemStack(String id) {
		int result = 0;

		String sql = deleteItemStack;
		sql = sql.replace("%stackid%", id);
		result = updateQuery(sql);

		if (result != 0) {
			return true;
		}
		return false;
	}

	public int getLastItemStackID() {
		String sql = selectAllItemStack;

		java.sql.ResultSet result = executeQuery(sql);
		if (result == null) {
			return 0;
		}
		try {
			if (result.next()) {
				result.last();
				return result.getInt(1);
			}
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

package com.yueou.MineInventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

public class MineInventoryHash {
	private ArrayList<String> playerlist;
	private HashMap<String, MineInventoryInventory> inventmap;
	private MineInventory plugin;

	public MineInventoryHash(MineInventory plugin) {
		this.plugin = plugin;
		this.inventmap = new HashMap();
		this.playerlist = new ArrayList();
	}

	public MineInventoryInventory addInventory(String player, int size) {
		MineInventoryInventory minv = new MineInventoryInventory(
				player.toLowerCase(), size);
		this.inventmap.put(player.toLowerCase(), minv);
		this.playerlist.add(player.toLowerCase());

		return minv;
	}

	public boolean addInventory(String playername,
			MineInventoryInventory inventory) {
		this.playerlist.add(playername.toLowerCase());
		this.inventmap.put(playername.toLowerCase(), inventory);

		return true;
	}

	public boolean loadItemStack(int id, int pos, MineInventoryInventory inv) {
		java.sql.ResultSet res = this.plugin.getItemStack(id);

		org.bukkit.inventory.ItemStack item = null;
		List<String> lore = null;

		ItemMeta meta = null;

		try {
			if (res.next()) {
				int itemid = res.getInt("ItemType");
				int itemSubid = res.getInt("SubType");
				
				item = new org.bukkit.inventory.ItemStack(itemid);
				item.getData().setData(res.getByte("SubType"));
				String dname = res.getString("DisplayName");
				meta = item.getItemMeta();

				item.setAmount(res.getInt("Amount"));
				item.setDurability(res.getShort("Durability"));

				if (!dname.equals("")) {
					meta.setDisplayName(dname);
				}
				String lores = res.getString("Lore");
				if (!lores.equals("")) {
					lore = new ArrayList<String>();
					String[] loress = lores.split("%return%");
					int len = loress.length;
					for (int i = 0; i < len; i++) {
						lore.add(loress[i]);
					}
					meta.setLore(lore);
				}

				if (itemid == 397 && itemSubid == 3) {
					String head = res.getString("HeadOwner");
					
					if(head.length() != 0)
						((SkullMeta) meta).setOwner(head);
				}

				if (itemid == 387) {
					String title = res.getString("BookTitle");
					String author = res.getString("BookAuthor");
					String pages = res.getString("BookPageData");
					String[] pagess = pages.split("%rp%");

					((BookMeta) meta).setTitle(title);
					((BookMeta) meta).setAuthor(author);
					((BookMeta) meta).addPage(pagess);
				}

				String senchant = res.getString("StoredEnchant");
				if ((itemid == 403) && (!senchant.equals(""))) {
					String[] se = senchant.split(";");
					int len = se.length;

					for (int i = 0; i < len; i += 2) {
						((EnchantmentStorageMeta) meta).addStoredEnchant(
								Enchantment.getByName(se[i]),
								Integer.parseInt(se[(i + 1)]), true);
					}
				}

				item.setItemMeta(meta);

				if (res.getInt("Enchanted") != 0) {
					String enchant = res.getString("EnchantData");
					String[] enchantdata = enchant.split(";");
					int len = enchantdata.length;
					for (int i = 0; i < len; i += 2) {
						int enchantid = Integer.parseInt(enchantdata[i]);
						int enchantlv = Integer.parseInt(enchantdata[(i + 1)]);
						item.addUnsafeEnchantment(
								Enchantment.getById(enchantid), enchantlv);
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		inv.getInventory().setItem(pos, item);

		return true;
	}

	public MineInventoryInventory loadInventory(String playerName, int size,
			String data, int locked, int[] tool) {
		MineInventoryInventory minv = new MineInventoryInventory(playerName,
				size);
		minv.setTochest(true);
		minv.setDrop(true);
		minv.setSort(true);
		minv.setSend(true);
		minv.setShift(true);
		if (locked == 1)
			minv.lockInventory(true);
		if (tool[0] == 1) {
			minv.Shift();
		}
		if ((data == null) || (data == "")) {
			if (this.plugin.isDebug()) {
				System.out
						.println("[MineInventory]Not found item stack ids, exit....");
			}
			this.plugin.removeInventory(playerName.toLowerCase());
			return null;
		}
		String[] itemidset = data.split(";");
		int len = itemidset.length;

		if (this.plugin.isDebug())
			System.out.println("[MineInventory]loading item stacks....");
		for (int i = 0; i < len; i++) {
			int id = Integer.parseInt(itemidset[i]);
			minv.addStackIDs(id);
			loadItemStack(id, i, minv);
		}

		addInventory(playerName.toLowerCase(), minv);

		if (this.plugin.isDebug()) {
			System.out.println("[MineInventory]InventoryLoaded....");
		}
		return minv;
	}

	public MineInventoryInventory getInventory(String playername) {
		MineInventoryInventory minv = (MineInventoryInventory) this.inventmap
				.get(playername.toLowerCase());
		return minv;
	}

	public MineInventoryInventory getInventory(Player player) {
		MineInventoryInventory minv = (MineInventoryInventory) this.inventmap
				.get(player.getName().toLowerCase());
		return minv;
	}

	public boolean removeInventory(String playername) {
		this.inventmap.remove(playername.toLowerCase());
		this.playerlist.remove(playername.toLowerCase());

		return true;
	}

	public static String getInventoryInfo(Inventory inv) {
		String invinfo = "";
		ListIterator<org.bukkit.inventory.ItemStack> invlist = inv.iterator();

		while (invlist.hasNext()) {
			org.bukkit.inventory.ItemStack is = (org.bukkit.inventory.ItemStack) invlist
					.next();
			if (is == null) {
				if (invinfo.compareTo("") == 0) {
					invinfo = invinfo + "0:0";
				} else {
					invinfo = invinfo + ":0:0";
				}
			} else {
				int id = is.getTypeId();
				String isdata = id + "";
				String enchantdatas = "";
				if (((id >= 256) && (id <= 258))
						|| ((id >= 267) && (id <= 279))
						|| ((id >= 283) && (id <= 286))
						|| ((id >= 290) && (id <= 294))
						|| ((id >= 298) && (id <= 317))) {
					java.util.Map<Enchantment, Integer> isec = is
							.getEnchantments();
					Object[] isecset = isec.keySet().toArray();
					for (Object e : isecset) {
						int level = is.getEnchantmentLevel((Enchantment) e);
						int enid = ((Enchantment) e).getId();
						if (enchantdatas.equals(";")) {
							enchantdatas = ";" + enid + ";" + level;
						} else
							enchantdatas = enchantdatas + ";" + enid + ";"
									+ level;
					}
					isdata = isdata + enchantdatas;
					isdata = isdata + ";" + is.getDurability();
				}

				if (invinfo.compareTo("") == 0) {
					invinfo = invinfo + isdata + ":" + is.getAmount();
				} else {
					invinfo = invinfo + ":" + isdata + ":" + is.getAmount();
				}
			}
		}
		return invinfo;
	}

	@SuppressWarnings("unchecked")
	public void saveItemStack(ItemStack item, int id) {
		int type = 0;
		Byte subtype = Byte.valueOf((byte) 0);
		String display = "";

		String lore = "";
		String head = "";
		int amount = 0;
		int dura = 0;
		int enchanted = 0;
		String enchantdata = "";
		ImmutableMap<String, Integer> bookenchants = null;
		String bookenchantdata = "";
		String other = "";
		String author = "";
		String title = "";
		ArrayList<String> al = null;
		String updatestr = "";
		Object[] isecset;
		if (item != null) {
			type = item.getTypeId();
			subtype = Byte.valueOf(item.getData().getData());
			amount = item.getAmount();
			dura = item.getDurability();

			if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				java.util.Map<String, Object> map = meta.serialize();

				if (meta.hasDisplayName())
					display = meta.getDisplayName();
				if (meta.hasLore()) {
					List<String> lores = meta.getLore();
					int len = lores.size();
					for (int i = 0; i < len; i++) {
						lore = lore + (String) lores.get(i);
						if (i != len - 1)
							lore = lore + "%return%";
					}
				}

				if (meta.hasEnchants()) {
					enchanted = 1;
					java.util.Map<Enchantment, Integer> isec = item
							.getEnchantments();
					isecset = isec.keySet().toArray();

					for (int i = 0; i < isecset.length; i++) {

						Enchantment e = (Enchantment) isecset[i];
						int level = item.getEnchantmentLevel(e);
						int enid = (e).getId();
						if (enchantdata.equals("")) {
							enchantdata = enid + ";" + level;
						} else {
							enchantdata = enchantdata + ";" + enid + ";"
									+ level;
						}
					}
				}
				if (map.containsKey("skull-owner")) {
					head = (String) map.get("skull-owner");
				}
				if (map.containsKey("title")) {
					title = (String) map.get("title");
				}
				if (map.containsKey("author")) {
					author = (String) map.get("author");
				}
				if (map.containsKey("pages")) {
					al = (ArrayList<String>) map.get("pages");
					for (String temp : al) {
						other = other + temp;
						other = other + "%rp%";
					}
				}
				if (map.containsKey("stored-enchants")) {
					bookenchants = (ImmutableMap) map.get("stored-enchants");
					isecset = bookenchants.keySet().toArray();
					String e;
					for (int i = 0; i < isecset.length; i++) {

						e = (String) isecset[i];

						if (bookenchantdata.equals("")) {
							bookenchantdata = e + ";" + bookenchants.get(e);
						} else {
							bookenchantdata = bookenchantdata + ";" + e + ";"
									+ bookenchants.get(e);
						}
					}
				}
			}
		}

		updatestr = "ItemType = " + type + "," + "SubType = " + subtype + ","
				+ "DisplayName = '" + display + "'," + "Lore = '" + lore + "',"
				+ "HeadOwner = '" + head + "'," + "Amount =" + amount + ","
				+ "Durability =" + dura + "," + "Enchanted =" + enchanted + ","
				+ "EnchantData = '" + enchantdata + "'," + "StoredEnchant = '"
				+ bookenchantdata + "'," + "BookTitle = '" + title + "',"
				+ "BookAuthor = '" + author + "'," + "BookPageData = '" + other
				+ "'";

		this.plugin.updateItemStack(id, updatestr);

		if ((this.plugin.isDebug()) && (item != null)) {
			System.out.println(item.toString());

			Object[] objs = item.getItemMeta().serialize().keySet().toArray();

			for (int i = 0; i < objs.length; i++) {
				String str = (String) objs[i];

				System.out.println((String) str);
			}
		}
	}

	public void saveInventory(MineInventoryInventory inv) {
		int locked = 0;
		org.bukkit.inventory.ItemStack item = null;

		if (inv.locked()) {
			locked = 1;
		}

		ListIterator<org.bukkit.inventory.ItemStack> list = inv.getItems();
		List<Integer> idlist = inv.getStackIDs();

		for (int i = 0; i < idlist.size(); i++) {
			if (list.hasNext()) {
				item = (org.bukkit.inventory.ItemStack) list.next();
			} else {
				item = null;
			}
			saveItemStack(item, ((Integer) idlist.get(i)).intValue());
		}

		Inventory psaveinventory = inv.getInventory();

		String updatestr = "Amount = " + psaveinventory.getSize()
				+ ", Locked = " + locked;

		this.plugin.updateInventory(inv.getOwnerName(), updatestr);

		if (this.plugin.isDebug()) {
			System.out.println("[MineInventory]: Inventory of "
					+ inv.getOwnerName() + " saved.");
		}
	}

	public void saveInventory(String playername) {
		MineInventoryInventory inv = getInventory(playername.toLowerCase());

		saveInventory(inv);
	}

	public void saveAll() {
		int size = this.playerlist.size();
		String playername = null;

		for (int i = 0; i < size; i++) {
			playername = (String) this.playerlist.get(i);
			saveInventory(playername);
		}
	}

	public void saveAndRemoveAll() {
		int size = this.playerlist.size();
		String playername = null;

		for (int i = 0; i < size; i++) {
			playername = (String) this.playerlist.get(i);
			saveInventory(playername);
			removeInventory(playername);
		}
	}
}

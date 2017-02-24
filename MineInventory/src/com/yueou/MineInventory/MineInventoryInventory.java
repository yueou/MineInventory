package com.yueou.MineInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MineInventoryInventory implements InventoryHolder {
	protected Inventory minventory;
	private boolean tochest;
	private boolean drop;
	private boolean sort;
	private boolean send;
	private boolean shift;
	private boolean shiftstat;
	public boolean stat;
	private boolean locked;
	private Player owner;
	private List<Integer> stackids;
	private String ownerName;

	public MineInventoryInventory(String ownerName, int size) {
		this.ownerName = ownerName;
		this.owner = MineInventory.instance.getServer().getPlayer(ownerName);
		List<String> names = MineInventoryConfigReader.instance
				.getBagNameList();
		String prefix = names.get(6);
		if (size <= 9) {
			prefix = names.get(0);
		} else if (size <= 18) {
			prefix = names.get(1);
		} else if (size <= 27) {
			prefix = names.get(2);
		} else if (size <= 36) {
			prefix = names.get(3);
		} else if (size <= 45) {
			prefix = names.get(4);
		} else if (size <= 54) {
			prefix = names.get(5);
		}
		prefix = ownerName + " µÄ " + prefix;

		this.minventory = Bukkit.createInventory(this, size, prefix);
		this.tochest = false;
		this.drop = false;
		this.sort = false;
		this.send = false;
		this.shift = false;
		this.shiftstat = false;
		this.stat = false;
		this.locked = false;
		this.stackids = new ArrayList<Integer>();
	}

	public void openInventory(Player player) {
		player.openInventory(this.minventory);
	}

	public Inventory getInventory() {
		return this.minventory;
	}

	public Player getOwner() {
		return this.owner;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public ListIterator<org.bukkit.inventory.ItemStack> getItems() {
		ListIterator<org.bukkit.inventory.ItemStack> itemlist = this.minventory
				.iterator();
		return itemlist;
	}

	public boolean levelUp() {
		ListIterator<org.bukkit.inventory.ItemStack> list = this.minventory
				.iterator();

		int size = this.minventory.getSize();

		if (size >= 54)
			return false;
		size += 9;

		List<String> names = MineInventoryConfigReader.instance
				.getBagNameList();
		String prefix = names.get(6);
		if (size <= 9) {
			prefix = names.get(0);
		} else if (size <= 18) {
			prefix = names.get(1);
		} else if (size <= 27) {
			prefix = names.get(2);
		} else if (size <= 36) {
			prefix = names.get(3);
		} else if (size <= 45) {
			prefix = names.get(4);
		} else if (size <= 54) {
			prefix = names.get(5);
		}
		prefix = ownerName + " µÄ " + prefix;

		this.minventory = Bukkit.createInventory(this, size, prefix);

		for (int i = 0; list.hasNext(); i++) {
			this.minventory.setItem(i,
					(org.bukkit.inventory.ItemStack) list.next());
		}

		return true;
	}

	public boolean levelUpToLargest() {
		ListIterator<org.bukkit.inventory.ItemStack> list = this.minventory
				.iterator();

		int size = this.minventory.getSize();

		if (size >= 54)
			return false;
		size = 54;

		String prefix = MineInventory.instance.getreader().getBagNameList()
				.get(5);

		this.minventory = Bukkit.createInventory(this, size, prefix);

		for (int i = 0; list.hasNext(); i++) {
			this.minventory.setItem(i,
					(org.bukkit.inventory.ItemStack) list.next());
		}

		return true;
	}

	public void setTochest(boolean tochest) {
		this.tochest = tochest;
	}

	public boolean canTochest() {
		return this.tochest;
	}

	public void setDrop(boolean drop) {
		this.drop = drop;
	}

	public boolean canDrop() {
		return this.drop;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}

	public void lockInventory(boolean lock) {
		this.locked = lock;
	}

	public boolean locked() {
		return this.locked;
	}

	public boolean canSort() {
		return this.sort;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public boolean canSend() {
		return this.send;
	}

	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public void Shift() {
		this.shiftstat = (!this.shiftstat);
	}

	public boolean shiftStat() {
		return this.shiftstat;
	}

	public boolean canShift() {
		return this.shift;
	}

	public void addStackIDs(int id) {
		this.stackids.add(Integer.valueOf(id));
	}

	public List<Integer> getStackIDs() {
		return this.stackids;
	}

	public int getStackIdAt(int pos) {
		return ((Integer) this.stackids.get(pos)).intValue();
	}

	public void sortInventory() {
		org.bukkit.inventory.ItemStack temp = null;

		ListIterator<ItemStack> itemlist = this.minventory.iterator();
		ArrayList<ItemStack> itemarray = new ArrayList<ItemStack>();
		ArrayList<ItemStack> newitemarray = new ArrayList<ItemStack>();

		for (int i = 0; itemlist.hasNext(); i++) {
			temp = (org.bukkit.inventory.ItemStack) itemlist.next();
			if (temp != null)
				itemarray.add(temp);
		}
		int amount = itemarray.size();
		int min = 0;
		for (int i = 0; i < amount; i++) {
			min = 0;
			for (int j = 1; j < amount - i; j++) {
				if (((org.bukkit.inventory.ItemStack) itemarray.get(min))
						.getTypeId() > ((org.bukkit.inventory.ItemStack) itemarray
						.get(j)).getTypeId()) {
					min = j;
				}
			}
			newitemarray.add((org.bukkit.inventory.ItemStack) itemarray
					.get(min));
			itemarray.remove(min);
		}

		this.minventory.clear();
		amount = newitemarray.size();
		for (int i = 0; i < amount; i++) {
			this.minventory
					.addItem(new org.bukkit.inventory.ItemStack[] { (org.bukkit.inventory.ItemStack) newitemarray
							.get(i) });
		}
	}
}

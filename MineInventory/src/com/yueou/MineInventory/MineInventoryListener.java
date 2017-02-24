package com.yueou.MineInventory;

import java.io.PrintStream;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MineInventoryListener implements Listener {
	MineInventory plugin;

	public MineInventoryListener(MineInventory plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onWorldSave(WorldSaveEvent pqe) {
		plugin.getMap().saveAll();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDisconnect(PlayerQuitEvent pqe) {
		String name = pqe.getPlayer().getName();

		MineInventoryInventory inv = this.plugin.getMap().getInventory(name);
		if (inv == null) {
			return;
		}

		System.out.println(inv.getInventory().getViewers().size());
		if (inv.getInventory().getViewers().size() == 0) {
			this.plugin.getMap().saveInventory(name);
			this.plugin.getMap().removeInventory(name);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent ice) {
		Inventory inv = ice.getInventory();

		if ((inv instanceof FurnaceInventory)) {
			return;
		}
		if (!(inv.getHolder() instanceof MineInventoryInventory)) {
			return;
		}

		MineInventoryInventory minv = (MineInventoryInventory) inv.getHolder();

		Player targetplayer = minv.getOwner();

		if (targetplayer == null) {
			System.out.println(minv.getInventory().getViewers().size());
			if (minv.getInventory().getViewers().size() == 1) {
				this.plugin.getMap().saveInventory(minv);
				this.plugin.getMap().removeInventory(minv.getOwnerName());
			}
		} else if (targetplayer.equals(ice.getPlayer())) {
			// this.plugin.getMap().saveInventory(targetplayer.getName());
			if ((((Player) ice.getPlayer()).getName()
					.equalsIgnoreCase(targetplayer.getName())) && (minv.stat)) {
				minv.stat = false;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemDrag(InventoryDragEvent event) {
		if (this.plugin.isDebug()) {
			System.out.println(event.getEventName());
		}
		Inventory inv = event.getInventory();

		if (inv == null) {
			return;
		}
		Player player = null;

		if ((inv.getHolder() instanceof MineInventoryInventory)) {
			HumanEntity hentity = (Player) event.getWhoClicked();

			if ((hentity instanceof Player)) {
				player = (Player) hentity;

				ItemStack item = event.getOldCursor();

				int iid = item.getTypeId();

				List<Integer> blist = this.plugin.getreader()
						.getItemBlackList();

				if (blist.contains(Integer.valueOf(iid))) {
					if (!this.plugin.getPermissionHandler().has(player,
							"mi.admin")) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "该类型的物品被禁止放入扩展背包中");
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemClick(InventoryClickEvent event) {
		if (this.plugin.isDebug()) {
			System.out.println(event.getEventName());
		}
		Inventory inv = event.getClickedInventory();

		if (inv == null) {
			return;
		}
		Player player = null;

		InventoryAction action = event.getAction();

		if (this.plugin.isDebug()) {
			System.out.println(action.name());
		}
		if ((action == InventoryAction.MOVE_TO_OTHER_INVENTORY)
				|| (action == InventoryAction.PLACE_ALL)
				|| (action == InventoryAction.PLACE_ONE)
				|| (action == InventoryAction.PLACE_SOME)
				|| (action == InventoryAction.SWAP_WITH_CURSOR)) {
			ItemStack item = event.getCursor();

			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (event.getInventory() == event.getClickedInventory())
					return;
				inv = event.getInventory();
				item = event.getCurrentItem();
			}

			if ((inv.getHolder() instanceof MineInventoryInventory)) {
				HumanEntity hentity = (Player) event.getWhoClicked();

				if ((hentity instanceof Player)) {
					player = (Player) hentity;

					int iid = item.getTypeId();

					List<Integer> blist = this.plugin.getreader()
							.getItemBlackList();

					if (blist.contains(Integer.valueOf(iid))) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							event.setCancelled(true);
							player.sendMessage(ChatColor.RED
									+ "该类型的物品被禁止放入扩展背包中");
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onChestOpen(InventoryOpenEvent ioe) {
		if (ioe.isCancelled()) {
			return;
		}
		Inventory inv = ioe.getInventory();

		if (inv.getType() != InventoryType.CHEST) {
			return;
		}
		if ((inv.getHolder() instanceof MineInventoryInventory)) {
			String playername = ioe.getPlayer().getName();
			Player player = this.plugin.getServer().getPlayer(playername);
			if (((MineInventoryInventory) inv.getHolder()).locked()) {
				if (!this.plugin.getPermissionHandler().has(player, "mi.admin")) {
					ioe.setCancelled(true);
					player.sendMessage(ChatColor.RED + "你想要查看的背包已被锁定");
				}
			}
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.plugin.loadInventory(player.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInventoryOpen(InventoryOpenEvent event) {
		Inventory inv = event.getInventory();

		Player player = (Player) event.getPlayer();

		if ((inv instanceof FurnaceInventory))
			return;
		if ((inv.getHolder() instanceof MineInventoryInventory)) {
			if (((MineInventoryInventory) inv.getHolder()).getOwner() != null) {
				if (player.getName().equalsIgnoreCase(
						((MineInventoryInventory) inv.getHolder()).getOwner()
								.getName())) {
					((MineInventoryInventory) inv.getHolder()).stat = true;
				}
				return;
			}
		}
	}
}

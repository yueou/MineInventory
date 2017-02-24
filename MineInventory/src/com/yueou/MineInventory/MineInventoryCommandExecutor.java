package com.yueou.MineInventory;

import java.io.PrintStream;
import java.util.List;
import java.util.ListIterator;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public class MineInventoryCommandExecutor implements CommandExecutor {
	private MineInventory plugin;
	private MineInventoryHash inventorymap;

	public MineInventoryCommandExecutor(MineInventory plugin) {
		this.plugin = plugin;
		this.inventorymap = plugin.getMap();
	}

	public boolean onCommand(CommandSender cmder, Command cmd, String str,
			String[] args) {
		double createprice = this.plugin.getreader().getCreatePrice();
		List<Double> luprice = this.plugin.getreader().getPrices();

		org.bukkit.entity.Player player = null;
		String playername = null;

		if ((cmder instanceof org.bukkit.entity.Player)) {
			player = (org.bukkit.entity.Player) cmder;
			playername = cmder.getName();
		} else {
			return false;
		}

		Economy economy = this.plugin.getEconomy();

		if (args.length >= 1) {
			if (cmd.getName().equalsIgnoreCase("mi")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("open")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}

						MineInventoryInventory minv = this.inventorymap
								.getInventory(playername);

						if (minv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有扩展背包， 使用/mi create 来创建背包");
							return true;
						}
						minv.openInventory(player);

						return true;
					}

					if (args[0].equalsIgnoreCase("create")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有创建扩展背包的权限.");
							return true;
						}

						if (this.plugin.hasInventory(playername)) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你已经拥有了扩展背包");
							return true;
						}

						player.sendMessage("这个操作将会花费你 " + createprice
								+ " 元, 请输入" + org.bukkit.ChatColor.YELLOW
								+ "/mi confirm " + org.bukkit.ChatColor.WHITE
								+ "确认操作.");
						this.plugin.getCommandMap().setLastCommand(
								cmder.getName(), args[0]);

						return true;
					}

					if (args[0].equalsIgnoreCase("expand")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.getInventory(playername);
						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你还没有扩展背包, 无法给背包升级");
							return true;
						}
						int invsize = inv.getInventory().getSize();
						if (invsize == 54) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你的背包已经是最大的了, 无法继续升级");
							return true;
						}
						double price;

						switch (invsize) {
						case 9:
							price = luprice.get(0);
							break;
						case 18:
							price = luprice.get(1);
							break;
						case 27:
							price = luprice.get(2);
							break;
						case 36:
							price = luprice.get(3);
							break;
						case 45:
							price = luprice.get(4);
							break;
						default:
							price = -1.0D;
						}
						if (price == -1.0D) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "发生未知错误[File = MIneInventoryCommandExecutor, Function = onCommand, Block = levelup]");
							return true;
						}

						player.sendMessage("你需要花费" + price + "元,将背包升级到"
								+ (invsize + 9) + "格, 确认升级请输入/mi confirm");

						this.plugin.getCommandMap().setLastCommand(
								cmder.getName(), args[0]);

						return true;
					}
					if ((args[0].equalsIgnoreCase("remove"))
							|| (args[0].equalsIgnoreCase("delete"))) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}

						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你还没有扩展背包");
							return true;
						}

						player.sendMessage("这个操作会删除背包里所有的物品和背包，并无法撤销，确认删除请输入/mi confirm");
						this.plugin.getCommandMap().setLastCommand(playername,
								args[0]);

						return true;
					}

					if (args[0].equalsIgnoreCase("drop")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}

						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你还没有扩展背包");
							return true;
						}

						Inventory inv = this.inventorymap.getInventory(
								playername).getInventory();
						ListIterator<ItemStack> itemlist = inv.iterator();

						World playerworld = player.getWorld();

						Location playerlocation = player.getLocation();
						ItemStack is = null;

						while (itemlist.hasNext()) {
							is = (ItemStack) itemlist.next();
							if (is != null) {
								playerworld.dropItemNaturally(playerlocation,
										is).setPickupDelay(100);
							}
						}
						inv.clear();

						player.sendMessage("哗啦~");

						// this.inventorymap.saveInventory(playername);

						return true;
					}

					if (args[0].equalsIgnoreCase("sort")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}
						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你还没有扩展背包");
							return true;
						}

						player.sendMessage("已将背包内物品自动整理.");

						this.plugin.getMap().getInventory(playername)
								.sortInventory();
						// this.plugin.getMap().saveInventory(playername);

						return true;
					}

					if (args[0].equalsIgnoreCase("reload")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}

						this.plugin.onReload();
						player.sendMessage("MineInventory v"
								+ this.plugin.getDescription().getVersion()
								+ " by yueou 设置已重载!");

						return true;
					}
					if (args[0].equalsIgnoreCase("confirm")) {
						MineInventoryCommandHash cm = this.plugin
								.getCommandMap();

						String lastcmd = cm.getLastCommand(playername);
						if (lastcmd == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "不明操作");
							return true;
						}

						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用扩展背包的权限.");
							return true;
						}

						if (lastcmd.equalsIgnoreCase("create")) {
							if (economy == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "经济插件加载失败,请联系技术组");
								return true;
							}
							if (economy.getBalance(playername) < createprice) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你的金钱不足");
								cm.removeLastCommand(playername);
								return true;
							}

							EconomyResponse ecores = economy.withdrawPlayer(
									playername, createprice);
							if (ecores.type != EconomyResponse.ResponseType.SUCCESS) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "错误:创建背包失败\n 错误信息:"
										+ ecores.errorMessage);
								return true;
							}

							MineInventoryInventory inv = this.inventorymap
									.addInventory(player.getName()
											.toLowerCase(), 9);

							if (inv != null) {
								player.sendMessage("花费了 " + createprice
										+ ", 创建扩展背包成功");

								this.plugin.registerInventory(playername, inv,
										9);

								System.out
										.println("[MineInventory]Inventory of "
												+ player.getName()
												+ " created.");
							} else {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "发生未知错误");
							}

							cm.removeLastCommand(playername);
							return true;
						}

						if ((lastcmd.equalsIgnoreCase("remove"))
								|| (lastcmd.equalsIgnoreCase("delete"))) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.use")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你没有使用扩展背包的权限.");
								return true;
							}

							if (this.plugin.removeInventory(playername)) {
								player.sendMessage("删除扩展背包成功");

								if (this.plugin.isDebug()) {
									System.out
											.println("[MineInventory]Inventory of "
													+ player.getName()
													+ " removed.");
								}
							} else {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "发生未知错误");
							}

							cm.removeLastCommand(playername);
							return true;
						}
						if (lastcmd.equalsIgnoreCase("expand")) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.use")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你没有使用扩展背包的权限.");
								return true;
							}

							MineInventoryInventory inv = this.inventorymap
									.getInventory(playername);

							if (inv == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你没还有扩展背包.");
								return true;
							}
							int isize = inv.getInventory().getSize();
							String needprm = "mi.use";

							double price;
							switch (isize) {
							case 9:
								price = luprice.get(0);
								needprm = "mi.use";
								break;
							case 18:
								price = luprice.get(1);
								needprm = "mi.use";
								break;
							case 27:
								price = luprice.get(2);
								needprm = "mi.use";
								break;
							case 36:
								price = luprice.get(3);
								needprm = "mi.use";
								break;
							case 45:
								price = luprice.get(4);
								needprm = "mi.use";
								break;
							default:
								price = 0.0D;
							}

							if (!this.plugin.getPermissionHandler().has(player,
									needprm)) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你没有将背包升级到 "
										+ org.bukkit.ChatColor.YELLOW
										+ (isize + 9)
										+ org.bukkit.ChatColor.RED + " 格的权限.");
								return true;
							}

							if (economy == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "经济插件加载失败,请联系技术组");
								return true;
							}

							if (economy.getBalance(playername) < price) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你的金钱不足");
								cm.removeLastCommand(playername);
								return true;
							}

							ListIterator<ItemStack> itemlist = inv
									.getInventory().iterator();
							MineInventoryInventory newinventory = new MineInventoryInventory(
									player.getName().toLowerCase(), 54);
							Inventory newinv = newinventory.getInventory();

							for (int i = 0; itemlist.hasNext(); i++) {
								newinv.setItem(i, (ItemStack) itemlist.next());
							}
							this.plugin.levelUpInventory(playername);

							EconomyResponse ecores = economy.withdrawPlayer(
									playername, price);
							if (ecores.type != EconomyResponse.ResponseType.SUCCESS) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "错误:创建背包失败\n 错误信息:"
										+ ecores.errorMessage);
								return true;
							}
							player.sendMessage("你成功的升级了背包");

							if (this.plugin.isDebug()) {
								System.out.println("Inventory of " + playername
										+ " level up!");
							}
							cm.removeLastCommand(playername);

							return true;
						}
					} else {
						if (args[0].equalsIgnoreCase("help")) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.use")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你没有使用扩展背包的权限.");
								return true;
							}

							player.sendMessage("查看 MineInventory 的玩家命令:");
							player.sendMessage("/mi create : 为自己创建一个扩展背包");
							player.sendMessage("/mi open : 打开自己的扩展背包");
							player.sendMessage("/mi expand : 为自己的扩展背包升级");

							player.sendMessage("/mi help : 查看所有普通命令");
							player.sendMessage("/mi adminhelp : 查看所有命令");
							player.sendMessage("MineInventory v"
									+ this.plugin.getDescription().getVersion()
									+ " 这个插件的作者是你们心爱的yueou.");
							return true;
						}
						if (args[0].equalsIgnoreCase("adminhelp")) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.admin")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "你使用这个命令的权限.");
								return true;
							}

							player.sendMessage("查看 MineInventory 的所有命令:");
							player.sendMessage("/mi create : 为自己创建一个扩展背包");
							player.sendMessage("/mi open : 打开自己的扩展背包");
							player.sendMessage("/mi sort : 将扩展背包里的物品按照物品ID进行排序和整理");
							player.sendMessage("/mi remove(delete) : 删除自己的扩展背包");
							player.sendMessage("/mi expand : 给自己的扩展背包扩容");
							player.sendMessage("/mi drop : 将扩展背包里的东西全部扔到地下");
							player.sendMessage("/mi help : 查看所有普通命令");
							player.sendMessage("/mi lock <玩家名> : 锁定目标玩家的扩展背包");
							player.sendMessage("/mi createfor <玩家名> : 为目标玩家创建一个扩展背包");
							player.sendMessage("/mi inspect <玩家名> : 监视目标玩家的扩展背包");
							player.sendMessage("/mi clearfrom <玩家名> : 清空目标玩家的扩展背包");
							player.sendMessage("/mi full <物品ID/物品名称> : 用某种物品填满自己的扩展背包");
							player.sendMessage("/mi reload : 重载插件");
							player.sendMessage("/mi adminhelp : 查看所有管理员专用命令");
							player.sendMessage("MineInventory 版本号 v"
									+ this.plugin.getDescription().getVersion()
									+ " 这个插件的作者是你们心爱的yueou.");
							return true;
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("inspect")) {
						String targetplayername = args[1];

						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}
						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);
						if (targetplayer == null) {
							MineInventoryInventory targetinv = this.plugin
									.loadInventory(args[1]);

							if (targetinv != null) {
								player.openInventory(targetinv.getInventory());
							} else
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "未找到该玩家的背包数据");
							return true;
						}
						targetplayername = targetplayer.getName();

						MineInventoryInventory targetinventory = this.inventorymap
								.getInventory(targetplayername);

						if (targetinventory == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "未找到该玩家的扩展背包.");
							return true;
						}
						player.openInventory(targetinventory.getInventory());
						return true;
					}
					if (args[0].equalsIgnoreCase("createfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}

						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);
						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "未找到在线玩家,创建失败");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "未找到在线玩家,创建失败");
							return true;
						}
						String targetplayername = targetplayer.getName();

						if (this.inventorymap.getInventory(targetplayername) != null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家已经拥有了扩展背包");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.addInventory(targetplayer.getName()
										.toLowerCase(), 9);
						if (inv != null) {
							player.sendMessage("为 " + targetplayername
									+ " 创建了一个背包.");
							targetplayer.sendMessage("管理员为你创建了一个扩展背包");

							this.plugin.registerInventory(targetplayername,
									inv, 9);

							if (this.plugin.isDebug()) {
								System.out
										.println("[MineInventory]Inventory of "
												+ targetplayername
												+ " created.");
							}
						} else {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "发生未知错误");
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("clearfrom")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}
						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);
						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "没有找到该玩家");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家不在线,无法清空背包");
							return true;
						}

						String targetplayername = targetplayer.getName();

						if (targetplayername.compareTo(args[1].toLowerCase()) != 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "清空背包必须使用完全匹配的玩家名");
							return true;
						}

						if (this.inventorymap.getInventory(targetplayer) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩没有扩展背包");
							return true;
						}

						Inventory targetinventory = this.inventorymap
								.getInventory(targetplayer).getInventory();
						targetinventory.clear();
						player.sendMessage("清空了 " + targetplayername + " 的扩展背包");
						targetplayer.sendMessage("你的扩展背包被管理员清空了");

						this.inventorymap.saveInventory(targetplayername);

						return true;
					}

					if (args[0].equalsIgnoreCase("lock")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}
						String tplayername = args[1];
						org.bukkit.entity.Player tplayer = this.plugin
								.getServer().getPlayer(tplayername);
						if (tplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "没有找到玩家");
							return true;
						}
						if (!tplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家不在线");
							return true;
						}
						MineInventoryInventory inv = this.inventorymap
								.getInventory(tplayer);

						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家没有扩展背包");
							return true;
						}

						inv.lockInventory(!inv.locked());
						if (inv.locked()) {
							player.sendMessage(org.bukkit.ChatColor.GREEN
									+ "已锁定该玩家的扩展背包");
							tplayer.sendMessage(org.bukkit.ChatColor.GREEN
									+ "你的扩展背包被锁定了!");
						} else {
							player.sendMessage(org.bukkit.ChatColor.GREEN
									+ "已解锁该玩家的扩展背包");
							tplayer.sendMessage(org.bukkit.ChatColor.GREEN
									+ "你的扩展背包解锁了!");
						}
						// this.inventorymap.saveInventory(tplayername);

						return true;
					}
					if (args[0].equalsIgnoreCase("full")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}

						if (this.inventorymap.getInventory(player.getName()) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你还没有扩展背包");
							return true;
						}

						int itemid = 0;

						args[1] = args[1].toUpperCase();
						int len = args[1].length();
						boolean stat;
						if (Material.getMaterial(args[1]) == null) {
							stat = false;
							for (int j = 0; j < len; j++) {
								char c = args[1].charAt(j);
								if ((c > '9') || (c < '0')) {
									player.sendMessage(org.bukkit.ChatColor.RED
											+ "没有找到物品  "
											+ org.bukkit.ChatColor.YELLOW
											+ args[1]);
									return true;
								}
							}
							itemid = Integer.parseInt(args[1]);
							if (Material.getMaterial(itemid) == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "没有找到物品  "
										+ org.bukkit.ChatColor.YELLOW + args[1]);
								return true;
							}
						} else {
							stat = true;
						}

						Inventory inv = this.inventorymap.getInventory(player)
								.getInventory();

						int size = inv.getSize();

						if (!stat) {
							for (int i = 0; i < size; i++) {
								ItemStack item = new ItemStack(itemid);
								item.setAmount(64);
								inv.setItem(i, item);
							}

						} else {
							for (int i = 0; i < size; i++) {
								ItemStack item = new ItemStack(
										Material.getMaterial(args[1]));
								item.setAmount(64);
								inv.setItem(i, item);
							}
						}
						player.sendMessage("填充完毕!");

						return true;
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("expandfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}

						String tplayername = args[1];

						org.bukkit.entity.Player tplayer = this.plugin
								.getServer().getPlayer(tplayername);
						if (tplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "未找到该玩家.");
							return true;
						}
						if (!tplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家不在线.");
						}

						MineInventoryInventory inv = this.inventorymap
								.getInventory(tplayername);
						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家没有背包");
							return true;
						}

						this.plugin.levelUpInventory(tplayername);

						player.sendMessage(org.bukkit.ChatColor.GREEN
								+ tplayername + " 的背包被升级到了 "
								+ inv.getInventory().getSize() + " 格.");
						this.plugin
								.getServer()
								.getPlayer(tplayername)
								.sendMessage(
										org.bukkit.ChatColor.GREEN
												+ "你的背包被管理员升级到了 "
												+ inv.getInventory().getSize()
												+ " 格.");

						return true;
					}
					if (args[0].equalsIgnoreCase("createfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你没有使用这个命令的权限.");
							return true;
						}

						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);

						int size = 0;
						int len = args[2].length();

						for (int j = 0; j < len; j++) {
							char c = args[2].charAt(j);
							if ((c > '9') || (c < '0')) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "错误的大小参数  " + args[2]);
								return true;
							}
						}
						size = Integer.parseInt(args[2]);

						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "未找到在线玩家,创建失败");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家不在线,创建失败");
							return true;
						}
						String targetplayername = targetplayer.getName();

						if (this.inventorymap.getInventory(targetplayername) != null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "该玩家已经拥有了扩展背包");
							return true;
						}
						if (size == 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "你在开玩笑?");
							return true;
						}

						if (size > 90) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "我擦!这么大的包,你是想逆天啊!?");
							return true;
						}
						if (size % 9 != 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "背包的大小必须为⑨的倍数");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.addInventory(targetplayer.getName()
										.toLowerCase(), size);
						if (inv != null) {
							player.sendMessage("为 " + targetplayername
									+ " 创建了一个背包.");
							targetplayer.sendMessage("管理员为你创建了一个扩展背包");

							this.plugin.registerInventory(targetplayername,
									inv, size);

							if (this.plugin.isDebug()) {
								System.out
										.println("[MineInventory]Inventory of "
												+ targetplayername
												+ " created.");
							}
						} else {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "发生未知错误");
						}
						return true;
					}
				}
			}
		}

		player.sendMessage(org.bukkit.ChatColor.RED
				+ "命令无效, 请使用/mi help 来查看详细的命令帮助.");
		return true;
	}
}

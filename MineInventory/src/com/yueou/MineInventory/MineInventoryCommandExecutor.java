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
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}

						MineInventoryInventory minv = this.inventorymap
								.getInventory(playername);

						if (minv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û����չ������ ʹ��/mi create ����������");
							return true;
						}
						minv.openInventory(player);

						return true;
					}

					if (args[0].equalsIgnoreCase("create")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û�д�����չ������Ȩ��.");
							return true;
						}

						if (this.plugin.hasInventory(playername)) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "���Ѿ�ӵ������չ����");
							return true;
						}

						player.sendMessage("����������Ứ���� " + createprice
								+ " Ԫ, ������" + org.bukkit.ChatColor.YELLOW
								+ "/mi confirm " + org.bukkit.ChatColor.WHITE
								+ "ȷ�ϲ���.");
						this.plugin.getCommandMap().setLastCommand(
								cmder.getName(), args[0]);

						return true;
					}

					if (args[0].equalsIgnoreCase("expand")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.getInventory(playername);
						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�㻹û����չ����, �޷�����������");
							return true;
						}
						int invsize = inv.getInventory().getSize();
						if (invsize == 54) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��ı����Ѿ���������, �޷���������");
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
									+ "����δ֪����[File = MIneInventoryCommandExecutor, Function = onCommand, Block = levelup]");
							return true;
						}

						player.sendMessage("����Ҫ����" + price + "Ԫ,������������"
								+ (invsize + 9) + "��, ȷ������������/mi confirm");

						this.plugin.getCommandMap().setLastCommand(
								cmder.getName(), args[0]);

						return true;
					}
					if ((args[0].equalsIgnoreCase("remove"))
							|| (args[0].equalsIgnoreCase("delete"))) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}

						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�㻹û����չ����");
							return true;
						}

						player.sendMessage("���������ɾ�����������е���Ʒ�ͱ��������޷�������ȷ��ɾ��������/mi confirm");
						this.plugin.getCommandMap().setLastCommand(playername,
								args[0]);

						return true;
					}

					if (args[0].equalsIgnoreCase("drop")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}

						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�㻹û����չ����");
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

						player.sendMessage("����~");

						// this.inventorymap.saveInventory(playername);

						return true;
					}

					if (args[0].equalsIgnoreCase("sort")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}
						if (this.inventorymap.getInventory(playername) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�㻹û����չ����");
							return true;
						}

						player.sendMessage("�ѽ���������Ʒ�Զ�����.");

						this.plugin.getMap().getInventory(playername)
								.sortInventory();
						// this.plugin.getMap().saveInventory(playername);

						return true;
					}

					if (args[0].equalsIgnoreCase("reload")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}

						this.plugin.onReload();
						player.sendMessage("MineInventory v"
								+ this.plugin.getDescription().getVersion()
								+ " by yueou ����������!");

						return true;
					}
					if (args[0].equalsIgnoreCase("confirm")) {
						MineInventoryCommandHash cm = this.plugin
								.getCommandMap();

						String lastcmd = cm.getLastCommand(playername);
						if (lastcmd == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��������");
							return true;
						}

						if (!this.plugin.getPermissionHandler().has(player,
								"mi.use")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����չ������Ȩ��.");
							return true;
						}

						if (lastcmd.equalsIgnoreCase("create")) {
							if (economy == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "���ò������ʧ��,����ϵ������");
								return true;
							}
							if (economy.getBalance(playername) < createprice) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��Ľ�Ǯ����");
								cm.removeLastCommand(playername);
								return true;
							}

							EconomyResponse ecores = economy.withdrawPlayer(
									playername, createprice);
							if (ecores.type != EconomyResponse.ResponseType.SUCCESS) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "����:��������ʧ��\n ������Ϣ:"
										+ ecores.errorMessage);
								return true;
							}

							MineInventoryInventory inv = this.inventorymap
									.addInventory(player.getName()
											.toLowerCase(), 9);

							if (inv != null) {
								player.sendMessage("������ " + createprice
										+ ", ������չ�����ɹ�");

								this.plugin.registerInventory(playername, inv,
										9);

								System.out
										.println("[MineInventory]Inventory of "
												+ player.getName()
												+ " created.");
							} else {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "����δ֪����");
							}

							cm.removeLastCommand(playername);
							return true;
						}

						if ((lastcmd.equalsIgnoreCase("remove"))
								|| (lastcmd.equalsIgnoreCase("delete"))) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.use")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��û��ʹ����չ������Ȩ��.");
								return true;
							}

							if (this.plugin.removeInventory(playername)) {
								player.sendMessage("ɾ����չ�����ɹ�");

								if (this.plugin.isDebug()) {
									System.out
											.println("[MineInventory]Inventory of "
													+ player.getName()
													+ " removed.");
								}
							} else {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "����δ֪����");
							}

							cm.removeLastCommand(playername);
							return true;
						}
						if (lastcmd.equalsIgnoreCase("expand")) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.use")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��û��ʹ����չ������Ȩ��.");
								return true;
							}

							MineInventoryInventory inv = this.inventorymap
									.getInventory(playername);

							if (inv == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��û������չ����.");
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
										+ "��û�н����������� "
										+ org.bukkit.ChatColor.YELLOW
										+ (isize + 9)
										+ org.bukkit.ChatColor.RED + " ���Ȩ��.");
								return true;
							}

							if (economy == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "���ò������ʧ��,����ϵ������");
								return true;
							}

							if (economy.getBalance(playername) < price) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��Ľ�Ǯ����");
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
										+ "����:��������ʧ��\n ������Ϣ:"
										+ ecores.errorMessage);
								return true;
							}
							player.sendMessage("��ɹ��������˱���");

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
										+ "��û��ʹ����չ������Ȩ��.");
								return true;
							}

							player.sendMessage("�鿴 MineInventory ���������:");
							player.sendMessage("/mi create : Ϊ�Լ�����һ����չ����");
							player.sendMessage("/mi open : ���Լ�����չ����");
							player.sendMessage("/mi expand : Ϊ�Լ�����չ��������");

							player.sendMessage("/mi help : �鿴������ͨ����");
							player.sendMessage("/mi adminhelp : �鿴��������");
							player.sendMessage("MineInventory v"
									+ this.plugin.getDescription().getVersion()
									+ " �������������������İ���yueou.");
							return true;
						}
						if (args[0].equalsIgnoreCase("adminhelp")) {
							if (!this.plugin.getPermissionHandler().has(player,
									"mi.admin")) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "��ʹ����������Ȩ��.");
								return true;
							}

							player.sendMessage("�鿴 MineInventory ����������:");
							player.sendMessage("/mi create : Ϊ�Լ�����һ����չ����");
							player.sendMessage("/mi open : ���Լ�����չ����");
							player.sendMessage("/mi sort : ����չ���������Ʒ������ƷID�������������");
							player.sendMessage("/mi remove(delete) : ɾ���Լ�����չ����");
							player.sendMessage("/mi expand : ���Լ�����չ��������");
							player.sendMessage("/mi drop : ����չ������Ķ���ȫ���ӵ�����");
							player.sendMessage("/mi help : �鿴������ͨ����");
							player.sendMessage("/mi lock <�����> : ����Ŀ����ҵ���չ����");
							player.sendMessage("/mi createfor <�����> : ΪĿ����Ҵ���һ����չ����");
							player.sendMessage("/mi inspect <�����> : ����Ŀ����ҵ���չ����");
							player.sendMessage("/mi clearfrom <�����> : ���Ŀ����ҵ���չ����");
							player.sendMessage("/mi full <��ƷID/��Ʒ����> : ��ĳ����Ʒ�����Լ�����չ����");
							player.sendMessage("/mi reload : ���ز��");
							player.sendMessage("/mi adminhelp : �鿴���й���Աר������");
							player.sendMessage("MineInventory �汾�� v"
									+ this.plugin.getDescription().getVersion()
									+ " �������������������İ���yueou.");
							return true;
						}
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("inspect")) {
						String targetplayername = args[1];

						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
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
										+ "δ�ҵ�����ҵı�������");
							return true;
						}
						targetplayername = targetplayer.getName();

						MineInventoryInventory targetinventory = this.inventorymap
								.getInventory(targetplayername);

						if (targetinventory == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "δ�ҵ�����ҵ���չ����.");
							return true;
						}
						player.openInventory(targetinventory.getInventory());
						return true;
					}
					if (args[0].equalsIgnoreCase("createfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}

						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);
						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "δ�ҵ��������,����ʧ��");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "δ�ҵ��������,����ʧ��");
							return true;
						}
						String targetplayername = targetplayer.getName();

						if (this.inventorymap.getInventory(targetplayername) != null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "������Ѿ�ӵ������չ����");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.addInventory(targetplayer.getName()
										.toLowerCase(), 9);
						if (inv != null) {
							player.sendMessage("Ϊ " + targetplayername
									+ " ������һ������.");
							targetplayer.sendMessage("����ԱΪ�㴴����һ����չ����");

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
									+ "����δ֪����");
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("clearfrom")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}
						org.bukkit.entity.Player targetplayer = this.plugin
								.getServer().getPlayer(args[1]);
						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "û���ҵ������");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "����Ҳ�����,�޷���ձ���");
							return true;
						}

						String targetplayername = targetplayer.getName();

						if (targetplayername.compareTo(args[1].toLowerCase()) != 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��ձ�������ʹ����ȫƥ��������");
							return true;
						}

						if (this.inventorymap.getInventory(targetplayer) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "����û����չ����");
							return true;
						}

						Inventory targetinventory = this.inventorymap
								.getInventory(targetplayer).getInventory();
						targetinventory.clear();
						player.sendMessage("����� " + targetplayername + " ����չ����");
						targetplayer.sendMessage("�����չ����������Ա�����");

						this.inventorymap.saveInventory(targetplayername);

						return true;
					}

					if (args[0].equalsIgnoreCase("lock")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}
						String tplayername = args[1];
						org.bukkit.entity.Player tplayer = this.plugin
								.getServer().getPlayer(tplayername);
						if (tplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "û���ҵ����");
							return true;
						}
						if (!tplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "����Ҳ�����");
							return true;
						}
						MineInventoryInventory inv = this.inventorymap
								.getInventory(tplayer);

						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�����û����չ����");
							return true;
						}

						inv.lockInventory(!inv.locked());
						if (inv.locked()) {
							player.sendMessage(org.bukkit.ChatColor.GREEN
									+ "����������ҵ���չ����");
							tplayer.sendMessage(org.bukkit.ChatColor.GREEN
									+ "�����չ������������!");
						} else {
							player.sendMessage(org.bukkit.ChatColor.GREEN
									+ "�ѽ�������ҵ���չ����");
							tplayer.sendMessage(org.bukkit.ChatColor.GREEN
									+ "�����չ����������!");
						}
						// this.inventorymap.saveInventory(tplayername);

						return true;
					}
					if (args[0].equalsIgnoreCase("full")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}

						if (this.inventorymap.getInventory(player.getName()) == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�㻹û����չ����");
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
											+ "û���ҵ���Ʒ  "
											+ org.bukkit.ChatColor.YELLOW
											+ args[1]);
									return true;
								}
							}
							itemid = Integer.parseInt(args[1]);
							if (Material.getMaterial(itemid) == null) {
								player.sendMessage(org.bukkit.ChatColor.RED
										+ "û���ҵ���Ʒ  "
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
						player.sendMessage("������!");

						return true;
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("expandfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
							return true;
						}

						String tplayername = args[1];

						org.bukkit.entity.Player tplayer = this.plugin
								.getServer().getPlayer(tplayername);
						if (tplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "δ�ҵ������.");
							return true;
						}
						if (!tplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "����Ҳ�����.");
						}

						MineInventoryInventory inv = this.inventorymap
								.getInventory(tplayername);
						if (inv == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�����û�б���");
							return true;
						}

						this.plugin.levelUpInventory(tplayername);

						player.sendMessage(org.bukkit.ChatColor.GREEN
								+ tplayername + " �ı������������� "
								+ inv.getInventory().getSize() + " ��.");
						this.plugin
								.getServer()
								.getPlayer(tplayername)
								.sendMessage(
										org.bukkit.ChatColor.GREEN
												+ "��ı���������Ա�������� "
												+ inv.getInventory().getSize()
												+ " ��.");

						return true;
					}
					if (args[0].equalsIgnoreCase("createfor")) {
						if (!this.plugin.getPermissionHandler().has(player,
								"mi.admin")) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "��û��ʹ����������Ȩ��.");
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
										+ "����Ĵ�С����  " + args[2]);
								return true;
							}
						}
						size = Integer.parseInt(args[2]);

						if (targetplayer == null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "δ�ҵ��������,����ʧ��");
							return true;
						}
						if (!targetplayer.isOnline()) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "����Ҳ�����,����ʧ��");
							return true;
						}
						String targetplayername = targetplayer.getName();

						if (this.inventorymap.getInventory(targetplayername) != null) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "������Ѿ�ӵ������չ����");
							return true;
						}
						if (size == 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "���ڿ���Ц?");
							return true;
						}

						if (size > 90) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�Ҳ�!��ô��İ�,���������찡!?");
							return true;
						}
						if (size % 9 != 0) {
							player.sendMessage(org.bukkit.ChatColor.RED
									+ "�����Ĵ�С����Ϊ��ı���");
							return true;
						}

						MineInventoryInventory inv = this.inventorymap
								.addInventory(targetplayer.getName()
										.toLowerCase(), size);
						if (inv != null) {
							player.sendMessage("Ϊ " + targetplayername
									+ " ������һ������.");
							targetplayer.sendMessage("����ԱΪ�㴴����һ����չ����");

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
									+ "����δ֪����");
						}
						return true;
					}
				}
			}
		}

		player.sendMessage(org.bukkit.ChatColor.RED
				+ "������Ч, ��ʹ��/mi help ���鿴��ϸ���������.");
		return true;
	}
}

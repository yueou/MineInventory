package com.yueou.MineInventory;


import java.util.ListIterator;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MineInventorySignListener implements Listener{
	
	
	private MineInventoryHash inventorymap;
	
	public static String PRETITLE = "[MI]";
	public static String TITLE = "[" + ChatColor.BLUE + "背包升级" + ChatColor.BLACK + "]";
	public static String PREUPDATE = "SizeUp";
	public static String UPDATE = ChatColor.BLUE + "扩大容量";
	public static String PRETOCHEST = "ToChest";
	public static String TOCHEST = ChatColor.BLUE + "快速装箱器";
	public static String PREDROP = "Drop";
	public static String DROP = ChatColor.BLUE + "背包上的洞";
	public static String PRESORT = "Sort";
	public static String SORT = ChatColor.BLUE + "智能背包";
	public static String PRESEND = "Send";
	public static String SEND = ChatColor.BLUE + "物品发射器";
	public static String PRICE = "价格：钻石x";
	public static String NOTE = "用钻石右键点击牌子";

	MineInventory plugin;
	
	public MineInventorySignListener(MineInventory plugin){
		this.plugin=plugin;
		inventorymap = plugin.getMap();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onSignPlace(SignChangeEvent bpe){
		
		if(bpe.isCancelled())
			return;
		
		Player player = bpe.getPlayer();
		//String playername = player.getName();
		
		Sign sign = (Sign)bpe.getBlock().getState();
		
		String title = bpe.getLine(0);
		if(!(title.equalsIgnoreCase(PRETITLE)||title.equalsIgnoreCase(TITLE))){
			return;
		}
		else
		{
            if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
            {
                 player.sendMessage("你没有这个操作的权限.");
                 bpe.setCancelled(true);
                 bpe.getBlock().breakNaturally();
                 return;
            }
            
            String data = bpe.getLine(1);
            String sprice = bpe.getLine(2);
            if(data==null||sprice==null||(data.compareTo("")==0)||(sprice.compareTo("")==0)){
            	player.sendMessage("无效的参数");
            	bpe.setCancelled(true);
            	bpe.getBlock().breakNaturally();
            	return;
            }
            
            int len = sprice.length();
        	for(int j=0;j<len;j++){
        		char c = sprice.charAt(j);
        		if(c>'9'||c<'0'){
                	player.sendMessage("无效的价格");
                	bpe.setCancelled(true);
                	bpe.getBlock().breakNaturally();
                	return;
        		}
        	}
            
            
            int price = Integer.parseInt(sprice);
            
            
        	bpe.setLine(0, TITLE);
        	bpe.setLine(2, PRICE+price);
        	bpe.setLine(3, NOTE);
        	
            if(data.equalsIgnoreCase(PREUPDATE)){
            	
            	bpe.setLine(1, UPDATE);

            }
            else if(data.equalsIgnoreCase(PRETOCHEST)){
            	
            	bpe.setLine(1, TOCHEST);

            }
            else if(data.equalsIgnoreCase(PREDROP)){
            	
            	bpe.setLine(1, DROP);

            }
            else if(data.equalsIgnoreCase(PRESORT)){
            	
            	bpe.setLine(1, SORT);

            }
            else if(data.equalsIgnoreCase(PRESEND)){
            	
            	bpe.setLine(1, SEND);

            }
            else{
            	player.sendMessage("无效的升级项目");
            	bpe.setCancelled(true);
            	sign.getBlock().breakNaturally();
            	return;
            }
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onSignClick(PlayerInteractEvent pie){
		
		if(pie.isCancelled())
			return;
		
		if(pie.getAction()==Action.LEFT_CLICK_AIR||pie.getAction()==Action.LEFT_CLICK_BLOCK)
			return;
		
		if(!(pie.getClickedBlock().getState() instanceof Sign)){
			return;
		}

		Sign sign = (Sign)pie.getClickedBlock().getState();
		if(sign.getLine(0)==null)
			return;

		if(sign.getLine(0).compareTo(TITLE)!=0)
			return;
		
		String lines[] = sign.getLines();
		
		Player player = pie.getPlayer();
		String playername = player.getName();
		
        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
        {
            player.sendMessage(ChatColor.RED + "你没有这个操作的权限.");
            return;
        }
        if(inventorymap.getInventory(player)==null){
        	player.sendMessage(ChatColor.RED + "你还没有扩展背包,无法购买升级项!");
        	return;
        }
        
        ItemStack item = player.getItemInHand();
        if(item.getType() != Material.DIAMOND){
        	player.sendMessage(ChatColor.RED + "你的手里没有钻石!");
        	return;
        }
        int itemnum = item.getAmount();
        
        int price = Integer.parseInt(lines[2].split("x")[1]);
        //Object[] items = player.getInventory().all(Material.DIAMOND).values().toArray();
        if(itemnum<price){
        	player.sendMessage(ChatColor.RED + "钻石数量不足!");
        	return;
        }
        
        if(lines[1].compareTo(UPDATE)==0){

    		MineInventoryInventory inv = inventorymap.getInventory(playername); 
    		int size = inv.getInventory().getSize();
    		
    		if(size >= 54){
    			player.sendMessage(ChatColor.RED + "这个大小的背包无法通过购买来继续升级");
    			return;
    		}
    		
    		int newsize = size+9;
    		
           	ListIterator<ItemStack> itemlist = inv.getInventory().iterator();
           	MineInventoryInventory newinventory = new MineInventoryInventory(playername,newsize);
           	Inventory newinv = newinventory.getInventory();
           	
           	for(int i=0;itemlist.hasNext();i++){
           		newinv.setItem(i, itemlist.next());
           	}
           	inventorymap.removeInventory(playername);
           	inventorymap.addInventory(playername, newinventory);
           	
           	player.sendMessage("你花费了 "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " 颗钻石, 成功的将背包升级到了 "+ ChatColor.YELLOW + newsize +" 格!");
      	
        }
        else if(lines[1].compareTo(TOCHEST)==0){
        	if(inventorymap.getInventory(playername).canTochest()){
        		player.sendMessage(ChatColor.RED + "你已经拥有了此项功能");
        		return;
        	}
        	inventorymap.getInventory(playername).setTochest(true);
           	player.sendMessage("你花费了 "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " 颗钻石, 成功的升级了背包功能 "+ ChatColor.YELLOW +" 快速装箱器!");
          	
        }        
        else if(lines[1].compareTo(DROP)==0){
        	if(inventorymap.getInventory(playername).canDrop()){
        		player.sendMessage(ChatColor.RED + "你已经拥有了此项功能");
        		return;
        	}
        	inventorymap.getInventory(playername).setDrop(true);
           	player.sendMessage("你花费了 "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " 颗钻石, 成功的升级了背包功能 "+ ChatColor.YELLOW +" 背包上的洞!");
          	
        }
        else if(lines[1].compareTo(SORT)==0){
        	if(inventorymap.getInventory(playername).canSort())
        	{
        		player.sendMessage(ChatColor.RED + "你已经拥有了此项功能");
        		return;
        	}
        	inventorymap.getInventory(playername).setSort(true);
           	player.sendMessage("你花费了 "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " 颗钻石, 成功的升级了背包功能 "+ ChatColor.YELLOW +" 智能背包!");
          	
        }
        else if(lines[1].compareTo(SEND)==0){
        	if(inventorymap.getInventory(playername).canSend()){
        		player.sendMessage(ChatColor.RED + "你已经拥有了此项功能");
        		return;
        	}
        	inventorymap.getInventory(playername).setSend(true);
           	player.sendMessage("你花费了 "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " 颗钻石, 成功的升级了背包功能 "+ ChatColor.YELLOW +" 物品发射器!");
          	
        }
        else{
        	player.sendMessage(ChatColor.RED + "错误: 无效的牌子");
        	return;
        }


		item.setAmount(itemnum-price);
		inventorymap.saveInventory(playername);
       	System.out.println("Inventory of "+ playername +" level up!");
       	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onSignBreak(BlockBreakEvent bbe){
		
		if(bbe.isCancelled())
			return;
		
		if(!(bbe.getBlock().getState() instanceof Sign))
			return;
		
		Sign sign = (Sign)bbe.getBlock().getState();
		
		if(sign.getLine(0).compareTo(TITLE)!=0)
			return;
		
		Player player = bbe.getPlayer();
		
        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
        {
             player.sendMessage(ChatColor.RED + "你没有这个操作的权限.");
             bbe.setCancelled(true);
             return;
        }
		
	}
}

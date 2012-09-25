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
	public static String TITLE = "[" + ChatColor.BLUE + "��������" + ChatColor.BLACK + "]";
	public static String PREUPDATE = "SizeUp";
	public static String UPDATE = ChatColor.BLUE + "��������";
	public static String PRETOCHEST = "ToChest";
	public static String TOCHEST = ChatColor.BLUE + "����װ����";
	public static String PREDROP = "Drop";
	public static String DROP = ChatColor.BLUE + "�����ϵĶ�";
	public static String PRESORT = "Sort";
	public static String SORT = ChatColor.BLUE + "���ܱ���";
	public static String PRESEND = "Send";
	public static String SEND = ChatColor.BLUE + "��Ʒ������";
	public static String PRICE = "�۸���ʯx";
	public static String NOTE = "����ʯ�Ҽ��������";

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
		
		Sign sign = (Sign)bpe.getBlock().getState();
		
		String title = bpe.getLine(0);
		if(!(title.equalsIgnoreCase(PRETITLE)||title.equalsIgnoreCase(TITLE))){
			return;
		}
		else
		{
            if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
            {
                 player.sendMessage("��û�����������Ȩ��.");
                 bpe.setCancelled(true);
                 bpe.getBlock().breakNaturally();
                 return;
            }
            
            String data = bpe.getLine(1);
            String sprice = bpe.getLine(2);
            if(data==null||sprice==null||(data.compareTo("")==0)||(sprice.compareTo("")==0)){
            	player.sendMessage("��Ч�Ĳ���");
            	bpe.setCancelled(true);
            	bpe.getBlock().breakNaturally();
            	return;
            }
            
            int len = sprice.length();
        	for(int j=0;j<len;j++){
        		char c = sprice.charAt(j);
        		if(c>'9'||c<'0'){
                	player.sendMessage("��Ч�ļ۸�");
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
            	player.sendMessage("��Ч��������Ŀ");
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
            player.sendMessage(ChatColor.RED + "��û�����������Ȩ��.");
            return;
        }
        if(inventorymap.getInventory(player)==null){
        	player.sendMessage(ChatColor.RED + "�㻹û����չ����,�޷�����������!");
        	return;
        }
        
        ItemStack item = player.getItemInHand();
        if(item.getType() != Material.DIAMOND){
        	player.sendMessage(ChatColor.RED + "�������û����ʯ!");
        	return;
        }
        int itemnum = item.getAmount();
        
        int price = Integer.parseInt(lines[2].split("x")[1]);
        //Object[] items = player.getInventory().all(Material.DIAMOND).values().toArray();
        if(itemnum<price){
        	player.sendMessage(ChatColor.RED + "��ʯ��������!");
        	return;
        }
        
        if(lines[1].compareTo(UPDATE)==0){

    		MineInventoryInventory inv = inventorymap.getInventory(playername); 
    		int size = inv.getInventory().getSize();
    		
    		if(size >= 54){
    			player.sendMessage(ChatColor.RED + "�����С�ı����޷�ͨ����������������");
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
           	
           	player.sendMessage("�㻨���� "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " ����ʯ, �ɹ��Ľ������������� "+ ChatColor.YELLOW + newsize +" ��!");
      	
        }
        else if(lines[1].compareTo(TOCHEST)==0){
        	if(inventorymap.getInventory(playername).canTochest()){
        		player.sendMessage(ChatColor.RED + "���Ѿ�ӵ���˴����");
        		return;
        	}
        	inventorymap.getInventory(playername).setTochest(true);
           	player.sendMessage("�㻨���� "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " ����ʯ, �ɹ��������˱������� "+ ChatColor.YELLOW +" ����װ����!");
          	
        }        
        else if(lines[1].compareTo(DROP)==0){
        	if(inventorymap.getInventory(playername).canDrop()){
        		player.sendMessage(ChatColor.RED + "���Ѿ�ӵ���˴����");
        		return;
        	}
        	inventorymap.getInventory(playername).setDrop(true);
           	player.sendMessage("�㻨���� "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " ����ʯ, �ɹ��������˱������� "+ ChatColor.YELLOW +" �����ϵĶ�!");
          	
        }
        else if(lines[1].compareTo(SORT)==0){
        	if(inventorymap.getInventory(playername).canSort())
        	{
        		player.sendMessage(ChatColor.RED + "���Ѿ�ӵ���˴����");
        		return;
        	}
        	inventorymap.getInventory(playername).setSort(true);
           	player.sendMessage("�㻨���� "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " ����ʯ, �ɹ��������˱������� "+ ChatColor.YELLOW +" ���ܱ���!");
          	
        }
        else if(lines[1].compareTo(SEND)==0){
        	if(inventorymap.getInventory(playername).canSend()){
        		player.sendMessage(ChatColor.RED + "���Ѿ�ӵ���˴����");
        		return;
        	}
        	inventorymap.getInventory(playername).setSend(true);
           	player.sendMessage("�㻨���� "+ ChatColor.YELLOW + price +ChatColor.WHITE+ " ����ʯ, �ɹ��������˱������� "+ ChatColor.YELLOW +" ��Ʒ������!");
          	
        }
        else{
        	player.sendMessage(ChatColor.RED + "����: ��Ч������");
        	return;
        }


		item.setAmount(itemnum-price);
		if(itemnum-price==0){
			player.setItemInHand(null);
		}
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
             player.sendMessage(ChatColor.RED + "��û�����������Ȩ��.");
             bbe.setCancelled(true);
             return;
        }
		
	}
}

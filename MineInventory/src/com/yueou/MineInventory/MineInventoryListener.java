package com.yueou.MineInventory;

import java.util.ListIterator;

//import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
//import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MineInventoryListener implements Listener{
	
	MineInventory plugin;
	
	public MineInventoryListener(MineInventory plugin){
		
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)	
	public void onPlayerDisconnect(PlayerQuitEvent pqe){
		String name = pqe.getPlayer().getName();
		//MineInventoryInventory minv = plugin.getMap().getMap().get(name);
		if(plugin.getMap().getInventory(name)==null){
			return;
		}
		/*
		ListIterator<ItemStack> invlist;
		ItemStack is;
		
		Inventory inv = minv.getInventory();
		
		String invinfo="";
		
		invlist = inv.iterator();
		
		while(invlist.hasNext()){
			is = invlist.next();
			if(is==null){
				if(invinfo.compareTo("")==0){
					invinfo = invinfo+"0:0";
				}
				else{
					invinfo=invinfo+":0:0";
				}				
			}
			else{
				if(invinfo.compareTo("")==0){
					invinfo = invinfo+is.getTypeId()+":"+is.getAmount();
				}
				else{
					invinfo=invinfo+":"+is.getTypeId()+":"+is.getAmount();
				}				
			}
		}
		*/
//		plugin.getServer().getPlayer(ice.getPlayer().getName()).sendMessage("监听到背包关闭事件,背包信息串: "+invinfo);
		//TODO:在下面进行数据库操作调用 进行背包数据存储
		
		plugin.getMap().saveInventory(name);

		//plugin.getChannel().sendClose((Player)pqe.getPlayer());
				
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent ice){
		
		
		//ListIterator<ItemStack> invlist;
		Inventory inv;
		//ItemStack is;
		//String name;
		
		//name = ice.getPlayer().getName();
		inv = ice.getInventory();
		//TODO 背包判别条件需要修改

		if(!(inv.getHolder() instanceof MineInventoryInventory)){
			return;
		}
		/*
		String invinfo="";
		
		invlist = inv.iterator();
		
		while(invlist.hasNext()){
			is = invlist.next();
			if(is==null){
				if(invinfo.compareTo("")==0){
					invinfo = invinfo+"0:0";
				}
				else{
					invinfo=invinfo+":0:0";
				}				
			}
			else{
				if(invinfo.compareTo("")==0){
					invinfo = invinfo+is.getTypeId()+":"+is.getAmount();
				}
				else{
					invinfo=invinfo+":"+is.getTypeId()+":"+is.getAmount();
				}				
			}
		}
		
//		ItemStack temp;
//		temp = new ItemStack(79);
//		temp.setAmount(10);
//		pinv.addItem(temp);
//		inv.addItem(temp);
		Bukkit.broadcastMessage(ice.getInventory().toString());
		Bukkit.broadcastMessage(pinv.toString());
		//TODO:无法保存，判定条件失败，待修改
		if(ice.getInventory()!= pinv){
			
			
			return;
		}*/
		/*
		Inventory pinv = plugin.getMap().getMap().get(name).getInventory();
		String pinvinfo = MineInventoryHash.getInventoryInfo(pinv);
		
		if(pinvinfo.compareTo(invinfo)!=0){
			
			MineInventoryInventory minv = (MineInventoryInventory)inv.getHolder();
			
			String targetplayername = minv.getOwner();
			
			String sql = "Update prefix_InventoryData SET invData = '" + invinfo +"' , Amount = " + inv.getSize() + " Where UserName = '" + targetplayername + "';";
			
			plugin.updateInventory(sql);
			
			System.out.println("[MineInventory]: Inventory of "+ name +" saved.");			
			return;
		}
		*/
//		plugin.getServer().getPlayer(ice.getPlayer().getName()).sendMessage("监听到背包关闭事件,背包信息串: "+invinfo);
		//TODO:在下面进行数据库操作调用 进行背包数据存储
		
		MineInventoryInventory minv = (MineInventoryInventory)inv.getHolder();
		
		String targetplayername = minv.getOwner();
		
		plugin.getMap().saveInventory(targetplayername);
		if(((Player)ice.getPlayer()).getName().equalsIgnoreCase(minv.getOwner())&&minv.stat==true){
			minv.stat = false;	
			plugin.getChannel().sendClose((Player)ice.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChestOpen(InventoryOpenEvent ioe){
		Inventory inv = ioe.getInventory();
		if(inv.getType()!=InventoryType.CHEST)
			return;
		if(inv.getHolder() instanceof MineInventoryInventory){
			return;
		}
		
		String playername = ioe.getPlayer().getName();
		String lastcmd =plugin.getCommandMap().getLastCommand(playername);
		
		if(lastcmd==null){
			return;
		}
		
	
		
		Player player = plugin.getServer().getPlayer(playername);
		
		Inventory chestinventory = ioe.getInventory();
		Inventory playerinventory = null;
		ListIterator<ItemStack> itemlist = null;
		ListIterator<ItemStack> citemlist = null;
		
		if(lastcmd.compareTo("tochest")==0){

			
			playerinventory = plugin.getMap().getInventory(playername).getInventory();
			
			itemlist = playerinventory.iterator();
			citemlist = chestinventory.iterator();
			ItemStack item = null; 
			int chestamount = 0;
			while(citemlist.hasNext()){
				if(citemlist.next()!=null)
					chestamount++;
			}
			
			for(int i=0;itemlist.hasNext();i++){
				item = (ItemStack) itemlist.next();
				if(item==null)continue;
				if(chestamount==chestinventory.getSize()){
					player.sendMessage("箱子空间不足，物品未能完全转移");
					break;
				}
				chestinventory.addItem(item);
				playerinventory.setItem(i, null);
				chestamount++;
			}
			
			player.sendMessage("额外背包内的物品已转移到箱子中");
			
		}
		else if(lastcmd.compareTo("topack")==0){
			
			
			playerinventory = plugin.getMap().getInventory(playername).getInventory();
			
			itemlist = playerinventory.iterator();
			citemlist = chestinventory.iterator();
			ItemStack item = null; 
			int chestamount = 0;
			while(itemlist.hasNext()){
				if(itemlist.next()!=null)
					chestamount++;
			}
			
			for(int i=0;citemlist.hasNext();i++){
				item = (ItemStack) citemlist.next();
			
				if(item==null)continue;
				if(chestamount==playerinventory.getSize()){
					player.sendMessage("额外背包空间不足，物品未能完全转移");
					break;
				}
				playerinventory.addItem(item);
				chestinventory.setItem(i, null);
				chestamount++;
			}
			
			player.sendMessage("箱子内的物品已转移到额外背包中");
		}
		else{
			return;
		}

		
		plugin.getMap().saveInventory(playername);
		
		plugin.getCommandMap().removeLastCommand(playername);
		//ioe.setCancelled(true);
		

	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInventoryOpen(InventoryOpenEvent event){
		
		//Bukkit.broadcastMessage("1");
		
		Inventory inv = event.getInventory();
		/*
		if(!(inv instanceof PlayerInventory)){
			return;
		}
		*/
		
		Player player = (Player) event.getPlayer();

		//Bukkit.broadcastMessage("1");
		if(inv.getHolder() instanceof MineInventoryInventory){

			//Bukkit.broadcastMessage("2");

			if(player.getName().equalsIgnoreCase(((MineInventoryInventory)inv.getHolder()).getOwner())){
				plugin.getChannel().sendOpen(player);
				((MineInventoryInventory)inv.getHolder()).stat = true;	
			}
			return;
		}
		
	}
}

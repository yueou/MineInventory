package com.yueou.MineInventory;

import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import com.iCo6.system.Account;

public class MineInventoryCommandExecutor implements CommandExecutor{

	private MineInventory plugin;
	private MineInventoryHash inventorymap;

	
	public MineInventoryCommandExecutor(MineInventory plugin){

		this.plugin=plugin;
		inventorymap = plugin.getMap();
	}

	@Override
	public boolean onCommand(CommandSender cmder, Command cmd, String str,
			String[] args) {
		
		// TODO Auto-generated method stub
		
		float createprice = plugin.getreader().getCreatePrice();		
		float luprice = plugin.getreader().getUpdatePrice();
		
        Player player = null;
        String playername = null;
       	
       	Account account = null;
       	
        if(cmder instanceof Player)
        {
                player = (Player)cmder;
                playername = cmder.getName();
                account = new Account(playername);
        }
        else return false;
        
        if(args.length >= 1){
        	
        	//MineInventory����
        	if(cmd.getName().equalsIgnoreCase("mi")){
        		
        		if(args.length==1){
        			 
        			if(args[0].equalsIgnoreCase("open")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                         else
                         {
                        	 /*
                             String name = player.getName();
                             Block block = plugin.getServer().getWorld("world").getBlockAt(0, 1, 0);
                             if(block.getType() !=Material.CHEST){
                            	 player.sendMessage(block.toString());
                            	 player.sendMessage("δ�ҵ�����");
                            	 return true;
                             }
                             chest=(Chest)block.getState();
                             
                             if(chest==null){
                            	 player.sendMessage("����δ֪����");
                            	 return true;
                             }
                             else{
                            	 Inventory tempinv = chestinv;

                            	 chestinv = chest.getInventory();
                            	 player.openInventory(chestinv);
                            	 
                             }
                             */
                        	 MineInventoryInventory minv = inventorymap.getInventory(playername);
                        	 
                        	 if(minv == null){
                        		 player.sendMessage("��û����չ������ ʹ��/mi create ����������");
                        		 return true;
                        	 }
                        	 else{
                        		 /*
                        		 ItemStack temp;
                        		 ListIterator<ItemStack> is;
                        		 is = inventorymap.getInventory(player).getItems();
                        		 for(int i=0;is.hasNext();i++){
                        			 temp=is.next();
//                        			 if(temp!=null)
//                        				 player.sendMessage("MI:  "+temp.getTypeId()+" : "+temp.getAmount());
                        		 }
                        		 */
                        		 minv.openInventory(player);
                        	 }                        	 
                        	 
                        	 
                             return true;
                         }

        			}
        			else if(args[0].equalsIgnoreCase("create")){
        				
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û�д�����չ������Ȩ��.");
                             return true;
                        }
                         else
                         {
                        	 if(inventorymap.getInventory(playername)!=null){
                        		 player.sendMessage("���Ѿ�ӵ������չ����");
                        		 return true;
                        	 }
                        	 else{
                        		 
                        		 player.sendMessage("����������Ứ���� "+ createprice +" Ԫ, ������/mi confirm ȷ�ϲ���.");
                        		 plugin.getCommandMap().setLastCommand(cmder.getName(), args[0]);
                        		 
                        	 }
                        	 
                        	 return true;
                         }
        				
        			}
        			else if(args[0].equalsIgnoreCase("levelup")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                            player.sendMessage("Ȩ�޲���.");
                            return true;
                        } 
                        
                        MineInventoryInventory inv = inventorymap.getInventory(playername); 
	                   	if(inv==null){
	                   		player.sendMessage("�㻹û����չ����, �޷�����������");
	                   		return true;
	                   	}
	                   	int invsize = inv.getInventory().getSize();
	                   	if(invsize == 54){

	                   		player.sendMessage("��ı����Ѿ���������, �޷���������");
	                   		return true;
	                   	}
	                   	

	                   	
	                   	player.sendMessage("�����������Ứ����" + luprice + "Ԫ, ȷ������������/mi confirm");
	                   	
	                   	plugin.getCommandMap().setLastCommand(cmder.getName(), args[0]);
	                   
	                   	
	                   	return true;
	                   	
        			}
        			else if(args[0].equalsIgnoreCase("remove")||args[0].equalsIgnoreCase("delete")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                         else
                         {
                        	 if(inventorymap.getInventory(playername)==null){
                        		 player.sendMessage("�㻹û����չ����");
                        		 return true;
                        	 }
                        	 else{
                        		 
                        		 player.sendMessage("���������ɾ�����������е���Ʒ�ͱ��������޷�������ȷ��ɾ��������/mi confirm");
                        		 plugin.getCommandMap().setLastCommand(playername, args[0]);
         
                        	 }
                        	 
                        	 return true;
                         }
        				
        			}
        			else if(args[0].equalsIgnoreCase("drop")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                        else{
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("�㻹û����չ����");
	                    		 return true;
	                    	 }
	                       	 

	                       	 if(!(inventorymap.getInventory(playername).canDrop()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("�㻹û�й�����չ����: "+ChatColor.GREEN +"�����ϵĶ�");
	                       		 return true;
	                       	 }
	                       	 
	                       	 Inventory inv = inventorymap.getInventory(playername).getInventory();
	                       	 ListIterator<ItemStack> itemlist = inv.iterator();
	                       	 
	                       	 World playerworld = player.getWorld();
	      
	                       	 Location playerlocation = player.getLocation();
	                       	 ItemStack is = null;
	                       	 
	                       	 while(itemlist.hasNext()){
	                       		 is = itemlist.next();
	                       		 if(is == null)continue;
	                       		 playerworld.dropItemNaturally(playerlocation, is).setPickupDelay(100);
	                       	//	 pw.dropItem(pl, is);
	                       	 }
	                       	 inv.clear();
	                       	 
	                       	 player.sendMessage("����~");
	                       	 
	         				
	                       	 inventorymap.saveInventory(playername);
	         				
	                       	 return true;
	                       	 
	                       	 
                        }
        			}
        			else if(args[0].equalsIgnoreCase("sort")){
        				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                      	if(inventorymap.getInventory(playername)==null){
                      		 player.sendMessage("�㻹û����չ����");
                      		 return true;
                      	}
                      	
                      	if(!(inventorymap.getInventory(playername).canSort()||plugin.getPermissionHandler().has(player, "mi.admin"))){
                      		player.sendMessage("�㻹û�й�����չ����: "+ChatColor.GREEN +"���ܱ���");
                      	}
        				player.sendMessage("�ѽ���������Ʒ������ƷID������������.");
        				
        				plugin.getMap().getInventory(playername).sortInventory();
        				plugin.getMap().saveInventory(playername);
        				
        				return true;
        			}
        			else if(args[0].equalsIgnoreCase("tochest")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                        else{
             
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("�㻹û����չ����");
	                    		 return true;
	                    	 }
	                       	 
	                       	 if(!(inventorymap.getInventory(playername).canTochest()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("�㻹û�й�����չ����: "+ChatColor.GREEN +"����װ��");
	                       		 return true;
	                       	 }
	                       	 player.sendMessage("���Ҫ������Ʒת�Ƶ�����");
	                       	 
	                       	 plugin.getCommandMap().setLastCommand(playername, args[0]);
	                       	 
	                       	 return true;
                        }
        			}
        			else if(args[0].equalsIgnoreCase("topack")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                        else{
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("�㻹û����չ����");
	                    		 return true;
	                    	 }

	                       	 if(!(inventorymap.getInventory(playername).canTochest()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("�㻹û�й�����չ����: "+ChatColor.GREEN +"����װ��");
	                       		 return true;
	                       	 }
	                       	 player.sendMessage("���Ҫ������Ʒת�Ƶ�����");
	                       	 
	                       	 plugin.getCommandMap().setLastCommand(playername, args[0]);
	                       	 
	                       	 return true;
                        }
        			}
        			else if(args[0].equalsIgnoreCase("reload")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
        				
        				plugin.onReload();
        				player.sendMessage("MineInventory v" + plugin.getDescription().getVersion() +" by yueou ����������!");
        				
        				return true;
        			}
        			else if(args[0].equalsIgnoreCase("confirm")){
        				MineInventoryCommandHash cm = plugin.getCommandMap();
        				
        				String lastcmd = cm.getLastCommand(playername);
        				if(lastcmd==null){
        					player.sendMessage("��������");
        					return true;
        				}
        				
        				else if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
                        
        				else if(lastcmd.equalsIgnoreCase("create")){
        					
                     		 if(account.getHoldings().getBalance()<createprice){
                    			 player.sendMessage("��Ľ�Ǯ����");
                    			 cm.removeLastCommand(playername);
                    			 return true;
                    		 }
                    		 account.getHoldings().subtract(createprice);
                    		 
                    		 boolean res = inventorymap.addInventory(playername, 18);
                    		 
                    		 if(res == true){
                    			 player.sendMessage("������ " +createprice+ "Ԫ, ������չ�����ɹ�");
    	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount ,invData) Values('" +
    	                				 playername + "',27,'0:0');";
                        		 plugin.registerInventory(sql);
                        		 
                        		 System.out.println("[MineInventory]Inventory of " + player.getName() +" created.");
                    		 }
                    		 else{
                    			 player.sendMessage("����δ֪����");
                    		 }
                    		 

                    		 
                    		 
                    		 cm.removeLastCommand(playername);
                    		 return true;
                    				 	
        				}
        				else if(lastcmd.equalsIgnoreCase("remove")||lastcmd.equalsIgnoreCase("delete")){
        					
            				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                            {
                                 player.sendMessage("��û��ʹ����չ������Ȩ��.");
                                 return true;
                            }
            				
	                   		 boolean res = inventorymap.removeInventory(playername);
	                   		 
	                   		 String sql = "Delete From prefix_InventoryData Where UserName = '" + player.getName() +"';";
	                   		 
	                   		 plugin.removeInventory(sql);
	                   		 
	                   		 if(res == true){
	                   			 player.sendMessage("ɾ����չ�����ɹ�");
	                   			 System.out.println("[MineInventory]Inventory of " + player.getName() +" removed.");
	                   		 }
	                   		 else{
	                   			 player.sendMessage("����δ֪����");
	                   		 }
	                   		 
	                   		 cm.removeLastCommand(playername);
	                   		 return true;
        				}
        				else if(lastcmd.equalsIgnoreCase("levelup")){
            				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                            {
                                 player.sendMessage("��û��ʹ����չ������Ȩ��.");
                                 return true;
                            }       		
	                		if(account.getHoldings().getBalance()<luprice){
	                			player.sendMessage("��Ľ�Ǯ����");
	                			cm.removeLastCommand(playername);
	                			return true;
	                		}
	                		MineInventoryInventory inv = inventorymap.getInventory(playername); 
	                		
		                   	ListIterator<ItemStack> itemlist = inv.getInventory().iterator();
		                   	MineInventoryInventory newinventory = new MineInventoryInventory(playername,54);
		                   	Inventory newinv = newinventory.getInventory();
		                   	
		                   	for(int i=0;itemlist.hasNext();i++){
		                   		newinv.setItem(i, itemlist.next());
		                   	}
		                   	inventorymap.removeInventory(playername);
		                   	inventorymap.addInventory(playername, newinventory);
		                   	
	        				inventorymap.saveInventory(playername);
	    
		                   	player.sendMessage("��ɹ��������˱���");
		                   	System.out.println("Inventory of "+ playername +" level up!");
		                   	
		                   	cm.removeLastCommand(playername);
		                   	
		                   	return true;
        				}
        			}
        			else if(args[0].equalsIgnoreCase("help")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("��û��ʹ����չ������Ȩ��.");
                             return true;
                        }
        				
        				player.sendMessage("�鿴 MineInventory ���������:");
        				player.sendMessage("/mi create : Ϊ�Լ�����һ����չ����");
        				player.sendMessage("/mi open : ���Լ�����չ����");
        				player.sendMessage("/mi sort : ����չ���������Ʒ������ƷID�������������("+ChatColor.GREEN+"���ܱ��� "+ChatColor.WHITE+")");
        				player.sendMessage("/mi remove(delete) : ɾ���Լ�����չ����");
        				player.sendMessage("/mi tochest : ����չ������Ķ���ת�Ƶ�Ŀ��������("+ChatColor.GREEN+"����װ�� "+ChatColor.WHITE+")");
        				player.sendMessage("/mi topack : ��Ŀ��������Ķ���ת�Ƶ���չ������( "+ChatColor.GREEN+"����װ��"+ChatColor.WHITE+")");
        				player.sendMessage("/mi drop : ����չ������Ķ���ȫ���ӵ�����("+ChatColor.GREEN+"�����ϵĶ� "+ChatColor.WHITE+")");
        				player.sendMessage("/mi send <�����> : ����չ�����ڵĶ������͵����˵���չ����("+ChatColor.GREEN+"��Ʒ������ "+ChatColor.WHITE+")");
        				player.sendMessage("/mi help : �鿴������ͨ����");
        				player.sendMessage("/mi adminhelp : �鿴��������");
        				player.sendMessage("MineInventory �汾�� v" + plugin.getDescription().getVersion() + " Author by yueou");
        				return true;
        			}        			
        			else if(args[0].equalsIgnoreCase("adminhelp")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��ʹ����������Ȩ��.");//
                             return true;
                        }
        				
        				player.sendMessage("�鿴 MineInventory ����������:");
        				player.sendMessage("/mi create : Ϊ�Լ�����һ����չ����");
        				player.sendMessage("/mi open : ���Լ�����չ����");
        				player.sendMessage("/mi sort : ����չ���������Ʒ������ƷID�������������");
        				player.sendMessage("/mi remove(delete) : ɾ���Լ�����չ����");
        				player.sendMessage("/mi levelup : ���Լ�����չ��������");
        				player.sendMessage("/mi tochest : ����չ������Ķ���ת�Ƶ�Ŀ��������");
        				player.sendMessage("/mi topack : ��Ŀ��������Ķ���ת�Ƶ���չ������");
        				player.sendMessage("/mi drop : ����չ������Ķ���ȫ���ӵ�����");
        				player.sendMessage("/mi help : �鿴������ͨ����");
        				player.sendMessage("/mi createfor <�����> : ΪĿ����Ҵ���һ����չ����");
        				player.sendMessage("/mi inspect <�����> : ����Ŀ����ҵ���չ����");
        				player.sendMessage("/mi clearfrom <�����> : ���Ŀ����ҵ���չ����");
        				player.sendMessage("/mi full <��ƷID/��Ʒ����> : ��ĳ����Ʒ�����Լ�����չ����");
        				player.sendMessage("/mi reload : ���ز��");
        				player.sendMessage("/mi adminhelp : �鿴���й���Աר������");
        				player.sendMessage("MineInventory �汾�� v" + plugin.getDescription().getVersion());
        				return true;
        			}
        		}
        		else if(args.length==2){
        			if(args[0].equalsIgnoreCase("inspect")){
        				String targetplayername = args[1];
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer == null){
                        	player.sendMessage("δ�ҵ����");
                        	return true;
                        }
                        targetplayername = targetplayer.getName();
                        
                        MineInventoryInventory targetinventory = 	inventorymap.getInventory(targetplayername);
                        
        				if(targetinventory == null){
        					player.sendMessage("δ�ҵ�����ҵ���չ����.");
        					return true;
        				}
        				player.openInventory(targetinventory.getInventory());
        				return true;
        			}
        			if(args[0].equalsIgnoreCase("createfor")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer==null){
                        	
                        	player.sendMessage("δ�ҵ��������,����ʧ��");
                        	return true;
                        }
                        String targetplayername = targetplayer.getName();
                        
                        if(inventorymap.getInventory(targetplayername)!=null){
                        	player.sendMessage("������Ѿ�ӵ������չ����");
                        	return true;
                        }
                        
                        boolean res = inventorymap.addInventory(targetplayername, 9);
	               		 if(res == true){	               			 
	               			 player.sendMessage("Ϊ " + targetplayername +" ������һ������.");
	            			 targetplayer.sendMessage("����ԱΪ�㴴����һ����չ����");
	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount , invData) Values('" +
	                				 targetplayername + "',27,'0:0');";
	                		 plugin.registerInventory(sql);
	                		 
	                		 System.out.println("[MineInventory]Inventory of " + targetplayername +" created.");
	            		 }
	            		 else{
	            			 player.sendMessage("����δ֪����");
	            		 }
	               		 return true;
        			}
        			if(args[0].equalsIgnoreCase("clearfrom")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer == null){
                        	player.sendMessage("����Ҳ�����,�޷���ձ���");
                        	return true;
                        }
                        
                        String targetplayername = targetplayer.getName();
                        
                        if(targetplayername.compareTo(args[1].toLowerCase())!=0){
                        	player.sendMessage("��ձ�������ʹ����ȫƥ��������");
                        	return true;
                        }
                        
                        if(inventorymap.getInventory(targetplayer)==null){
                        	player.sendMessage("����û����չ����");
                        	return true;
                        }
                        
        				Inventory targetinventory = inventorymap.getInventory(targetplayer).getInventory();
        				targetinventory.clear();
        				player.sendMessage("����� " + targetplayername + " ����չ����");
        				targetplayer.sendMessage("�����չ����������Ա�����");
        				
        				inventorymap.saveInventory(targetplayername);
        				
        				return true;
        			}
        			if(args[0].equalsIgnoreCase("send")){

                        if(!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage(ChatColor.RED+"��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        
                        if(!inventorymap.getInventory(playername).canSend()){
                        	player.sendMessage("�㻹û�й�����չ����: "+ChatColor.GREEN +"��Ʒ������");
                        	return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer==null){
                        	player.sendMessage(ChatColor.RED+"û���ҵ����");
                        	return true;
                        }
                        
                        if(targetplayer==player){
                        	player.sendMessage(ChatColor.RED+"�㲻�ܷ�����Ʒ�������");
                        	return true;
                        }
                        if(inventorymap.getInventory(targetplayer)==null){
                        	player.sendMessage("Ŀ����һ�û����չ����");
                        	return true;
                        }
                        
                        Inventory playerinventory = inventorymap.getInventory(playername).getInventory();
                        Inventory targetinventory = inventorymap.getInventory(targetplayer).getInventory();
                        
                        ListIterator<ItemStack> pinvlist = playerinventory.iterator();
                        
                        ListIterator<ItemStack> tinvlist = targetinventory.iterator();
                        int size = targetinventory.getSize();
                        int amount = 0;
                        while(tinvlist.hasNext()){
                        	if(tinvlist.next()!=null){
                        		amount++;
                        	}
                        }
                        
                        int oldamount = amount;
                        ItemStack item = null;
                        
                        for(int i=0;pinvlist.hasNext();i++){
                        	if(amount==size){
                        		player.sendMessage("Ŀ�������չ������������Ʒδ����ȫ����");
                        		break;
                        	}
                        	item = pinvlist.next();
                        	if(item==null)
                        		continue;
                        	targetinventory.addItem(item);
                        	playerinventory.setItem(i, null);
                        	amount++;
                        }
                        
                        if(amount==oldamount){
                        	player.sendMessage(ChatColor.RED+"�����չ�����ǿյ�!");
                        	return true;
                        }
                        
                        player.sendMessage("��Ʒ�ѷ���!");
                        targetplayer.sendMessage(ChatColor.GREEN + playername + ChatColor.WHITE + "�������չ�����з�����һЩ����");
                        inventorymap.saveInventory(playername);
                        inventorymap.saveInventory(targetplayer.getName());
                        return true;
        			}
        			if(args[0].equalsIgnoreCase("full")){
        				
        				boolean stat;
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        
                        if(inventorymap.getInventory(player.getName())==null){
                        	player.sendMessage("�㻹û����չ����");
                        	return true;
                        }
                        
                        int itemid = 0;
                        /*
                        if(itemid==0){
                        	player.sendMessage("����������");
                        	return true;                       	
                        }
                        */
                        args[1]=args[1].toUpperCase();
                        int len = args[1].length();
                        	
                        if(Material.getMaterial(args[1])==null){
                        	stat = false;
                        	for(int j=0;j<len;j++){
                        		char c = args[1].charAt(j);
                        		if(c>'9'||c<'0'){
                                	player.sendMessage("û���ҵ���Ʒ  " + args[1]);
                                	return true;
                        		}
                        	}
                        	itemid = Integer.parseInt(args[1]);
                        	if(Material.getMaterial(itemid)==null){

                            	player.sendMessage("û���ҵ���Ʒ  " + args[1]);
                            	return true;
                        	}
                        }
                        else{
                        	stat = true;
                        }
                        
                        
                        Inventory inv = inventorymap.getInventory(player).getInventory();
                        
                        int size = inv.getSize();
                        ItemStack item;
                        
                        if(stat == false){
                            
                            for(int i=0;i<size;i++){
                        		item = new ItemStack(itemid);
                        		item.setAmount(64);
                        		inv.setItem(i, item);
                            }
                            
                        }
                        else{
                            for(int i=0;i<size;i++){
                        		item = new ItemStack(Material.getMaterial(args[1]));
                        		item.setAmount(64);
                        		inv.setItem(i, item);
                            }
                        }
                        player.sendMessage("������!");
                        
                        return true;
        			}
        		}
        		else if(args.length==3){
        			if(args[0].equalsIgnoreCase("levelupfor")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        
        			}
        			if(args[0].equalsIgnoreCase("createfor")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("��û��ʹ����������Ȩ��.");
                             return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        
                        int size = 0;
                        int len = args[2].length();
                        
                    	for(int j=0;j<len;j++){
                    		char c = args[2].charAt(j);
                    		if(c>'9'||c<'0'){
                            	player.sendMessage("����Ĵ�С����  " + args[2]);
                            	return true;
                    		}
                    	}
                    	size = Integer.parseInt(args[2]);
                    	
                        if(targetplayer==null){
                        	
                        	player.sendMessage("δ�ҵ��������,����ʧ��");
                        	return true;
                        }
                        String targetplayername = targetplayer.getName();
                        
                        if(inventorymap.getInventory(targetplayername)!=null){
                        	player.sendMessage("������Ѿ�ӵ������չ����");
                        	return true;
                        }
                        if(size == 0 )
                        {
                        	player.sendMessage("���ڿ���Ц?");
                        	return true;
                        }
                
                    	if(size>90){
                    		player.sendMessage("�Ҳ�!��ô��İ�,���������찡!?");
                    		return true;
                    	}
                    	if(size%9!=0){
                    		player.sendMessage("�����Ĵ�С����Ϊ��ı���");
                    		return true;
                    	}
                    	
                        boolean res = inventorymap.addInventory(targetplayername, size);
	               		 if(res == true){	               			 
	               			 player.sendMessage("Ϊ " + targetplayername +" ������һ������.");
	            			 targetplayer.sendMessage("����ԱΪ�㴴����һ����չ����");
	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount ,invData) Values('" +
	                				 targetplayername + "',27,'0:0');";
	                		 plugin.registerInventory(sql);
	                		 
	                		 System.out.println("[MineInventory]Inventory of " + targetplayername +" created.");
	            		 }
	            		 else{
	            			 player.sendMessage("����δ֪����");
	            		 }
	               		 return true;
        			}
        			
        		}
        	}
        }
        player.sendMessage("������Ч, ��ʹ��/mi help ���鿴��ϸ���������.");
		return true;
	}

}

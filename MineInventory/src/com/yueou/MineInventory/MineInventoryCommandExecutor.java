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
        	
        	//MineInventory命令
        	if(cmd.getName().equalsIgnoreCase("mi")){
        		
        		if(args.length==1){
        			 
        			if(args[0].equalsIgnoreCase("open")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                         else
                         {
                        	 /*
                             String name = player.getName();
                             Block block = plugin.getServer().getWorld("world").getBlockAt(0, 1, 0);
                             if(block.getType() !=Material.CHEST){
                            	 player.sendMessage(block.toString());
                            	 player.sendMessage("未找到背包");
                            	 return true;
                             }
                             chest=(Chest)block.getState();
                             
                             if(chest==null){
                            	 player.sendMessage("发生未知错误");
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
                        		 player.sendMessage("你没有扩展背包， 使用/mi create 来创建背包");
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
                             player.sendMessage("你没有创建扩展背包的权限.");
                             return true;
                        }
                         else
                         {
                        	 if(inventorymap.getInventory(playername)!=null){
                        		 player.sendMessage("你已经拥有了扩展背包");
                        		 return true;
                        	 }
                        	 else{
                        		 
                        		 player.sendMessage("这个操作将会花费你 "+ createprice +" 元, 请输入/mi confirm 确认操作.");
                        		 plugin.getCommandMap().setLastCommand(cmder.getName(), args[0]);
                        		 
                        	 }
                        	 
                        	 return true;
                         }
        				
        			}
        			else if(args[0].equalsIgnoreCase("levelup")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                            player.sendMessage("权限不足.");
                            return true;
                        } 
                        
                        MineInventoryInventory inv = inventorymap.getInventory(playername); 
	                   	if(inv==null){
	                   		player.sendMessage("你还没有扩展背包, 无法给背包升级");
	                   		return true;
	                   	}
	                   	int invsize = inv.getInventory().getSize();
	                   	if(invsize == 54){

	                   		player.sendMessage("你的背包已经是最大的了, 无法继续升级");
	                   		return true;
	                   	}
	                   	

	                   	
	                   	player.sendMessage("升级背包将会花费你" + luprice + "元, 确认升级请输入/mi confirm");
	                   	
	                   	plugin.getCommandMap().setLastCommand(cmder.getName(), args[0]);
	                   
	                   	
	                   	return true;
	                   	
        			}
        			else if(args[0].equalsIgnoreCase("remove")||args[0].equalsIgnoreCase("delete")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                         else
                         {
                        	 if(inventorymap.getInventory(playername)==null){
                        		 player.sendMessage("你还没有扩展背包");
                        		 return true;
                        	 }
                        	 else{
                        		 
                        		 player.sendMessage("这个操作会删除背包里所有的物品和背包，并无法撤销，确认删除请输入/mi confirm");
                        		 plugin.getCommandMap().setLastCommand(playername, args[0]);
         
                        	 }
                        	 
                        	 return true;
                         }
        				
        			}
        			else if(args[0].equalsIgnoreCase("drop")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                        else{
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("你还没有扩展背包");
	                    		 return true;
	                    	 }
	                       	 

	                       	 if(!(inventorymap.getInventory(playername).canDrop()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("你还没有购买扩展功能: "+ChatColor.GREEN +"背包上的洞");
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
	                       	 
	                       	 player.sendMessage("哗啦~");
	                       	 
	         				
	                       	 inventorymap.saveInventory(playername);
	         				
	                       	 return true;
	                       	 
	                       	 
                        }
        			}
        			else if(args[0].equalsIgnoreCase("sort")){
        				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                      	if(inventorymap.getInventory(playername)==null){
                      		 player.sendMessage("你还没有扩展背包");
                      		 return true;
                      	}
                      	
                      	if(!(inventorymap.getInventory(playername).canSort()||plugin.getPermissionHandler().has(player, "mi.admin"))){
                      		player.sendMessage("你还没有购买扩展功能: "+ChatColor.GREEN +"智能背包");
                      	}
        				player.sendMessage("已将背包内物品按照物品ID进行整理排序.");
        				
        				plugin.getMap().getInventory(playername).sortInventory();
        				plugin.getMap().saveInventory(playername);
        				
        				return true;
        			}
        			else if(args[0].equalsIgnoreCase("tochest")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                        else{
             
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("你还没有扩展背包");
	                    		 return true;
	                    	 }
	                       	 
	                       	 if(!(inventorymap.getInventory(playername).canTochest()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("你还没有购买扩展功能: "+ChatColor.GREEN +"快速装箱");
	                       		 return true;
	                       	 }
	                       	 player.sendMessage("请打开要进行物品转移的箱子");
	                       	 
	                       	 plugin.getCommandMap().setLastCommand(playername, args[0]);
	                       	 
	                       	 return true;
                        }
        			}
        			else if(args[0].equalsIgnoreCase("topack")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                        else{
	                       	 if(inventorymap.getInventory(playername)==null){
	                    		 player.sendMessage("你还没有扩展背包");
	                    		 return true;
	                    	 }

	                       	 if(!(inventorymap.getInventory(playername).canTochest()||plugin.getPermissionHandler().has(player, "mi.admin"))){
	                       		 player.sendMessage("你还没有购买扩展功能: "+ChatColor.GREEN +"快速装箱");
	                       		 return true;
	                       	 }
	                       	 player.sendMessage("请打开要进行物品转移的箱子");
	                       	 
	                       	 plugin.getCommandMap().setLastCommand(playername, args[0]);
	                       	 
	                       	 return true;
                        }
        			}
        			else if(args[0].equalsIgnoreCase("reload")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
        				
        				plugin.onReload();
        				player.sendMessage("MineInventory v" + plugin.getDescription().getVersion() +" by yueou 设置已重载!");
        				
        				return true;
        			}
        			else if(args[0].equalsIgnoreCase("confirm")){
        				MineInventoryCommandHash cm = plugin.getCommandMap();
        				
        				String lastcmd = cm.getLastCommand(playername);
        				if(lastcmd==null){
        					player.sendMessage("不明操作");
        					return true;
        				}
        				
        				else if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
                        
        				else if(lastcmd.equalsIgnoreCase("create")){
        					
                     		 if(account.getHoldings().getBalance()<createprice){
                    			 player.sendMessage("你的金钱不足");
                    			 cm.removeLastCommand(playername);
                    			 return true;
                    		 }
                    		 account.getHoldings().subtract(createprice);
                    		 
                    		 boolean res = inventorymap.addInventory(playername, 18);
                    		 
                    		 if(res == true){
                    			 player.sendMessage("花费了 " +createprice+ "元, 创建扩展背包成功");
    	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount ,invData) Values('" +
    	                				 playername + "',27,'0:0');";
                        		 plugin.registerInventory(sql);
                        		 
                        		 System.out.println("[MineInventory]Inventory of " + player.getName() +" created.");
                    		 }
                    		 else{
                    			 player.sendMessage("发生未知错误");
                    		 }
                    		 

                    		 
                    		 
                    		 cm.removeLastCommand(playername);
                    		 return true;
                    				 	
        				}
        				else if(lastcmd.equalsIgnoreCase("remove")||lastcmd.equalsIgnoreCase("delete")){
        					
            				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                            {
                                 player.sendMessage("你没有使用扩展背包的权限.");
                                 return true;
                            }
            				
	                   		 boolean res = inventorymap.removeInventory(playername);
	                   		 
	                   		 String sql = "Delete From prefix_InventoryData Where UserName = '" + player.getName() +"';";
	                   		 
	                   		 plugin.removeInventory(sql);
	                   		 
	                   		 if(res == true){
	                   			 player.sendMessage("删除扩展背包成功");
	                   			 System.out.println("[MineInventory]Inventory of " + player.getName() +" removed.");
	                   		 }
	                   		 else{
	                   			 player.sendMessage("发生未知错误");
	                   		 }
	                   		 
	                   		 cm.removeLastCommand(playername);
	                   		 return true;
        				}
        				else if(lastcmd.equalsIgnoreCase("levelup")){
            				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                            {
                                 player.sendMessage("你没有使用扩展背包的权限.");
                                 return true;
                            }       		
	                		if(account.getHoldings().getBalance()<luprice){
	                			player.sendMessage("你的金钱不足");
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
	    
		                   	player.sendMessage("你成功的升级了背包");
		                   	System.out.println("Inventory of "+ playername +" level up!");
		                   	
		                   	cm.removeLastCommand(playername);
		                   	
		                   	return true;
        				}
        			}
        			else if(args[0].equalsIgnoreCase("help")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage("你没有使用扩展背包的权限.");
                             return true;
                        }
        				
        				player.sendMessage("查看 MineInventory 的玩家命令:");
        				player.sendMessage("/mi create : 为自己创建一个扩展背包");
        				player.sendMessage("/mi open : 打开自己的扩展背包");
        				player.sendMessage("/mi sort : 将扩展背包里的物品按照物品ID进行排序和整理("+ChatColor.GREEN+"智能背包 "+ChatColor.WHITE+")");
        				player.sendMessage("/mi remove(delete) : 删除自己的扩展背包");
        				player.sendMessage("/mi tochest : 将扩展背包里的东西转移到目标箱子中("+ChatColor.GREEN+"快速装箱 "+ChatColor.WHITE+")");
        				player.sendMessage("/mi topack : 将目标箱子里的东西转移到扩展背包中( "+ChatColor.GREEN+"快速装箱"+ChatColor.WHITE+")");
        				player.sendMessage("/mi drop : 将扩展背包里的东西全部扔到地下("+ChatColor.GREEN+"背包上的洞 "+ChatColor.WHITE+")");
        				player.sendMessage("/mi send <玩家名> : 将扩展背包内的东西发送到别人的扩展背包("+ChatColor.GREEN+"物品发射器 "+ChatColor.WHITE+")");
        				player.sendMessage("/mi help : 查看所有普通命令");
        				player.sendMessage("/mi adminhelp : 查看所有命令");
        				player.sendMessage("MineInventory 版本号 v" + plugin.getDescription().getVersion() + " Author by yueou");
        				return true;
        			}        			
        			else if(args[0].equalsIgnoreCase("adminhelp")){
        				
        				if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你使用这个命令的权限.");//
                             return true;
                        }
        				
        				player.sendMessage("查看 MineInventory 的所有命令:");
        				player.sendMessage("/mi create : 为自己创建一个扩展背包");
        				player.sendMessage("/mi open : 打开自己的扩展背包");
        				player.sendMessage("/mi sort : 将扩展背包里的物品按照物品ID进行排序和整理");
        				player.sendMessage("/mi remove(delete) : 删除自己的扩展背包");
        				player.sendMessage("/mi levelup : 给自己的扩展背包扩容");
        				player.sendMessage("/mi tochest : 将扩展背包里的东西转移到目标箱子中");
        				player.sendMessage("/mi topack : 将目标箱子里的东西转移到扩展背包中");
        				player.sendMessage("/mi drop : 将扩展背包里的东西全部扔到地下");
        				player.sendMessage("/mi help : 查看所有普通命令");
        				player.sendMessage("/mi createfor <玩家名> : 为目标玩家创建一个扩展背包");
        				player.sendMessage("/mi inspect <玩家名> : 监视目标玩家的扩展背包");
        				player.sendMessage("/mi clearfrom <玩家名> : 清空目标玩家的扩展背包");
        				player.sendMessage("/mi full <物品ID/物品名称> : 用某种物品填满自己的扩展背包");
        				player.sendMessage("/mi reload : 重载插件");
        				player.sendMessage("/mi adminhelp : 查看所有管理员专用命令");
        				player.sendMessage("MineInventory 版本号 v" + plugin.getDescription().getVersion());
        				return true;
        			}
        		}
        		else if(args.length==2){
        			if(args[0].equalsIgnoreCase("inspect")){
        				String targetplayername = args[1];
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer == null){
                        	player.sendMessage("未找到玩家");
                        	return true;
                        }
                        targetplayername = targetplayer.getName();
                        
                        MineInventoryInventory targetinventory = 	inventorymap.getInventory(targetplayername);
                        
        				if(targetinventory == null){
        					player.sendMessage("未找到该玩家的扩展背包.");
        					return true;
        				}
        				player.openInventory(targetinventory.getInventory());
        				return true;
        			}
        			if(args[0].equalsIgnoreCase("createfor")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer==null){
                        	
                        	player.sendMessage("未找到在线玩家,创建失败");
                        	return true;
                        }
                        String targetplayername = targetplayer.getName();
                        
                        if(inventorymap.getInventory(targetplayername)!=null){
                        	player.sendMessage("该玩家已经拥有了扩展背包");
                        	return true;
                        }
                        
                        boolean res = inventorymap.addInventory(targetplayername, 9);
	               		 if(res == true){	               			 
	               			 player.sendMessage("为 " + targetplayername +" 创建了一个背包.");
	            			 targetplayer.sendMessage("管理员为你创建了一个扩展背包");
	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount , invData) Values('" +
	                				 targetplayername + "',27,'0:0');";
	                		 plugin.registerInventory(sql);
	                		 
	                		 System.out.println("[MineInventory]Inventory of " + targetplayername +" created.");
	            		 }
	            		 else{
	            			 player.sendMessage("发生未知错误");
	            		 }
	               		 return true;
        			}
        			if(args[0].equalsIgnoreCase("clearfrom")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer == null){
                        	player.sendMessage("该玩家不在线,无法清空背包");
                        	return true;
                        }
                        
                        String targetplayername = targetplayer.getName();
                        
                        if(targetplayername.compareTo(args[1].toLowerCase())!=0){
                        	player.sendMessage("清空背包必须使用完全匹配的玩家名");
                        	return true;
                        }
                        
                        if(inventorymap.getInventory(targetplayer)==null){
                        	player.sendMessage("该玩没有扩展背包");
                        	return true;
                        }
                        
        				Inventory targetinventory = inventorymap.getInventory(targetplayer).getInventory();
        				targetinventory.clear();
        				player.sendMessage("清空了 " + targetplayername + " 的扩展背包");
        				targetplayer.sendMessage("你的扩展背包被管理员清空了");
        				
        				inventorymap.saveInventory(targetplayername);
        				
        				return true;
        			}
        			if(args[0].equalsIgnoreCase("send")){

                        if(!(plugin.getPermissionHandler().has(player, "mi.use"))) 
                        {
                             player.sendMessage(ChatColor.RED+"你没有使用这个命令的权限.");
                             return true;
                        }
                        
                        if(!inventorymap.getInventory(playername).canSend()){
                        	player.sendMessage("你还没有购买扩展功能: "+ChatColor.GREEN +"物品发射器");
                        	return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        if(targetplayer==null){
                        	player.sendMessage(ChatColor.RED+"没有找到玩家");
                        	return true;
                        }
                        
                        if(targetplayer==player){
                        	player.sendMessage(ChatColor.RED+"你不能发送物品给该玩家");
                        	return true;
                        }
                        if(inventorymap.getInventory(targetplayer)==null){
                        	player.sendMessage("目标玩家还没有扩展背包");
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
                        		player.sendMessage("目标玩家扩展背包已满，物品未能完全发送");
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
                        	player.sendMessage(ChatColor.RED+"你的扩展背包是空的!");
                        	return true;
                        }
                        
                        player.sendMessage("物品已发送!");
                        targetplayer.sendMessage(ChatColor.GREEN + playername + ChatColor.WHITE + "往你的扩展背包中发送了一些东西");
                        inventorymap.saveInventory(playername);
                        inventorymap.saveInventory(targetplayer.getName());
                        return true;
        			}
        			if(args[0].equalsIgnoreCase("full")){
        				
        				boolean stat;
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        
                        if(inventorymap.getInventory(player.getName())==null){
                        	player.sendMessage("你还没有扩展背包");
                        	return true;
                        }
                        
                        int itemid = 0;
                        /*
                        if(itemid==0){
                        	player.sendMessage("不能填充空气");
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
                                	player.sendMessage("没有找到物品  " + args[1]);
                                	return true;
                        		}
                        	}
                        	itemid = Integer.parseInt(args[1]);
                        	if(Material.getMaterial(itemid)==null){

                            	player.sendMessage("没有找到物品  " + args[1]);
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
                        player.sendMessage("填充完毕!");
                        
                        return true;
        			}
        		}
        		else if(args.length==3){
        			if(args[0].equalsIgnoreCase("levelupfor")){
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        
        			}
        			if(args[0].equalsIgnoreCase("createfor")){
        				
                        if (!(plugin.getPermissionHandler().has(player, "mi.admin"))) 
                        {
                             player.sendMessage("你没有使用这个命令的权限.");
                             return true;
                        }
                        
                        Player targetplayer = plugin.getServer().getPlayer(args[1]);
                        
                        int size = 0;
                        int len = args[2].length();
                        
                    	for(int j=0;j<len;j++){
                    		char c = args[2].charAt(j);
                    		if(c>'9'||c<'0'){
                            	player.sendMessage("错误的大小参数  " + args[2]);
                            	return true;
                    		}
                    	}
                    	size = Integer.parseInt(args[2]);
                    	
                        if(targetplayer==null){
                        	
                        	player.sendMessage("未找到在线玩家,创建失败");
                        	return true;
                        }
                        String targetplayername = targetplayer.getName();
                        
                        if(inventorymap.getInventory(targetplayername)!=null){
                        	player.sendMessage("该玩家已经拥有了扩展背包");
                        	return true;
                        }
                        if(size == 0 )
                        {
                        	player.sendMessage("你在开玩笑?");
                        	return true;
                        }
                
                    	if(size>90){
                    		player.sendMessage("我擦!这么大的包,你是想逆天啊!?");
                    		return true;
                    	}
                    	if(size%9!=0){
                    		player.sendMessage("背包的大小必须为⑨的倍数");
                    		return true;
                    	}
                    	
                        boolean res = inventorymap.addInventory(targetplayername, size);
	               		 if(res == true){	               			 
	               			 player.sendMessage("为 " + targetplayername +" 创建了一个背包.");
	            			 targetplayer.sendMessage("管理员为你创建了一个扩展背包");
	                		 String sql = "Insert Into prefix_InventoryData (UserName , Amount ,invData) Values('" +
	                				 targetplayername + "',27,'0:0');";
	                		 plugin.registerInventory(sql);
	                		 
	                		 System.out.println("[MineInventory]Inventory of " + targetplayername +" created.");
	            		 }
	            		 else{
	            			 player.sendMessage("发生未知错误");
	            		 }
	               		 return true;
        			}
        			
        		}
        	}
        }
        player.sendMessage("命令无效, 请使用/mi help 来查看详细的命令帮助.");
		return true;
	}

}

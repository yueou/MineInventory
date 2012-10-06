package com.yueou.MineInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MineInventoryHash {
	
	private ArrayList<String> playerlist;
    private HashMap<String,MineInventoryInventory> inventmap;
    private MineInventory plugin;
    
    public MineInventoryHash(MineInventory plugin){
    	this.plugin=plugin;
    	inventmap = new HashMap<String, MineInventoryInventory>();
    	playerlist = new ArrayList<String>();
    }
    
    public boolean addInventory(String playername,int size){
    	
    	MineInventoryInventory minv;
    	minv = new MineInventoryInventory(playername,size);
    	inventmap.put(playername.toLowerCase(), minv);
    	playerlist.add(playername.toLowerCase());
    	
    	return true;
    }
    
    public boolean addInventory(String playername,MineInventoryInventory inventory){
    	playerlist.add(playername.toLowerCase());
    	inventmap.put(playername.toLowerCase(),inventory);
    	
    	return true;
    }
    
    public boolean loadInventory(String playername,int size,String data,int []tool){
    	
    	String itemset[];
    	MineInventoryInventory minv;
    	ItemStack item;
    	
    	minv = new MineInventoryInventory(playername.toLowerCase(),size);
    	minv.setTochest(tool[0]==1);
    	minv.setDrop(tool[1]==1);
    	minv.setSort(tool[2]==1);
    	minv.setSend(tool[3]==1);
    	minv.setShift(tool[4]==1);
    	if(tool[5]==1)
    		minv.Shift();
    	
    	if(data==null||data=="")return false;
    	
    	itemset = data.split(":");
    	int i,j,n;
    	for(i=0,j=0,n=0;i<itemset.length;i++,j++){
    		int itemtype = Integer.parseInt(itemset[i]);
    		
    		if(itemtype!=0)n++;
    		
    		item = new ItemStack(itemtype);
    		i++;
    		
    		item.setAmount(Integer.parseInt(itemset[i]));
        	minv.getInventory().setItem(j, item); 
    	}
    	
    	this.addInventory(playername.toLowerCase(), minv);
    	
    	System.out.println("[MineInventory]: Inventory of "+ playername+" loaded, " + n +" Items has.");
    	
    	return true;
    }
    
    public MineInventoryInventory getInventory(String playername){
    	MineInventoryInventory minv;
    	minv = inventmap.get(playername.toLowerCase());
    	return minv;
    }
    
    public MineInventoryInventory getInventory(Player player){
    	MineInventoryInventory minv;
    	minv = inventmap.get(player.getName().toLowerCase());
    	return minv;
    }
    
    public boolean removeInventory(String playername){
    	inventmap.remove(playername.toLowerCase());
    	playerlist.remove(playername.toLowerCase());
    	
    	return true;
    }
    
    public static String getInventoryInfo(Inventory inv){
    	
    	String invinfo = "";
    	ListIterator<ItemStack> invlist = inv.iterator();
    	ItemStack is;
    	
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
    	return invinfo;
    }
    
    public void saveInventory(String playername){
    	int tochest = 0;
    	int sort = 0;
    	int drop = 0;
    	int send = 0;
    	
    	MineInventoryInventory inv = getInventory(playername.toLowerCase());
    	
    	if(inv.canTochest()){
    		tochest = 1;
    	}
    	if(inv.canSort()){
    		sort = 1;
    	}
    	if(inv.canDrop()){
    		drop = 1;
    	}
    	if(inv.canSend()){
    		send = 1;
    	}
    	
    	Inventory psaveinventory = inv.getInventory();
		String sql = "Update prefix_InventoryData SET invData = '" + getInventoryInfo(psaveinventory) +
												"' , Amount = " + psaveinventory.getSize() +
												", CanChest = "+ tochest +
												", CanDrop = " +drop +
												", CanSort = " +sort +
												", CanSend = " +send +
												" Where UserName = '" + playername.toLowerCase() + "';";
		
		plugin.updateInventory(sql);
		
		System.out.println("[MineInventory]: Inventory of " + playername +" saved.");
    }
    
    public void saveAll(){
    	
    	int size = playerlist.size();
    	String playername = null;
    	
    	for(int i=0;i<size;i++){
    		
    		playername = playerlist.get(i);
    		saveInventory(playername);
    	}
    }

}

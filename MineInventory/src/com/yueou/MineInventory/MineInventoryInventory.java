package com.yueou.MineInventory;

import java.util.ArrayList;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class MineInventoryInventory implements InventoryHolder{

	protected Inventory minventory;
	private String playername;
	private boolean tochest;
	private boolean drop;
	private boolean sort;
	private boolean send;
	
	public MineInventoryInventory(String playername,int size){
		this.playername = playername;
		String prefix = "不科学背包";
		if(size <= 9)
			prefix = "苦逼小破包";
		else if(size <= 18)
			prefix = "扩展背包";
		else if(size <= 27)
			prefix = "大容量背包";
		else if(size <= 36)
			prefix = "海量背包";
		else if(size <= 45)
			prefix = "四维空间背包";
		else if(size <= 54)
			prefix = "超次元背包";
		
		minventory = Bukkit.createInventory(this, size, prefix);
		tochest = false;
		drop = false;
	}
	
	public void openInventory(Player player){
		player.openInventory(minventory);
	}
	
	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return minventory;
	}
	
	
	public String getOwner(){
		return playername;
	}
	
	public ListIterator<ItemStack> getItems(){
		
		ListIterator<ItemStack> itemlist;
		itemlist = minventory.iterator();
		return itemlist;
		
	}
	
	public void setTochest(boolean tochest){
		this.tochest = tochest; 
	}
	
	public boolean canTochest(){
		return tochest;
	}
	
	public void setDrop(boolean drop){
		this.drop = drop; 
	}
	
	public boolean canDrop(){
		return drop;
	}
	
	public void setSort(boolean sort){
		this.sort = sort; 
	}
	
	public boolean canSort(){
		return sort;
	}
	
	public void setSend(boolean send){
		this.send = send; 
	}
	
	public boolean canSend(){
		return send;
	}
	
	
	
	public void sortInventory(){
		ItemStack temp = null;
		
		ListIterator<ItemStack> itemlist = minventory.iterator();
		ArrayList<ItemStack> itemarray = new ArrayList<ItemStack>();
		ArrayList<ItemStack> newitemarray = new ArrayList<ItemStack>();
		int i,j;
		for(i=0;itemlist.hasNext();i++){
			temp = itemlist.next();
			if(temp==null)continue;
			
			itemarray.add(temp);
		}
		int amount = itemarray.size();
		int min = 0;
		for(i=0;i<amount;i++){
			min = 0;
			for(j=1;j<amount-i;j++){
				
				if(itemarray.get(min).getTypeId()>itemarray.get(j).getTypeId()){
					min = j;
				}
			}
			newitemarray.add(itemarray.get(min));
			itemarray.remove(min);
			
		}
		minventory.clear();
		amount = newitemarray.size();
		for(i=0;i<amount;i++){
			minventory.addItem(newitemarray.get(i));
		}
		
	}


}

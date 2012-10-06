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
	private boolean shift;
	private boolean shiftstat;
	public boolean stat;
	
	public MineInventoryInventory(String playername,int size){
		this.playername = playername;
		String prefix = "����ѧ����";
		if(size <= 9)
			prefix = "���С�ư�";
		else if(size <= 18)
			prefix = "��չ����";
		else if(size <= 27)
			prefix = "����������";
		else if(size <= 36)
			prefix = "��������";
		else if(size <= 45)
			prefix = "��ά�ռ䱳��";
		else if(size <= 54)
			prefix = "����Ԫ����";
		
		minventory = Bukkit.createInventory(this, size, prefix);
		tochest = false;
		drop = false;
		sort = false;
		send = false;
		shift = false;
		shiftstat = false;
		stat = false;
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
	
	public void setShift(boolean shift){
		this.shift = shift;
	}
	
	public void Shift(){
		shiftstat = !shiftstat;
	}
	
	public boolean shiftStat(){
		return shiftstat;
	}
	
	public boolean canShift(){
		return shift;
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

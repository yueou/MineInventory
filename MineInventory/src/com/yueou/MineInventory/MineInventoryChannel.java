package com.yueou.MineInventory;

import net.minecraft.server.v1_8_R1.PacketDataSerializer;
import net.minecraft.server.v1_8_R1.PacketPlayOutCustomPayload;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MineInventoryChannel {
	private final String channel ="MineInventory";
	
	MineInventory plugin;
	
	public MineInventoryChannel(MineInventory plugin){
		
		this.plugin = plugin;
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
	
	}
	
	public void sendOpen(Player player){
		if(player.getListeningPluginChannels().contains(channel)){
			player.sendPluginMessage(plugin , channel, "open".getBytes(java.nio.charset.Charset.forName("UTF-8")));
		}
	}
	
	public void sendClose(Player player){

		if(player.getListeningPluginChannels().contains(channel)){
			player.sendPluginMessage(plugin , channel, "close".getBytes(java.nio.charset.Charset.forName("UTF-8")));
		}
	}
}
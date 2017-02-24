package com.yueou.MineInventory;

import java.util.HashMap;

public class MineInventoryCommandHash {

	HashMap<String, String> commandmap;

	public MineInventoryCommandHash() {
		commandmap = new HashMap<String, String>();
	}

	public String getLastCommand(String playername) {

		return commandmap.get(playername);
	}

	public void setLastCommand(String playername, String command) {
		if (commandmap.containsKey(playername)) {
			commandmap.remove(playername);
		}
		commandmap.put(playername, command);
	}

	public void removeLastCommand(String playername) {

		commandmap.remove(playername);
	}

}

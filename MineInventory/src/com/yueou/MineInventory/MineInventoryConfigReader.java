package com.yueou.MineInventory;

import java.util.List;
import java.util.logging.Logger;

public class MineInventoryConfigReader {

    Logger log = Logger.getLogger("Minecraft");

     private String host;
     private String port;
     private String dbName;
     private String userName;
     private String pw;
     private String pre;
     private Double createprice;
     private List<Double> updateprices;
     private List<String> bagName;
     private List<Integer> itemBlackList;
     private boolean debug;
     public static MineInventoryConfigReader instance;
     
    public MineInventoryConfigReader()
     {
    	instance = this;
    	   host = MineInventory.config.getString("Database.MySQL.Host");
    	   port = MineInventory.config.getString("Database.MySQL.Port");
    	   dbName = MineInventory.config.getString("Database.MySQL.Database");
    	   userName = MineInventory.config.getString("Database.MySQL.User");
    	   pw = MineInventory.config.getString("Database.MySQL.Password");
    	   pre = MineInventory.config.getString("Database.MySQL.Prefix");
    	   createprice = MineInventory.config.getDouble("Main.Price.Create");
    	   updateprices = MineInventory.config.getDoubleList("Main.Price.Update");
    	   bagName = MineInventory.config.getStringList("Main.BagName");
    	   itemBlackList = MineInventory.config.getIntegerList("Main.ItemBlackList");
    	   debug = MineInventory.config.getBoolean("Main.Debug");
    	   

    	   
            	   
     }
     
     public String getHostname()
     {
             return host;
     }
     
     public String getDataBase()
     {
             return dbName;
     }
     
     public String getPort()
     {
             return port;
     }
     
     public String getUsername()
     {
             return userName;
     }
     
     public String getPassword()
     {
             return pw;
     }
     
     public String getPrefix()
     {
             return pre;
     }
     public double getCreatePrice()
     {
             return createprice;
     }

     
     public List<Double> getPrices()
     {
             return updateprices;
     }    

     
     public List<String> getBagNameList()
     {
             return bagName;
     }    
     
     public List<Integer> getItemBlackList(){
    	 return itemBlackList;
     }
     
     public boolean isDebug()
     {
             return debug;
	}
}

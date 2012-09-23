package com.yueou.MineInventory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MineInventoryDataBaser{
	
    private static Connection conn = null;     
    private static String host;     
    private static String port;    
    private static String db;     
    private static String dbuser;   
    private static String dbpw;
    private static String prefix;
    private static MineInventory plugin;
    
    public MineInventoryDataBaser(String phost,String database,String puser,String ppw,String pport,String pprefix,MineInventory pplugin){

    	host = phost;
    	dbuser = puser;
    	db = database;
    	dbpw = ppw;
    	port =pport;
    	prefix = pprefix;
    	
    	
    	plugin = pplugin;
    	 	
        try{
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(3);
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?" + "user=" + 
                           dbuser+ "&" + "password=" + dbpw);
        }
	    catch(ClassNotFoundException e)
	    {
	            System.out.println("[MineInvnetory]: 没有找到数据库");
	            plugin.onDisable();
	    }
	    catch(SQLException e)
	    {
	            System.out.println("[MineInventory]: 连接数据库发生错误,请检查你的配置文件");
	            plugin.onDisable();
	    }
	    createTable();
	    //hold connection to db open through a repeating sql task
	    plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {public void run(){checkConnection();holdConnection();}}, 60, 72000);
	
    }
    
    public void createTable(){
    	
        if(conn != null)
        {
                checkConnection();
                try{
                        
                       Statement query = conn.createStatement();
                        String sql = "Show Tables like '" + prefix + "InventoryData';";
                        ResultSet result = query.executeQuery(sql);
                        if(result.next())
                        {
                                return;
                        }
                        else {
                                String sqlcre = "Create Table " + prefix + "InventoryData (ID Integer Primary Key NOT NULL AUTO_INCREMENT, " +
                                                "Username Varchar (20) NOT NULL, " + 
                                                "Amount Tinyint DEFAULT 9, " + 
                                                "CanChest Tinyint DEFAULT 0, " +  
                                                "CanDrop Tinyint DEFAULT 0, " + 
                                                "CanSort Tinyint DEFAULT 0, " + 
                                                "CanSend Tinyint DEFAULT 0, " + 
                                                "Pre1 Tinyint DEFAULT 0, " + 
                                                "Pre2 Tinyint DEFAULT 0, " +
                                                "invData Varchar(800) DEFAULT '0:0');";
                                query.executeUpdate(sqlcre);
                        }
                        
               }catch(SQLException e){
                        e.printStackTrace();
                }
        }
    	
    }
    
    public ResultSet executeQuery(String sql)
    {
            checkConnection();
            Statement query;
            ResultSet result = null;
            sql = sql.replace("prefix_", prefix);
            try{
                    query = conn.createStatement();
                    result = query.executeQuery(sql);
                    }
            catch(SQLException e)
            {
                    e.printStackTrace();
            }
            return result;
    }
    
    public int updateQuery(String sql)
    {
            checkConnection();
            Statement query;
            int result = 0;
            sql = sql.replace("prefix_", prefix);
            try{
                    query = conn.createStatement();
                    result = query.executeUpdate(sql);
                    
           }
            catch(SQLException e){
                    e.printStackTrace();
            }
            return result;
    }
    
    private void checkConnection()
    {
            
           try {
                    if(conn.isClosed())
                    {
                            conn = null;
                            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?" + "user=" + 
                                           dbuser+ "&" + "password=" + dbpw);
                            
                   }
            } catch (SQLException e) {
                    e.printStackTrace();
            }
    }
    
    private void holdConnection()
    {
            String sql = "Select * from " + prefix + "InventoryData;";
            Statement query;
            try{
                    query = conn.createStatement();
                    query.executeQuery(sql);
            }
            catch(SQLException e)
            {
                    e.printStackTrace();
            }
    }
    
    public boolean registerInventory(String sql){
        int result = 0;
        
               
        sql = sql.replace("prefix_", prefix);
        result = updateQuery(sql);

        if(result != 0)
        {
                return true;
        }
        return false;

    }
    public ResultSet getAll(String sql){
    	ResultSet result;
    	
        sql = sql.replace("prefix_", prefix);
        result = executeQuery(sql);  	
    	
    	return result;
    }
    
    public String getInventory(String sql){
    	String inventorystr = "";
    	ResultSet result;
    	
        sql = sql.replace("prefix_", prefix);
        result = executeQuery(sql);
        
        try {
			inventorystr = result.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return inventorystr;
    }
    
    public boolean updateInventory(String sql){
    	int result = 0;

        sql = sql.replace("prefix_", prefix);
        result = updateQuery(sql);
        
        if(result != 0)
        {
                return true;
        }
        return false;   	
    }
    
    public boolean removeInventory(String sql){
    	int result = 0;

        sql = sql.replace("prefix_", prefix);
        result = updateQuery(sql);
        
        if(result != 0)
        {
                return true;
        }
        return false;   	
    }
    
    public String getUserName(String sql){
    	
    	String username = "";
    	ResultSet result;
    	
        sql = sql.replace("prefix_", prefix);
        result = executeQuery(sql);
        
        try {
			username = result.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return username;   	
    }

}

package base;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Deque;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class Home {
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/tweet_db";
	final String USER = "root";
	final String PASSWORD = "db15319root";
	
	final String ELB_ADDR = "ec2-54-85-169-198.compute-1.amazonaws.com";
	Connection driver;
	HTable table;
	
	private void buildSQLConnection() {
		try {
			Class.forName(JDBC_DRIVER); //Register JDBC driver.
			driver = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void prepareHBaseConnection() {
		Configuration config = HBaseConfiguration.create();
		try {
			table = new HTable(config, Bytes.toBytes("tweets"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSQLEntries(String key) {
		try {
			Statement stmt = driver.createStatement();
			ResultSet set = stmt.executeQuery("SELECT tweet_list FROM tweets WHERE user_time=\"" + key + "\"");
			String results = "";
			
			while (set.next()) {
				results += set.getString("tweet_list").replaceAll("_", "\n");
			}
			
			stmt.close();
			return results.replaceAll("\t", "");	
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String getHBaseEntries(String key) {
		
        Get g = new Get(Bytes.toBytes(key));
        Result r = null;
		try {
			r = table.get(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			table.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        byte[] value = r.getValue(Bytes.toBytes("tweetID"), Bytes.toBytes(""));
        String res = Bytes.toString(value);
        
		res = res.replaceAll("_", "\n");
        return res; 
	}
	
	public static void main(String[] args) {
		final String info = "TeamSYC,8635-0832-4410\n";
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String HOST = args[0];
		final Home home = new Home();
		home.buildSQLConnection();
		//home.prepareHBaseConnection();
		
		Undertow server = Undertow.builder()
				.addHttpListener(80, HOST)
				.setHandler(new HttpHandler() {

					public void handleRequest(final HttpServerExchange exchange) throws Exception {
						String path = exchange.getRequestPath();
						exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
						
						if (path.equals("/q1")) {
							exchange.getResponseSender().send(info + fmt.format(new Date()));
						}
						else if (path.startsWith("/q2")) {
							Map<String, Deque<String>> queryMap = exchange.getQueryParameters();
							String userid = queryMap.get("userid").getFirst();
							String tweet_time = queryMap.get("tweet_time").getFirst();
							String result = home.getSQLEntries(userid + "_" + tweet_time.replaceAll(" ", "+"));
							//String result = home.getHBaseEntries(userid + "_" + tweet_time.replaceAll(" ", "+"));
							exchange.getResponseSender().send(info + result);
						}
					}
				}).build();
		server.start();
	}
}

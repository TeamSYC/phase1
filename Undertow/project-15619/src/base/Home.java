package base;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Deque;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class Home {
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/tweet_db";
	final String USER = "root";
	final String PASSWORD = "db15319root";
	
	public String getSQLEntries(String key) {
		try {
			Class.forName(JDBC_DRIVER); //Register JDBC driver.
			Connection driver = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			Statement stmt = driver.createStatement();
			ResultSet set = stmt.executeQuery("SELECT tweet_list FROM tweets WHERE user_time=\"" + key + "\"");
			String results = "";
			
			while (set.next()) {
				results += set.getString("tweet_list").replaceAll("_", "\n");
			}
			
			driver.close();
			return results;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String getHBaseEntries() {
		return null;
	}
	
	public static void main(String[] args) {
		final String info = "SYC,8635-0832-4410\n";
		final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String HOST = args[0];
		final Home home = new Home();
		
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
							String result = home.getSQLEntries(userid + "_" + tweet_time);
							exchange.getResponseSender().send(info + result);
						}
					}
				}).build();
		server.start();
	}
}

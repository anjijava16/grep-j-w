package com.gnts.erputil.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Database {
	@Autowired
	private static DataSource datasource;
	private static Logger logger = Logger.getLogger(Database.class);
	
	public static Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.2:1521:XE", "erp", "saarc");
			System.out.println("Test----1");
			return con;
		}
		catch (Exception ex) {
			logger.error("Error>", ex);
			return null;
		}
	}
	
	public static void close(Connection con) {
		try {
			con.close();
		}
		catch (Exception ex) {
		}
	}
}

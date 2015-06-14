package com.gnts.erputil.tool;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public class DatabaseUtil {
 
	@Autowired
	private static DataSource datasource;
	
	private static Logger logger = Logger.getLogger(DatabaseUtil.class);
	
    public static Connection getConnection() {
        try {
        	/*AccountService serviceBean = (AccountService) SpringContextHelper
        			.getBean("accountBean");*/
            Class.forName("oracle.jdbc.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.2:1521:XE",
                    "gerp", "erp");
        	//Connection con=datasource.getConnection();
        	System.out.println("Test----1");
        	
            return con;
        } catch (Exception ex) {
            System.out.println("Database.getConnection() Error -->" + ex.getMessage());
            logger.error("Error>", ex);
            return null;
        }
    }
 
    public static void close(Connection con) {
        try {
            con.close();
        } catch (Exception ex) {
        }
    }
}


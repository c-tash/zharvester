package ru.umeta.zharvester;

import java.sql.*;

public class SQLiteJDBC{

    public static boolean db_check(Integer hash, String location) {
        
        boolean not_in_cache = true;

        String sql_table = "CREATE TABLE IF NOT EXISTS CACHE " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                " HASH           INT NOT NULL, " +
                " LOCATION       CHAR(100) NOT NULL)";

        String sql_insert = "INSERT INTO CACHE (HASH, LOCATION) " +
                "VALUES (" + hash.toString() + ", '" + location + "');";

        try(Connection c = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stmt = c.createStatement()) {

            Class.forName("org.sqlite.JDBC");
            stmt.executeUpdate(sql_table);

            ResultSet rs = stmt.executeQuery( "SELECT HASH FROM CACHE WHERE HASH = " + hash + ";" );
            if(!rs.isClosed())
                not_in_cache = false;
            rs.close();

            if(not_in_cache == true) {
                stmt.executeUpdate(sql_insert);
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        }
        return not_in_cache;
    }
}
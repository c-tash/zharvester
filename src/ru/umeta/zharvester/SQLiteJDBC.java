package ru.umeta.zharvester;

import java.sql.*;

public class SQLiteJDBC{

    public static boolean db_check(Integer hash, String location) {

        boolean NotInCache = true;

        try(Connection Con = DriverManager.getConnection("jdbc:sqlite:cache.db")) {

            PreparedStatement SqlTable = Con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS CACHE " +
                            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                            " HASH           INT NOT NULL, " +
                            " LOCATION       CHAR(100) NOT NULL);");

            Class.forName("org.sqlite.JDBC");
            SqlTable.executeUpdate();
            
            PreparedStatement SqlInsert = Con.prepareStatement(
                    "INSERT INTO CACHE (HASH, LOCATION) " +
                            "VALUES (? , ?);");

            PreparedStatement SqlSelect = Con.prepareStatement(
                    "SELECT HASH FROM CACHE WHERE HASH = ?;" );

            SqlInsert.setInt(1,hash);
            SqlInsert.setString(2,"'"+location+"'");
            SqlSelect.setInt(1,hash);
            
            ResultSet rs = SqlSelect.executeQuery();
            if(!rs.isClosed())
                NotInCache = false;
            rs.close();

            if(NotInCache == true) {
                SqlInsert.executeUpdate();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        }
        return NotInCache;
    }
}
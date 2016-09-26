package ru.umeta.zharvester;

import java.sql.*;

public class SQLiteJDBC{

    public static boolean dbCheck(Integer hash, String location) {

        boolean NotInCache = true;

        try(Connection Con = DriverManager.getConnection("jdbc:sqlite:cache.db")) {

            PreparedStatement sqlTable = Con.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS CACHE " +
                            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                            " HASH           INT NOT NULL, " +
                            " LOCATION       CHAR(100) NOT NULL);");

            Class.forName("org.sqlite.JDBC");
            sqlTable.executeUpdate();

            PreparedStatement sqlInsert = Con.prepareStatement(
                    "INSERT INTO CACHE (HASH, LOCATION) " +
                            "VALUES (? , ?);");

            PreparedStatement sqlSelect = Con.prepareStatement(
                    "SELECT HASH FROM CACHE WHERE HASH = ?;" );

            sqlInsert.setInt(1,hash);
            sqlInsert.setString(2,"'"+location+"'");
            sqlSelect.setInt(1,hash);

            ResultSet rs = sqlSelect.executeQuery();
            if(!rs.isClosed())
                NotInCache = false;
            rs.close();

            if(NotInCache == true) {
                sqlInsert.executeUpdate();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        }
        return NotInCache;
    }
}
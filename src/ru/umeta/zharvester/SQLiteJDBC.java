package ru.umeta.zharvester;

import java.sql.*;

public class SQLiteJDBC
{
    public static boolean db_check(Integer hash, String location)
    {
        Connection c = null;
        Statement stmt = null;
        boolean not_in_cache = true;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS CACHE " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    " HASH           INT NOT NULL, " +
                    " LOCATION       CHAR(100) NOT NULL)";
            stmt.executeUpdate(sql);

            ResultSet rs = stmt.executeQuery( "SELECT HASH FROM CACHE;" );
            while ( rs.next() ) {
                int hash_db  = rs.getInt("hash");
                if (hash == hash_db)
                {
                    not_in_cache = false;
                    break;
                }
            }
            rs.close();

            if(not_in_cache == true)
            {
                sql = "INSERT INTO CACHE (HASH, LOCATION) " +
                        "VALUES (" + hash.toString() + ", '" + location + "');";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return not_in_cache;
    }
}
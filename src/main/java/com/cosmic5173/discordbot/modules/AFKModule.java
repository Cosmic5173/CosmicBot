package com.cosmic5173.discordbot.modules;

import com.cosmic5173.discordbot.Bot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class AFKModule extends Module{

    public static String INVALID_USER = "0";
    public static String SQL_ERROR = "1";

    public AFKModule(String guild) {
        super(guild);
    }

    public void isAfk(String userId, Consumer<Boolean> callable) {
        try {
            Statement stmt = Bot.getDataProvider().getConnection().createStatement();
            stmt.executeQuery("SELECT AFKMessage FROM AFKSessions WHERE userID='" + userId + "';");
            ResultSet result = stmt.getResultSet();

            if (!result.next()) {
                callable.accept(false);
                return;
            }
            callable.accept(true);
        } catch (SQLException e) {
            e.printStackTrace();
            callable.accept(false);
        }
    }

    public boolean addAFK(String userId, String afkMessage) {
        try {
            Statement stmt = Bot.getDataProvider().getConnection().createStatement();
            stmt.execute("INSERT INTO AFKSessions (userID, AFKMessage) VALUES ('"+userId+"','"+afkMessage+"');");// ON DUPLICATE KEY UPDATE AFKSessions SET AFKMessage='"+afkMessage+"' WHERE userID='"+userId+"';");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
   }

    public boolean removeAFK(String userId) {
        try {
            Statement stmt = Bot.getDataProvider().getConnection().createStatement();
            stmt.execute("DELETE FROM AFKSessions WHERE userID='"+userId+"';");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getAFKMessage(String userId, Consumer<String> callable) {
        try {
            Statement stmt = Bot.getDataProvider().getConnection().createStatement();
            stmt.executeQuery("SELECT AFKMessage FROM AFKSessions WHERE userID='" + userId + "';");
            ResultSet result = stmt.getResultSet();

            if (!result.next()) {
                callable.accept(INVALID_USER);
                return;
            }
            callable.accept(result.getString("AFKMessage"));
        } catch (SQLException e) {
            e.printStackTrace();
            callable.accept(SQL_ERROR);
        }
    }

    @Override
    public String getId() {
        return "afk_module";
    }
}
package com.cosmic5173.discordbot.session;

import com.cosmic5173.discordbot.Bot;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SessionManager {

    public static Map<String, Map<String, VerificationSession>> verificationSessions = new HashMap<>();

    public static void getVerificationSession(String userId, String guildId, Consumer<VerificationSession> callback) throws SQLException {
        if (verificationSessions.containsKey(userId)) {
            callback.accept(verificationSessions.get(userId).get(guildId));
        } else {
            Statement stmt = Bot.getDataProvider().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM VerificationSession WHERE userId='"+userId+"';");
            Map<String, VerificationSession> sessionMap = new HashMap<>();

            while (resultSet.next()) {
                VerificationSession session = VerificationSession.create(resultSet.getString("guildId"), resultSet.getString("userId"), new Gson().fromJson(resultSet.getString("sessionData"), VerificationSession.VerificationSessionData.class));
                sessionMap.put(session.getGuildId(), session);
            }

            verificationSessions.put(userId, sessionMap);
            callback.accept(sessionMap.get(guildId));
        }
    }

    public static void createVerificationSession(VerificationSession session) throws SQLException {
        if (verificationSessions.containsKey(session.getUserId())) {
            verificationSessions.get(session.getUserId()).put(session.getGuildId(), session);
        } else {
            verificationSessions.put(session.getUserId(), new HashMap<>());
            verificationSessions.get(session.getUserId()).put(session.getGuildId(), session);
        }

        Statement stmt = Bot.getDataProvider().getConnection().createStatement();
        stmt.execute("INSERT INTO VerificationSession (userId, guildId, sessionData) VALUES ('"+session.getUserId()+"','"+session.getGuildId()+"','"+session.getSessionData().toString()+"');");
    }

    public static void removeVerificationSession(String userId, String guildId) throws SQLException {
        verificationSessions.get(userId).remove(guildId);
        Statement stmt = Bot.getDataProvider().getConnection().createStatement();
        stmt.execute("DELETE FROM VerificationSession WHERE userId='"+userId+"' AND guildId='"+guildId+"';");
    }
}

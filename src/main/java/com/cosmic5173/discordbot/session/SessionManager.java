package com.cosmic5173.discordbot.session;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SessionManager {

    public static Map<String, VerificationSession> verificationSessions = new HashMap<>();

    public static void getVerificationSession(String id, Consumer<VerificationSession> callback) throws SQLException {
        if (verificationSessions.containsKey(id)) {
            callback.accept(verificationSessions.get(id));
        } else {
            //TODO: SQL Query
        }
    }

    public static VerificationSession createVerificationSession(VerificationSession session) throws SQLException {
        verificationSessions.put(session.getUserId(), session);
        //TODO: SQL Query
        return session;
    }

    public static void removeVerificationSession(String id) throws SQLException {
        verificationSessions.remove(id);

        //TODO: SQL Query
    }
}

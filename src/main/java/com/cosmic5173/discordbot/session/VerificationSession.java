package com.cosmic5173.discordbot.session;

import com.cosmic5173.discordbot.Bot;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.Random;

public class VerificationSession {

    public static final int MAX_ATTEMPTS = 3;

    private String guildId;
    private String userId;
    private VerificationSessionData sessionData;

    public static VerificationSession create(String guildId, String userId, String code, int attempt) {
        VerificationSession session = new VerificationSession();
        session.guildId = guildId;
        session.userId = userId;
        session.sessionData = VerificationSessionData.create(code, attempt);
        return session;
    }

    public static VerificationSession create(String guildId, String userId, VerificationSessionData data) {
        VerificationSession session = new VerificationSession();
        session.guildId = guildId;
        session.userId = userId;
        session.sessionData = data;
        return session;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserId() {
        return userId;
    }

    public VerificationSessionData getSessionData() {
        return sessionData;
    }

    public String getCode() {
        return sessionData.code;
    }

    public int getAttempts() {
        return sessionData.attempt;
    }

    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public static String generateCode() {
        Random r = new Random();
        String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        String[] capLetters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        String[] numbers = {"0","1","2","3","4","5","6","7","8","9"};

        int length = r.nextInt(10-6)+6;
        StringBuilder code = new StringBuilder();
        for (int i=0;i<length;i++) {
            switch (r.nextInt(3 - 1) + 1) {
                case 1 -> code.append(letters[r.nextInt(letters.length - 1)]);
                case 2 -> code.append(capLetters[r.nextInt(capLetters.length - 1)]);
                case 3 -> code.append(numbers[r.nextInt(numbers.length - 1)]);
            }
        }
        return code.toString();
    }

    public String createNewCode() throws SQLException {
        sessionData.attempt++;
        sessionData.code = generateCode();

        Bot.getDataProvider().getConnection().createStatement().execute("UPDATE VerificationSession SET sessionData='"+sessionData.toString()+"' WHERE userId='"+userId+"' AND guildId='"+guildId+"';");
        return sessionData.code;
    }

    public boolean validateEntry(String message) {
        return message.trim().equals(sessionData.code);
    }

    public static class VerificationSessionData {
        public String code;
        public int attempt;

        public static VerificationSessionData create(String code, int attempt) {
            VerificationSessionData data = new VerificationSessionData();
            data.code = code;
            data.attempt = attempt;
            return data;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}

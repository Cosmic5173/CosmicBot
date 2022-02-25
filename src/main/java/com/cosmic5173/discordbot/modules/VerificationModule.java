package com.cosmic5173.discordbot.modules;

import com.cosmic5173.discordbot.Bot;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class VerificationModule extends Module {

    public static final String IDENTIFIER = "verification_module";

    public static final String DEFAULT_SETTINGS = "{\"unverifiedRole\":\"\",\"verifiedRole\":\"\",\"verificationMethod\":0,\"verificationChannel\":\"\",\"verificationMessage\":\"\",\"verificationEmoji\":\"\"}";

    private VerificationSettings settings;

    public VerificationModule(String guild) {
        super(guild);
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    @Override
    public void Initialize(Consumer<Module> callback) throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT verification_module FROM GuildModuleSettings WHERE guildId='"+getGuild()+"';");
        if (!resultSet.next()) {
            settings = new Gson().fromJson(DEFAULT_SETTINGS, VerificationSettings.class);
            stmt.execute("INSERT INTO GuildModuleSettings (guildId, join_module) VALUES ('"+getGuild()+"','"+DEFAULT_SETTINGS+"');");
        } else {
            settings = new Gson().fromJson(resultSet.getString("verification_module"), VerificationSettings.class);
        }

        super.Initialize(callback);
    }

    public VerificationSettings getSettings() {
        return settings;
    }

    public void updateSettings() throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("UPDATE GuildModuleSettings SET verification_module='"+new Gson().toJson(settings)+"' WHERE guildId='"+getGuild()+"';");
    }

    public static class VerificationSettings {

        public static int DM_CODE = 0;
        public static int COMMAND = 1;
        public static int REACTION = 2;

        public String unverifiedRole;
        public String verifiedRole;
        public int verificationMethod;
        public String verificationChannel;
        public String verificationMessage;
        public String verificationEmoji;
        public String DMMessageContent;
        public String successMessageContent;
        public String failMessageContent;
    }
}

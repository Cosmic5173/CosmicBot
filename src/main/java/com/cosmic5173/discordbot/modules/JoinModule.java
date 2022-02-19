package com.cosmic5173.discordbot.modules;


import com.cosmic5173.discordbot.Bot;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class JoinModule extends Module {

    public static final String DEFAULT_SETTINGS = "{\"trackBots\":false,\"giveRole\":false,\"role\":\"\",\"sendPublicMessage\":false,\"messageChannel\":\"\",\"publicMessage\":\"\",\"sendDm\":false,\"DMMessage\":\"\"}";

    private JoinSettings settings;

    public JoinModule(String guild) {
        super(guild);
    }

    @Override
    public void Initialize(Consumer<Module> callback) throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT join_module FROM GuildModuleSettings WHERE guildId='"+getGuild()+"';");
        if (!resultSet.next()) {
            settings = new Gson().fromJson(DEFAULT_SETTINGS, JoinSettings.class);
            stmt.execute("INSERT INTO GuildModuleSettings (guildId, join_module) VALUES ('"+getGuild()+"','"+DEFAULT_SETTINGS+"');");
        } else {
            settings = new Gson().fromJson(resultSet.getString("join_module"), JoinSettings.class);
        }

        super.Initialize(callback);
    }

    public JoinSettings getSettings() {
        return settings;
    }

    public void updateSettings() throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("UPDATE GuildModuleSettings SET join_module='"+new Gson().toJson(settings)+"' WHERE guildId='"+getGuild()+"';");
    }

    @Override
    public String getId() {
        return "join_module";
    }

    public static class JoinSettings {
        public boolean trackBots;
        public boolean giveRole;
        public String role;
        public boolean sendPublicMessage;
        public String messageChannel;
        public String publicMessage;
        public boolean sendDM;
        public String DMMessage;
    }
}

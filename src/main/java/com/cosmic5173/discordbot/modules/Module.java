package com.cosmic5173.discordbot.modules;

import com.cosmic5173.discordbot.Bot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Module {

    private boolean enabled = true;

    private final String guild;

    public Module(String guild) {
        this.guild = guild;
    }

    abstract public String getId();

    public void Initialize(Consumer<Module> callback) throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT "+getId()+" FROM GuildModules WHERE guildId='"+guild+"';");
        if(!resultSet.next()) {
            stmt.execute("INSERT INTO GuildModules (guildId) VALUES ('"+guild+"');");
            enabled = true;
        } else {
            enabled = resultSet.getBoolean(getId());
        }
        if(callback != null) callback.accept(this);
    }

    public String getGuild() {
        return guild;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) throws SQLException {
        Connection connection = Bot.getDataProvider().getConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("UPDATE GuildModules SET "+getId()+"='"+(enabled ? 1 : 0)+"' WHERE guildId='"+guild+"';");
        this.enabled = enabled;
    }
}

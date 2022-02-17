package com.cosmic5173.discordbot.modules;

import net.dv8tion.jda.api.JDA;

import java.util.HashMap;

public class AFKModule extends Module{

    private final HashMap<String, String> afkUsers = new HashMap<>();

    public AFKModule(JDA jda) {
        super(jda);
    }

    public boolean isAfk(String userId) {
        return afkUsers.containsKey(userId);
    }

    public void addAFK(String userId, String afkMessage) {
        afkUsers.put(userId, afkMessage);
    }

    public void removeAFK(String userId) {
        afkUsers.remove(userId);
    }

    public String getAFKMessage(String userId) {
        return afkUsers.get(userId);
    }
}

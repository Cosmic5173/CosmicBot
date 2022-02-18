package com.cosmic5173.discordbot.modules;

import net.dv8tion.jda.api.JDA;

public class ModuleManager {

    public static AFKModule afkModule;

    public ModuleManager(JDA jda) {
        afkModule = new AFKModule(jda);
    }

    public AFKModule getAfkModule() {
        return afkModule;
    }
}

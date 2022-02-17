package com.cosmic5173.discordbot.modules;

import com.cosmic5173.discordbot.exceptions.ModuleDisabledException;
import net.dv8tion.jda.api.JDA;

public class ModuleManager {

    public static AFKModule afkModule;

    public ModuleManager(JDA jda) {
        afkModule = new AFKModule(jda);
    }

    public AFKModule getAfkModule() {
        if (afkModule.isEnabled()) {
            return afkModule;
        } else {
            throw new ModuleDisabledException("The AFK Module is currently disabled.");
        }
    }
}

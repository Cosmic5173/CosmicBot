package com.cosmic5173.discordbot.modules;

import net.dv8tion.jda.api.JDA;

public abstract class Module {

    private final JDA jda;

    private boolean enabled = true;

    public Module(JDA jda) {
        this.jda = jda;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

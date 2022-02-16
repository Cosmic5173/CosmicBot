package com.cosmic5173.discordbot.commands;

import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "Ping the bot to see latency.");
    }
    @Override
    public void execute(Interaction interaction) {
        long time = System.currentTimeMillis();
        if (interaction.getMessage() != null) {
            interaction.getMessage().reply("Ping: ``Checking Ping...``").flatMap(v -> v.editMessageFormat("Ping: ``%d ms``", System.currentTimeMillis() - time)).queue();
        } else {
            assert interaction.getSlashExecutor() != null;
            interaction.getSlashExecutor().reply("Ping: ``Checking Ping...``").flatMap(v -> v.editOriginalFormat("Ping: ``%d ms``", System.currentTimeMillis() - time)).queue();
        }
    }
}

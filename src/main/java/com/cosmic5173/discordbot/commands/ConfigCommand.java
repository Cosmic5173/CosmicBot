package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.commands.subcommands.ConfigSettingsSubcommand;
import tech.xigam.cch.command.Baseless;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;


public class ConfigCommand extends Command implements Baseless {

    public ConfigCommand() {
        super("config", "Configure bot settings.");

        registerSubCommand(new ConfigSettingsSubcommand());
    }

    @Override
    public void execute(Interaction interaction) {

    }
}

package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.commands.subcommands.ConfigModulesSubcommand;
import com.cosmic5173.discordbot.commands.subcommands.JoinModuleSettingsCommand;
import com.cosmic5173.discordbot.commands.subcommands.VerificationModuleSettingsCommand;
import tech.xigam.cch.command.Baseless;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;


public class ConfigCommand extends Command implements Baseless {

    public ConfigCommand() {
        super("config", "Configure bot settings.");

        registerSubCommand(new ConfigModulesSubcommand());
        registerSubCommand(new JoinModuleSettingsCommand());
        registerSubCommand(new VerificationModuleSettingsCommand());
    }

    @Override
    public void execute(Interaction interaction) {

    }
}

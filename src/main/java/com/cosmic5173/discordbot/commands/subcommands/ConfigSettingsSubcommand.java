package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public class ConfigSettingsSubcommand extends SubCommand implements Arguments {

    public ConfigSettingsSubcommand() {
        super("modules", "Configure bot modules.");
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.createWithChoices("modules", "Select a module.", "modules", OptionType.STRING, true, 0, "afk module"),
                Argument.create("enabled", "Set whether the module is enabled or disabled.", "enabled", OptionType.BOOLEAN, false, 1)
        );
    }

    @Override
    public void execute(Interaction interaction) {
        assert interaction.getMessage() != null;
        interaction.setEphemeral(true);

        if (interaction.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            switch (interaction.getArgument("modules", String.class)) {
                case "afk module":
                    Bot.getModuleManager().getAfkModule().setEnabled(interaction.getArgument("enabled", Boolean.class));
                    break;
            }
        }
    }
}

package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.modules.ModuleManager;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class ConfigModulesSubcommand extends SubCommand implements Arguments {

    public ConfigModulesSubcommand() {
        super("modules", "Configure bot modules.");
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.createWithChoices("modules", "Select a module.", "modules", OptionType.STRING, true, 0, String.valueOf(Bot.getModuleManager().getModuleRegistry().keySet())),
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
                    boolean enabled = interaction.getArgument("enabled", Boolean.class);
                    Bot.getModuleManager().getGuildModule(interaction.getGuild().getId(), ModuleManager.ModuleIds.AFK_MODULE, (Module module) -> {
                        try {
                            module.setEnabled(enabled);
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Modules", "The AFK Module has been " + (enabled ? "enabled." : "disabled.")));
                        } catch (SQLException e) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Modules", "There was an error, please try again later."));
                            e.printStackTrace();
                        }
                    });
                    break;
            }
        }
    }
}
package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.AFKModule;
import com.cosmic5173.discordbot.modules.ModuleManager;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public class AFKCreateSubcommand extends SubCommand implements Arguments {


    public AFKCreateSubcommand() {
        super("create", "Create your AFK message.");
    }

    @Override
    public void execute(Interaction interaction) {
        Bot.getModuleManager().getGuildModule(interaction.getGuild().getId(), AFKModule.IDENTIFIER, (Module module) -> {
            if (module.isEnabled()) {
                String userId = interaction.getMember().getId();
                ((AFKModule) module).isAfk(userId, (Boolean isAfk) -> {
                    if (isAfk) {
                        ((AFKModule) module).getAFKMessage(userId, (String AFKMessage) -> {
                            if (AFKMessage.equals(AFKModule.INVALID_USER) || AFKMessage.equals(AFKModule.SQL_ERROR)) {
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "There was an issue, please try again later."));
                            } else {
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "Your are currently AFK: ``" + AFKMessage + "``"));
                            }
                        });
                    } else {
                        String message = interaction.getArgument("message", String.class);
                        if (!((AFKModule) module).addAFK(interaction.getMember().getId(), message)) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "There was an issue, please try again later."));
                        } else {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "You are now AFK: ``" + message + "``"));
                        }
                    }
                });
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "The AFK Module is disabled."));
            }
        });
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("message", "Set your AFK message.", "message", OptionType.STRING, false, 0)
        );
    }
}

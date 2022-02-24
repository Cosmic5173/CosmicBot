package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.AFKModule;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.modules.ModuleManager;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Interaction;

public class AFKClearSubcommand extends SubCommand {


    public AFKClearSubcommand() {
        super("clear", "Clear your AFK message.");
    }

    @Override
    public void execute(Interaction interaction) {
        Bot.getModuleManager().getGuildModule(interaction.getGuild().getId(), AFKModule.IDENTIFIER, (Module module) -> {
            if (module.isEnabled()) {
                String userId = interaction.getMember().getId();
                ((AFKModule) module).isAfk(userId, (Boolean isAfk) -> {
                    if (!isAfk) {
                        interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "You currently are not afk."));
                    } else {
                        if (!((AFKModule) module).removeAFK(userId)) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "There was an issue, please try again later."));
                        } else {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "Your AFK status has been removed."));
                        }
                    }
                });
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "The AFK Module is disabled."));
            }
        });
    }
}

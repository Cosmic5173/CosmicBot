package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.commands.subcommands.AFKClearSubcommand;
import com.cosmic5173.discordbot.commands.subcommands.AFKCreateSubcommand;
import com.cosmic5173.discordbot.modules.AFKModule;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import tech.xigam.cch.command.Baseless;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public class AFKCommand extends Command implements Baseless {

    public AFKCommand() {
        super("afk", "Set yourself as AFK.");

        registerSubCommand(new AFKCreateSubcommand());
        registerSubCommand(new AFKClearSubcommand());
    }

    @Override
    public void execute(Interaction interaction) {
        AFKModule module = Bot.getModuleManager().getAfkModule();
        if (module.isEnabled()) {
            String userId = interaction.getMember().getId();
            module.isAfk(userId, (Boolean isAfk) -> {
                if (isAfk) {
                    module.getAFKMessage(userId, (String AFKMessage) -> {
                        if(AFKMessage.equals(AFKModule.INVALID_USER) || AFKMessage.equals(AFKModule.SQL_ERROR)) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "There was an issue, please try again later."));
                        } else {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "Your are currently AFK: ``" + AFKMessage + "``"));
                        }
                    });
                } else {
                    interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "You are not currently AFK."));
                }
            });
        } else {
            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "The AFK Module is disabled."));
        }
    }
}

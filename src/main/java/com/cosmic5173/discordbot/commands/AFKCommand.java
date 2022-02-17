package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.commands.subcommands.AFKCreateSubcommand;
import com.cosmic5173.discordbot.modules.AFKModule;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public class AFKCommand extends Command {

    public AFKCommand() {
        super("afk", "Set yourself as AFK.");

        registerSubCommand(new AFKCreateSubcommand());
    }

    @Override
    public void execute(Interaction interaction) {
        AFKModule module = Bot.getModuleManager().getAfkModule();
        if (module.isEnabled()) {
            if (module.isAfk(interaction.getMember().getId())) {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "Your are currently AFK: ``" + module.getAFKMessage(interaction.getMember().getId()) + "``"));
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "You are not currently AFK."));
            }
        } else {
            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | AFK Module", "The AFK Module is disabled."));
        }
    }
}

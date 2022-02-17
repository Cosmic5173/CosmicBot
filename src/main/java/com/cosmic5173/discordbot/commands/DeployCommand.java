package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Interaction;

public class DeployCommand extends Command {

    public DeployCommand() {
        super("deploy", "Deploys slash-commands to this guild.");
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.setEphemeral(false);
        if(!interaction.getMember().getId().matches(Bot.getConfiguration().owner)) {
            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Deploy", "You cannot deploy slash-commands."));
            return;
        }

        interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Deploy", "Attempting to deploy slash-commands..."));
        Bot.getCommandHandler().deployAll(interaction.getGuild());
    }
}
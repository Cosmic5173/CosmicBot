package com.cosmic5173.discordbot.commands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.modules.SnipeModule;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public class SnipeCommand extends Command implements Arguments {

    public SnipeCommand() {
        super("snipe", "See a users deleted message list.");
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.create("user", "The user which deleted messages are requested for.", "user", OptionType.MENTIONABLE, true, 0)
        );
    }

    @Override
    public void execute(Interaction interaction) {
        interaction.setEphemeral();
        Guild guild = interaction.getGuild();

        Bot.getModuleManager().getGuildModule(guild.getId(), SnipeModule.IDENTIFIER, (Module module) -> {
            if (module.isEnabled()) {
                IMentionable user = interaction.getArgument("user", IMentionable.class);

                if (((SnipeModule) module).hasDeletedMessage(user.getId())) {
                    if (((SnipeModule) module).getSettings().trackMultiple) {
                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setTitle(user.getAsMention() + "'s Last Deleted Messages");

                        int c = 1;
                        for (String message : ((SnipeModule) module).getAllDeletedMessages(user.getId())) {
                            if (embedBuilder.build().getLength() + message.length() > 3900) break;
                            if (message.length() < 1000) {
                                embedBuilder.addField("Message #" + c + ":", message, false);
                                c++;
                            }
                        }

                        interaction.reply(embedBuilder.build());
                    } else {
                        interaction.reply(EmbedUtils.defaultEmbed(user.getAsMention() + "'s Last Deleted Message", ((SnipeModule) module).getDeletedMessage(user.getId())));
                    }
                } else {
                    interaction.reply(EmbedUtils.defaultEmbed("Cosmic Bot | Snipe Module", user.getAsMention() + " does not have any tracked deleted messages."));
                }
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Snipe Module", "The Snipe Module is disabled."));
            }
        });
    }
}

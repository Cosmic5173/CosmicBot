package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.JoinModule;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public class JoinModuleSettingsCommand extends SubCommand implements Arguments {

    public JoinModuleSettingsCommand() {
        super("join-module-settings", "Settings for the Join Module.");
    }

    @Override
    public void execute(Interaction interaction) {
        assert interaction.getMessage() != null;
        interaction.setEphemeral(true);

        Bot.getModuleManager().getGuildModule(interaction.getGuild().getId(), "join_module", (Module module) -> {
            if (module.isEnabled()) {
                if (interaction.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    try {
                        String setting = interaction.getArgument("setting", String.class);
                        String value = interaction.getArgument("value", String.class);

                        if (!setting.equals("list") && value == null) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "You must provide a setting value."));
                            return;
                        }
                        switch (setting) {
                            case "track-bots":
                                ((JoinModule) module).getSettings().trackBots = Boolean.parseBoolean(value);
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Track Bots set to: ``"+((JoinModule) module).getSettings().trackBots+"``"));
                                break;
                            case "do-give-role":
                                ((JoinModule) module).getSettings().giveRole = Boolean.parseBoolean(value);
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Give Role set to: ``"+((JoinModule) module).getSettings().giveRole+"``"));
                                break;
                            case "give-role-id":
                                ((JoinModule) module).getSettings().role = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Role ID set to: ``"+((JoinModule) module).getSettings().role+"``"));
                                break;
                            case "do-send-public-join-message":
                                ((JoinModule) module).getSettings().sendPublicMessage = Boolean.parseBoolean(value);
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Send Public Message set to: ``"+((JoinModule) module).getSettings().sendPublicMessage+"``"));
                                break;
                            case "public-message-channel-id":
                                ((JoinModule) module).getSettings().messageChannel = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Message Channel ID set to: ``"+((JoinModule) module).getSettings().messageChannel+"``"));
                                break;
                            case "public-message-content":
                                ((JoinModule) module).getSettings().publicMessage = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Public Message Content set to: ``"+((JoinModule) module).getSettings().publicMessage+"``"));
                                break;
                            case "do-send-join-DM":
                                ((JoinModule) module).getSettings().sendDM = Boolean.parseBoolean(value);
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Send DM set to: ``"+((JoinModule) module).getSettings().sendDM+"``"));
                                break;
                            case "DM-message-content":
                                ((JoinModule) module).getSettings().DMMessage = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "DM Message Content set to: ``"+((JoinModule) module).getSettings().DMMessage+"``"));
                                break;
                            case "list":
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Track Bots set to: ``" + ((JoinModule) module).getSettings().trackBots + "``\n"
                                        +"Give Role set to: ``"+((JoinModule) module).getSettings().giveRole+"``\n"
                                        +"Role ID set to: ``"+((JoinModule) module).getSettings().role+"``\n"
                                        +"Send Public Message set to: ``"+((JoinModule) module).getSettings().sendPublicMessage+"``\n"
                                        +"Message Channel ID set to: ``"+((JoinModule) module).getSettings().messageChannel+"``\n"
                                        +"Public Message Content set to: ``"+((JoinModule) module).getSettings().publicMessage+"``\n"
                                        +"Send DM set to: ``"+((JoinModule) module).getSettings().sendDM+"``\n"
                                        +"DM Message Content set to: ``"+((JoinModule) module).getSettings().DMMessage+"``"
                                ));
                                break;
                        }

                        ((JoinModule) module).updateSettings();
                    } catch (Exception e) {
                        interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Error changing settings..."));
                        e.printStackTrace();
                    }
                }
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Join Module is disabled."));
            }
        });
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.createWithChoices("setting", "Join Module Setting", "setting", OptionType.STRING, true, 0, "track-bots", "do-give-role", "give-role-id", "do-send-public-join-message", "public-message-channel-id", "public-message-content", "do-send-join-DM", "DM-message-content", "list"),
                Argument.create("value", "New setting value.", "value", OptionType.STRING, false, 1)
        );
    }
}

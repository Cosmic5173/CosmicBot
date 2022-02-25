package com.cosmic5173.discordbot.commands.subcommands;

import com.cosmic5173.discordbot.Bot;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.modules.VerificationModule;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.xigam.cch.command.Arguments;
import tech.xigam.cch.command.SubCommand;
import tech.xigam.cch.utils.Argument;
import tech.xigam.cch.utils.Interaction;

import java.util.Collection;
import java.util.List;

public class VerificationModuleSettingsCommand extends SubCommand implements Arguments {

    public VerificationModuleSettingsCommand() {
        super("verification-module-settings", "Settings for the Verification Module.");
    }

    @Override
    public void execute(Interaction interaction) {
        assert interaction.getMessage() != null;
        interaction.setEphemeral(true);

        Bot.getModuleManager().getGuildModule(interaction.getGuild().getId(), VerificationModule.IDENTIFIER, (Module module) -> {
            if (module.isEnabled()) {
                if (interaction.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    try {
                        String setting = interaction.getArgument("setting", String.class);
                        String value = interaction.getArgument("value", String.class);

                        if (!setting.equals("list") && value == null) {
                            interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Verification Module", "You must provide a setting value."));
                            return;
                        }
                        switch (setting) {
                            case "track-bots" -> {
                                ((VerificationModule) module).getSettings().unverifiedRole = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Track Bots set to: ``" + ((VerificationModule) module).getSettings().unverifiedRole + "``"));
                            }
                            case "do-give-role" -> {
                                ((VerificationModule) module).getSettings().verifiedRole = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Give Role set to: ``" + ((VerificationModule) module).getSettings().verifiedRole + "``"));
                            }
                            case "give-role-id" -> {
                                ((VerificationModule) module).getSettings().verificationMethod = Integer.parseInt(value);
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Role ID set to: ``" + ((VerificationModule) module).getSettings().verificationMethod + "``"));
                            }
                            case "do-send-public-join-message" -> {
                                ((VerificationModule) module).getSettings().verificationChannel = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Send Public Message set to: ``" + ((VerificationModule) module).getSettings().verificationChannel + "``"));
                            }
                            case "public-message-channel-id" -> {
                                ((VerificationModule) module).getSettings().verificationMessage = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Message Channel ID set to: ``" + ((VerificationModule) module).getSettings().verificationMessage + "``"));
                            }
                            case "public-message-content" -> {
                                ((VerificationModule) module).getSettings().verificationEmoji = value.trim();
                                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Public Message Content set to: ``" + ((VerificationModule) module).getSettings().verificationEmoji + "``"));
                            }
                            case "list" -> interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Join Module", "Unverified Role set to: ``" + ((VerificationModule) module).getSettings().unverifiedRole + "``\n"
                                    + "Verified Role ID set to: ``" + ((VerificationModule) module).getSettings().verifiedRole + "``\n"
                                    + "Verification Method set to: ``" + ((VerificationModule) module).getSettings().verificationMethod + "``\n"
                                    + "Verification Channel ID set to: ``" + ((VerificationModule) module).getSettings().verificationChannel + "``\n"
                                    + "Verification Message ID set to: ``" + ((VerificationModule) module).getSettings().verificationMessage + "``\n"
                                    + "Verification Reaction set to: ``" + ((VerificationModule) module).getSettings().verificationEmoji + "``"
                            ));
                        }

                        ((VerificationModule) module).updateSettings();
                    } catch (Exception e) {
                        interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Verification Module", "Error changing settings..."));
                        e.printStackTrace();
                    }
                }
            } else {
                interaction.reply(EmbedUtils.defaultEmbed("CosmicBot | Verification Module", "Verification Module is disabled."));
            }
        });
    }

    @Override
    public Collection<Argument> getArguments() {
        return List.of(
                Argument.createWithChoices("setting", "Verification Module Setting", "setting", OptionType.STRING, true, 0, "unverified-role-id", "verified-role-id", "verification-method", "verification-channel-id", "verification-message-id", "verification-reaction-id"),
                Argument.create("value", "New setting value.", "value", OptionType.STRING, false, 1)
        );
    }
}

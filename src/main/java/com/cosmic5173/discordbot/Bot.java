package com.cosmic5173.discordbot;

import com.cosmic5173.discordbot.commands.AFKCommand;
import com.cosmic5173.discordbot.commands.ConfigCommand;
import com.cosmic5173.discordbot.commands.DeployCommand;
import com.cosmic5173.discordbot.commands.PingCommand;
import com.cosmic5173.discordbot.modules.*;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.provider.DataProvider;
import com.cosmic5173.discordbot.session.SessionManager;
import com.cosmic5173.discordbot.session.VerificationSession;
import com.cosmic5173.discordbot.utilities.BotConfiguration;
import com.cosmic5173.discordbot.utilities.EmbedUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import tech.xigam.cch.ComplexCommandHandler;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;

public class Bot extends ListenerAdapter {

    /**
     * Bot Configuration built from config.json
     */
    private static BotConfiguration configuration;

    private static ModuleManager moduleManager;

    private static DataProvider dataProvider;

    /**
     * Bot JDA Instance
     */
    private static JDA jda;

    private static final ComplexCommandHandler commandHandler = new ComplexCommandHandler(true);

    static {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    System.out.println("Unable to create config file. Check permissions.");
                    System.exit(1);
                }

                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                InputStream stream = classLoader.getResourceAsStream("config.json");
                if (stream == null) {
                    System.out.println("Unable to read template config file.");
                    System.exit(1);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                reader.close();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(configFile);
                gson.toJson(gson.fromJson(out.toString(), BotConfiguration.class), writer);
                writer.close();
            } catch (IOException ignored) {
                System.out.println("Unable to create config file. Check permissions.");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        File file = new File("config.json");
        if (!file.exists()) {
            System.out.println("Config file not found. Exiting.");
            System.exit(1);
        }

        try {
            FileReader reader = new FileReader(file);
            configuration = new Gson().fromJson(reader, BotConfiguration.class);

            if (!configuration.version.matches(BotConfiguration.LATEST_VERSION))
                BotConfiguration.updateConfig(file, configuration);
        } catch (IOException ignored) {
            System.out.println("Unable to read config file. Exiting.");
            System.exit(1);
        }

        commandHandler.setPrefix(";")
                .registerCommand(new DeployCommand())
                .registerCommand(new PingCommand())
                .registerCommand(new AFKCommand())
                .registerCommand(new ConfigCommand());

        try {
            jda = JDABuilder.createDefault(configuration.token, EnumSet.allOf(GatewayIntent.class))
                    .addEventListeners(new Bot())
                    .addEventListeners(commandHandler)
                    .setActivity(Activity.watching("Cosmic's Room"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .build().awaitReady();

            commandHandler.setJda(jda);

            moduleManager = new ModuleManager()
                    .registerModule(AFKModule.IDENTIFIER, AFKModule.class)
                    .registerModule(JoinModule.IDENTIFIER, JoinModule.class)
                    .registerModule(VerificationModule.IDENTIFIER, VerificationModule.class);

            Map<String, String> databaseDetails = configuration.database;
            dataProvider = new DataProvider();
            dataProvider.connect(
                    DataProvider.DatabaseConnectValues.create(databaseDetails.getOrDefault("address", "localhost"),
                            databaseDetails.getOrDefault("username", "root"),
                            databaseDetails.getOrDefault("password", "password"),
                            databaseDetails.getOrDefault("database", "gildenkrieg"),
                            Integer.parseInt(databaseDetails.getOrDefault("port", "3306")))
            );
        } catch (LoginException | InterruptedException | SQLException e) {
            System.out.println("Error starting bot.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Bot is logged in!");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        Guild guild = event.getGuild();
        Member member = event.getMember();
        assert (member != null);

        moduleManager.getGuildModule(guild.getId(), AFKModule.IDENTIFIER, (Module module) -> {
            if(module == null) return;
            if (!module.isEnabled()) return;

            if(event.getChannelType() != ChannelType.PRIVATE) {
                for (Member mem : event.getMessage().getMentionedMembers()) {
                    ((AFKModule) module).isAfk(mem.getId(), (Boolean isAfk) -> {
                        if(isAfk)
                            ((AFKModule) module).getAFKMessage(mem.getId(), (String AFKMessage) -> event.getMessage().reply("``"+(mem.getNickname() == null ? mem.getUser().getName() : mem.getNickname())+"`` is currently AFK: ``"+AFKMessage+"``\n").mentionRepliedUser(false).queue());
                    });
                }
            }
        });

        moduleManager.getGuildModule(guild.getId(), VerificationModule.IDENTIFIER, (Module module) -> {
            if(module.isEnabled()) {
                if(event.getChannelType() != ChannelType.PRIVATE && ((VerificationModule) module).getSettings().verificationMethod == VerificationModule.VerificationSettings.DM_CODE) return;

                try {
                    SessionManager.getVerificationSession(member.getId(), guild.getId(), (VerificationSession session) -> {
                        if (session != null) {
                            if (session.validateEntry(event.getMessage().getContentRaw())) {

                            } else {
                                if (session.getMaxAttempts() - session.getAttempts() < 0) {

                                } else {
                                    try {
                                        String newCode = session.createNewCode();

                                    } catch (SQLException e) {
                                        event.getMessage().reply(new MessageBuilder().setEmbeds(EmbedUtils.defaultEmbed("CosmicBot | Verification Module", "There was an issue with the verification system, please try again later.")).build()).queue();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        moduleManager.getGuildModule(event.getGuild().getId(), JoinModule.IDENTIFIER, (Module module) -> {
            Guild guild = event.getGuild();
            JoinModule.JoinSettings settings = ((JoinModule) module).getSettings();
            if (event.getMember().getUser().isBot() && !settings.trackBots) return;

            if (settings.giveRole) {
                Role role = guild.getRoleById(settings.role);
                if (role != null) {
                    guild.addRoleToMember(event.getMember(), role).queue();
                }
            }

            if(settings.sendPublicMessage) {
                TextChannel channel = guild.getTextChannelById(settings.messageChannel);
                if (channel != null) {
                    channel.sendMessage(settings.publicMessage).queue();
                }
            }

            if(settings.sendDM) {
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(settings.DMMessage).queue());
            }
        });

        moduleManager.getGuildModule(event.getGuild().getId(), VerificationModule.IDENTIFIER, (Module module) -> {
            Guild guild = event.getGuild();
            Member member = event.getMember();

            if (module.isEnabled()) {
                Role role = guild.getRoleById(((VerificationModule) module).getSettings().unverifiedRole);
                if(role != null)
                    guild.addRoleToMember(member, role).queue();

                try {
                    SessionManager.getVerificationSession(member.getId(), guild.getId(), (VerificationSession session) -> {
                        if (session == null) {
                            VerificationSession newSession = VerificationSession.create(guild.getId(), member.getId(), VerificationSession.generateCode(), 1);
                            try {
                                SessionManager.createVerificationSession(newSession);

                                if (((VerificationModule) module).getSettings().verificationMethod == VerificationModule.VerificationSettings.DM_CODE) {
                                    member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(((VerificationModule) module).getSettings().DMMessageContent.replace("{code}", newSession.getCode())).queue());
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("User: ("+member.getUser().getName()+" | "+member.getId() + ") already has a session, but just joined the server?");
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        moduleManager.getGuildModule(event.getGuild().getId(), VerificationModule.IDENTIFIER, (Module module) -> {
            if(module.isEnabled()) {
                try {
                    SessionManager.removeVerificationSession(event.getGuild().getId(), event.getUser().getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static JDA getJDA() {
        return jda;
    }

    public static ComplexCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public static BotConfiguration getConfiguration() {
        return configuration;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static DataProvider getDataProvider() {
        return dataProvider;
    }
}

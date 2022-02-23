package com.cosmic5173.discordbot;

import com.cosmic5173.discordbot.commands.AFKCommand;
import com.cosmic5173.discordbot.commands.ConfigCommand;
import com.cosmic5173.discordbot.commands.DeployCommand;
import com.cosmic5173.discordbot.commands.PingCommand;
import com.cosmic5173.discordbot.modules.AFKModule;
import com.cosmic5173.discordbot.modules.JoinModule;
import com.cosmic5173.discordbot.modules.Module;
import com.cosmic5173.discordbot.modules.ModuleManager;
import com.cosmic5173.discordbot.provider.DataProvider;
import com.cosmic5173.discordbot.utilities.BotConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
                    .registerModule("afk_module", AFKModule.class)
                    .registerModule("join_module", JoinModule.class);

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

        moduleManager.getGuildModule(event.getGuild().getId(), ModuleManager.ModuleIds.AFK_MODULE, (Module module) -> {
            if(module == null) return;
            if (!module.isEnabled()) return;

            if(event.getChannelType() != ChannelType.PRIVATE) {
                for (Member member : event.getMessage().getMentionedMembers()) {
                    ((AFKModule) module).isAfk(member.getId(), (Boolean isAfk) -> {
                        if(isAfk)
                            ((AFKModule) module).getAFKMessage(member.getId(), (String AFKMessage) -> event.getMessage().reply("``"+(member.getNickname() == null ? member.getUser().getName() : member.getNickname())+"`` is currently AFK: ``"+AFKMessage+"``\n").mentionRepliedUser(false).queue());
                    });
                }
            }
        });
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        moduleManager.getGuildModule(event.getGuild().getId(), ModuleManager.ModuleIds.JOIN, (Module module) -> {
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
                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {privateChannel.sendMessage(settings.DMMessage).queue();});
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

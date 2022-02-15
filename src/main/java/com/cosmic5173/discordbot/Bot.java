package com.cosmic5173.discordbot;

import com.cosmic5173.discordbot.utilities.BotConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.*;

public class Bot{

    private static BotConfiguration configuration;

    static {
        File configFile = new File("config.json");
        if(!configFile.exists()) {
            try {
                if(!configFile.createNewFile()) {
                    System.out.println("Unable to create config file. Check permissions.");
                    System.exit(1);
                }

                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                InputStream stream = classLoader.getResourceAsStream("config.json");
                if(stream == null) {
                    System.out.println("Unable to read template config file.");
                    System.exit(1);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder out = new StringBuilder(); String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                } reader.close();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(configFile);
                gson.toJson(gson.fromJson(out.toString(), BotConfiguration.class), writer); writer.close();
            } catch (IOException ignored) {
                System.out.println("Unable to create config file. Check permissions.");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) throws LoginException {
        File file = new File("config.json");
        if(!file.exists()) {
            System.out.println("Config file not found. Exiting.");
            System.exit(1);
        }

        try {
            FileReader reader = new FileReader(file);
            configuration = new Gson().fromJson(reader, BotConfiguration.class);

            if(!configuration.version.matches(BotConfiguration.LATEST_VERSION))
                BotConfiguration.updateConfig(file, configuration);
        } catch (IOException ignored) {
            System.out.println("Unable to read config file. Exiting.");
            System.exit(1);
        }

        JDABuilder.createLight(configuration.token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .setActivity(Activity.playing("Getting that good development :)"))
                .build();
    }
}

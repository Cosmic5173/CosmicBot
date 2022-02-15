package com.cosmic5173.discordbot.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class BotConfiguration {

    public static final String LATEST_VERSION = "1.0.1";

    public String version = "1.0.0";
    public String token;

    public static void updateConfig(File existingConfig, BotConfiguration existingData) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream("config.json"); if (stream == null) return;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder(); String line;
        while ((line = reader.readLine()) != null)
            out.append(line);
        reader.close();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BotConfiguration defaultData = gson.fromJson(out.toString(), BotConfiguration.class);
        FileWriter writer = new FileWriter(existingConfig);

        // Update config file.
        BotConfiguration newConfiguration = new BotConfiguration();
        if(existingData.token != null)
            newConfiguration.token = existingData.token;
        else newConfiguration.token = defaultData.token;

        gson.toJson(newConfiguration, writer); // Write to the file.
        System.out.println("Updated config file to version " + LATEST_VERSION + " from version " + existingData.version);
    }
}

package com.cosmic5173.discordbot.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModuleManager {

    private final Map<String, Class<? extends Module>> moduleRegistry = new HashMap<>();

    private final Map<String, Map<String, Module>> guildModules = new HashMap<>();

    public boolean moduleExists(String moduleName) {
        return moduleRegistry.containsKey(moduleName);
    }

    public ModuleManager registerModule(String moduleName, Class<? extends Module> className) {
        moduleRegistry.put(moduleName, className);
        return this;
    }

    public ModuleManager unregisterModule(String moduleName) {
        moduleRegistry.remove(moduleName);
        return this;
    }

    public ModuleManager unregisterAll() {
        moduleRegistry.clear();
        return this;
    }

    public Class<? extends Module> getRegisteredModule(String moduleName) {
        return moduleRegistry.get(moduleName);
    }

    public Map<String, Class<? extends Module>> getModuleRegistry() {
        return moduleRegistry;
    }

    public boolean isGuildInitiated(String guildId) {
        return guildModules.containsKey(guildId);
    }

    public void initializeGuild(String guild, Consumer<Map<String, Module>> callback) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map <String, Module> moduleMap = new HashMap<>();
        Class[] cArg = {String.class};
        for (Class<? extends Module> moduleClass : getModuleRegistry().values()) {
            Module module = moduleClass.getDeclaredConstructor(cArg).newInstance(guild);
            moduleMap.put(module.getId(), module);
        }
        callback.accept(moduleMap);
        guildModules.put(guild, moduleMap);
    }

    public void getGuildModule(String guildId, String moduleName, Consumer<Module> callback){
        if(isGuildInitiated(guildId)) {
            callback.accept(guildModules.get(guildId).get(moduleName));
        } else {
            try {
                initializeGuild(guildId, (Map<String, Module> moduleMap) -> {
                    for (Module module : moduleMap.values()) {
                        try {
                            if (module.getId().equals(moduleName)) {
                                module.Initialize(callback);
                            } else {
                                module.Initialize(null);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            callback.accept(null);
                        }
                    }
                });
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        }
    }

    public static class ModuleIds {
        public static final String AFK_MODULE = "afk_module";
        public static final String JOIN = "join_module";
    }
}

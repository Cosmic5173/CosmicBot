package com.cosmic5173.discordbot.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnipeModule extends Module {

    public static String IDENTIFIER = "snipe_module";

    private SnipeModuleSettings settings;
    private final Map<String, List<String>> userMessages = new HashMap<>();

    public SnipeModule(String guild) {
        super(guild);
    }

    public SnipeModuleSettings getSettings() {
        return settings;
    }

    public boolean hasDeletedMessage(String userId) {
        return userMessages.containsKey(userId) && !userMessages.get(userId).isEmpty();
    }

    public void addDeletedMessage(String userId, String messageContent) {
        if (!userMessages.containsKey(userId)) userMessages.put(userId, new ArrayList<>(1));

        if (settings.trackMultiple) {
            List<String> messages = userMessages.get(userId);
            if (messages.size() >= 10) {
                Object[] msgs = messages.toArray();
                messages.clear();
                messages.add(messageContent);
                for (int i = msgs.length-1;i > msgs.length-10;i--) {
                    messages.add((String) msgs[i]);
                }
            } else {
                messages.add(messageContent);
            }
        } else {
            userMessages.get(userId).set(0, messageContent);
        }
    }

    public String getDeletedMessage(String userId) {
        if (!settings.trackMultiple) {
            if (hasDeletedMessage(userId)) {
                return userMessages.get(userId).get(0);
            } else {
                throw new RuntimeException("User: "+userId+" does not have any deleted messages.");
            }
        } else {
            throw new RuntimeException("Cannot fetch a single message, currently multiple deleted messages are being tracked.");
        }
    }

    public List<String> getAllDeletedMessages(String userId) {
        if (settings.trackMultiple) {
            if (hasDeletedMessage(userId)) {
                return userMessages.get(userId);
            } else {
                throw new RuntimeException("User: "+userId+" does not have any deleted messages.");
            }
        } else {
            throw new RuntimeException("Cannot fetch a list of messages, currently only a single deleted message is being tracked.");
        }
    }

    @Override
    public String getId() {
        return IDENTIFIER;
    }

    public static class SnipeModuleSettings {
        public boolean trackMultiple = true;
    }
}

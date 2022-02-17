package com.cosmic5173.discordbot.exceptions;

public class ModuleDisabledException extends RuntimeException {

    public ModuleDisabledException(String message) {
        super(message);
    }
}

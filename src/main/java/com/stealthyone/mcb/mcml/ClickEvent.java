package com.stealthyone.mcb.mcml;

import mkremins.fanciful.FancyMessage;

final class ClickEvent {

    private static final String TYPE_RUN_COMMAND = "!";
    private static final String TYPE_SUGGEST_COMMAND = "?";
    private static final String TYPE_OPEN_URL = ">";

    static ClickEvent parse(String type, String input) {
        return new ClickEvent(type, input);
    }

    final String type;
    final String value;

    ClickEvent(String type, String value) {
        this.type = type;
        this.value = value;
    }

    void apply(FancyMessage message) {
        switch (type) {
            case TYPE_RUN_COMMAND:
                message.command(value);
                break;

            case TYPE_SUGGEST_COMMAND:
                message.suggest(value);
                break;

            case TYPE_OPEN_URL:
                message.link(value);
                break;
        }
    }

}
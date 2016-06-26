package com.stealthyone.mcb.mcml;

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

}
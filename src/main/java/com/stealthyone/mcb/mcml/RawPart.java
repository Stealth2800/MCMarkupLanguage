package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

final class RawPart {

    int index = -1;

    String text = null;
    ChatColor color = ChatColor.RESET; // TODO: not sure what default should be
    boolean isBold = false;
    boolean isItalic = false;
    boolean isUnderlined = false;
    boolean isStrikethrough = false;
    boolean isObfuscated = false;
    HoverEvent hoverEvent = null;
    ClickEvent clickEvent = null;

    void handleColor(ChatColor color) {
        System.out.println("Handling color: " + color.name());
        this.color = color;
        if (color == ChatColor.RESET) {
            isBold = isItalic = isUnderlined = isStrikethrough = isObfuscated = false;
        }
    }

    void handleFormat(ChatColor format) {
        System.out.println("Handling format: " + format.name());
        switch (format) {
            case BOLD:
                isBold = true;
                break;
            case ITALIC:
                isItalic = true;
                break;
            case MAGIC:
                isObfuscated = true;
                break;
            case UNDERLINE:
                isUnderlined = true;
                break;
            case STRIKETHROUGH:
                isStrikethrough = true;
                break;
        }
    }

    // TODO: Events

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("RawPart{");
        sb.append("text='").append(text).append("',");
        sb.append("color='").append(color.name()).append("',");
        sb.append("bold='").append(isBold).append("',");
        sb.append("italic='").append(isItalic).append("',");
        sb.append("underline='").append(isUnderlined).append("',");
        sb.append("strikethrough='").append(isStrikethrough).append("',");
        sb.append("obfuscated='").append(isObfuscated).append("',");
        sb.append("index=").append(index).append("}");

        return sb.toString();
    }

}
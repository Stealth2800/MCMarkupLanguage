package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

final class RawPart {

    int index = -1;

    String text = null;
    ChatColor color = ChatColor.WHITE; // TODO: not sure what default should be
    boolean isBold = false;
    boolean isItalic = false;
    boolean isUnderlined = false;
    boolean isStrikethrough = false;
    boolean isObfuscated = false;

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
        sb.append("obfuscated='").append(isObfuscated).append("}");

        return sb.toString();
    }

}
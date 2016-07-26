package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

final class RawPart {

    int index = -1;

    String text = null;
    ChatColor color = ChatColor.RESET;
    List<ChatColor> formats = new ArrayList<>();
    HoverEvent hoverEvent = null;
    ClickEvent clickEvent = null;

    void handleColor(ChatColor color) {
        System.out.println("Handling color: " + color.name());
        this.color = color;
        if (color == ChatColor.RESET) {
            formats.clear();
        }
    }

    void handleFormat(ChatColor format) {
        System.out.println("Handling format: " + format.name());
        formats.add(format);
    }

    ChatColor[] getFormats() {
        return formats.toArray(new ChatColor[formats.size()]);
    }

}
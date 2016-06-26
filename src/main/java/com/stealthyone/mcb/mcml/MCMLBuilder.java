package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MCMLBuilder {

    private final static Pattern COLOR_PATTERN = Pattern.compile("&[a-f0-9]");

    private final List<RawPart> parts = new ArrayList<>();

    public MCMLBuilder(String input) {
        int lastIndex = 0;
        RawPart curPart = new RawPart();

        Matcher matcher = COLOR_PATTERN.matcher(input);
        while (matcher.find()) {
            System.out.println("Last index: " + lastIndex);
            System.out.println("Start: " + matcher.start());
            System.out.println("Matcher group: " + matcher.group());

            if (curPart.text == null && curPart.index != -1) {
                System.out.println("Adding previous loop text");
                curPart.text = input.substring(lastIndex, matcher.start());
                parts.add(curPart);
                curPart = new RawPart();
            }

            curPart.color = ChatColor.getByChar(matcher.group().charAt(1));

            curPart.index = matcher.start();
            lastIndex = matcher.start() + matcher.group().length();
        }

        System.out.println("Adding final part");
        curPart.index = lastIndex;
        curPart.text = input.substring(lastIndex, input.length());
        parts.add(curPart);
    }

    // TODO: Temporary method
    public List<RawPart> getParts() {
        return parts;
    }

}
package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MCMLBuilder {

    // TODO: Dynamically build pattern
    private final static Pattern COLOR_PATTERN = Pattern.compile("&[a-f0-9rlnokm]");

    private final List<RawPart> parts = new ArrayList<>();

    public MCMLBuilder(String input) {
        if (input == null || input.isEmpty()) {
            // Don't process if input is null or empty
            parts.add(new RawPart());
            return;
        }

        int lastIndex = 0;
        RawPart curPart = new RawPart();

        Matcher matcher = COLOR_PATTERN.matcher(input);
        while (matcher.find()) {
            final int startIndex = matcher.start();

            System.out.println("Last index: " + lastIndex);
            System.out.println("Start: " + startIndex);
            System.out.println("Matcher group: " + matcher.group());

            if (curPart.text == null && curPart.index != -1 && startIndex != lastIndex) {
                System.out.println("Adding previous loop text");
                curPart.text = input.substring(lastIndex, startIndex);
                parts.add(curPart);
                curPart = new RawPart();
            }

            ChatColor found = ChatColor.getByChar(matcher.group().charAt(1));

            if (found.isFormat()) {
                curPart.handleFormat(found);
            } else {
                curPart.handleColor(found);
            }

            curPart.index = startIndex;
            lastIndex = startIndex + matcher.group().length();
        }

        System.out.println("Adding final part");
        curPart.index = lastIndex;
        curPart.text = input.substring(lastIndex, input.length());
        parts.add(curPart);

        //condenseParts();
    }

    private void condenseParts() {
        System.out.println("Condensing parts");
        parts.sort((a, b) -> Integer.compare(a.index, b.index));
    }

    // TODO: Temporary method
    public List<RawPart> getParts() {
        return parts;
    }

}
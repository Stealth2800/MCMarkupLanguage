package com.stealthyone.mcb.mcml;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MCMLBuilder {

    // TODO: Dynamically build pattern
    private final static Pattern COLOR_PATTERN = Pattern.compile("&[a-f0-9rlnokm]");
    private final static Pattern EVENT_PATTERN = Pattern.compile("\\[([\\S ]+?)\\]\\((?:(?:([!?>])\"([\\S ^\"]+?)\"(?: \"([\\S ]+?)\")?)|(?:\"([\\S ]+?)\"))\\)");

    private final List<RawPart> parts = new ArrayList<>();

    public MCMLBuilder(String input) {
        if (input == null || input.isEmpty()) {
            // Don't process if input is null or empty
            parts.add(new RawPart());
            return;
        }

        process(input, true, 0);
    }

    /**
     * No-args constructor for internal library use.
     */
    MCMLBuilder() { }

    void process(String input, boolean isBase, int offset) {
        int lastIndex = 0;
        RawPart curPart = new RawPart();

        // If parsing base message, check for hover/click events
        if (isBase) {
            final List<Integer> regions = new ArrayList<>();
            regions.add(0);

            Matcher eventMatcher = EVENT_PATTERN.matcher(input);
            while (eventMatcher.find()) {
                System.out.println("Found event group");

                final int startIndex = eventMatcher.start();

                final int oldCount = parts.size();

                // First, add text group as part
                process(eventMatcher.group(1), false, startIndex);

                final int newCount = parts.size();

                HoverEvent hoverEvent = null;
                ClickEvent clickEvent = null;

                final int groupCount = eventMatcher.groupCount() - 1;
                System.out.println("Group count: " + groupCount);
                System.out.println("Group: " + eventMatcher.group());
                for (int i = 1; i <= groupCount; i++) {
                    System.out.println("Group " + i + ": " + eventMatcher.group(i));
                }

                if (groupCount == 2 || groupCount == 4) {
                    System.out.println("Parsing hover event");

                    // Hover event
                    hoverEvent = HoverEvent.parse(eventMatcher.group(groupCount));
                }

                if (groupCount == 3 || groupCount == 4) {
                    System.out.println("Parsing click event");

                    // Click event
                    clickEvent = ClickEvent.parse(eventMatcher.group(2), eventMatcher.group(3));
                }

                for (int i = 0; i < newCount - oldCount; i++) {
                    RawPart temp = parts.get(oldCount + i);
                    temp.index += startIndex;
                    temp.hoverEvent = hoverEvent;
                    temp.clickEvent = clickEvent;
                }

                regions.add(startIndex);
                regions.add(eventMatcher.end());
            }

            regions.add(input.length());

            if (regions.size() > 2) {
                for (int i = 0; i < regions.size(); i += 2) {
                    final String substr = input.substring(regions.get(i), regions.get(i + 1));
                    if (!substr.isEmpty()) {
                        System.out.println("Processing " + input.substring(regions.get(i), regions.get(i + 1)));
                        process(input.substring(regions.get(i), regions.get(i + 1)), false, regions.get(i));
                    }
                }
                return;
            }
        }

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

            curPart.index = startIndex + offset;
            lastIndex = startIndex + matcher.group().length();
        }

        System.out.println("Adding final part");
        curPart.index = lastIndex + offset;
        curPart.text = input.substring(lastIndex, input.length());
        parts.add(curPart);
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
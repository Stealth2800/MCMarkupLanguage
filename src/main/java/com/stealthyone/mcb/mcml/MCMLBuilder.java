/**
 * Copyright 2016 Stealth2800 and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stealthyone.mcb.mcml;

import mkremins.fanciful.FancyMessage;
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

    private FancyMessage fancyMessage;

    public MCMLBuilder(String input) {
        if (input == null || input.isEmpty()) {
            // Don't process if input is null or empty
            parts.add(new RawPart());
            return;
        }

        process(input, true, 0);
        condenseParts();
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
                final int startIndex = eventMatcher.start();

                final int oldCount = parts.size();

                // First, add text group as part
                process(eventMatcher.group(1), false, startIndex);

                final int newCount = parts.size();

                HoverEvent hoverEvent = null;
                ClickEvent clickEvent = null;

                // Click event exists if groups 2 and 3 are not null
                if (eventMatcher.group(2) != null && eventMatcher.group(3) != null) {
                    clickEvent = ClickEvent.parse(eventMatcher.group(2), eventMatcher.group(3));

                    // Hover event exists if group 4 is not null
                    if (eventMatcher.group(4) != null) {
                        hoverEvent = HoverEvent.parse(eventMatcher.group(4));
                    }
                } else {
                    hoverEvent = HoverEvent.parse(eventMatcher.group(5));
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
                        process(input.substring(regions.get(i), regions.get(i + 1)), false, regions.get(i));
                    }
                }
                return;
            }
        }

        Matcher matcher = COLOR_PATTERN.matcher(input);
        while (matcher.find()) {
            final int startIndex = matcher.start();

            if (curPart.text == null && curPart.index != -1 && startIndex != lastIndex) {
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

        curPart.index = lastIndex + offset;
        curPart.text = input.substring(lastIndex, input.length());
        parts.add(curPart);
    }

    private void condenseParts() {
        parts.sort((a, b) -> Integer.compare(a.index, b.index));
    }

    public FancyMessage toFancyMessage() {
        if (fancyMessage != null) {
            return fancyMessage;
        }

        fancyMessage = new FancyMessage();

        boolean started = false;
        for (RawPart part : parts) {
            if (started) {
                fancyMessage.then(part.text);
            } else {
                fancyMessage.text(part.text);
                started = true;
            }

            fancyMessage.color(part.color);
            fancyMessage.style(part.getFormats());

            if (part.hoverEvent != null) {
                part.hoverEvent.apply(fancyMessage);
            }

            if (part.clickEvent != null) {
                part.clickEvent.apply(fancyMessage);
            }
        }

        return fancyMessage;
    }

}
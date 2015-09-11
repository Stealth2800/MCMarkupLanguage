/**
 * MCMarkupLanguage - Licensed under the MIT License (MIT)
 *
 * Copyright (c) Stealth2800 <http://stealthyone.com/>
 * Copyright (c) contributors <https://github.com/Stealth2800/MCMarkupLanguage>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.stealthyone.mcb.mcml;


import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MCMLBuilder {

    static final Pattern PATTERN_EVENT = Pattern.compile("^\\((txt|ach|itm|cmd|url|scmd): *([\\S\\s]+?)\\);");
    static final Pattern PATTERN_TEXT_GROUP = Pattern.compile("\\[(.+?)\\];");
    static final Pattern PATTERN_CHATCOLOR = Pattern.compile("&([a-f0-9l-o|kr])");

    String rawText;
    final Map<String, Object> replacements = new HashMap<>();

    final List<TempPart> parts = new ArrayList<>();

    FancyMessage fancyMessage;

    public MCMLBuilder(String input) {
        this(input, null);
    }

    public MCMLBuilder(String input, Map<String, Object> replacements) {
        Validate.notNull(input, "Input cannot be null.");

        this.rawText = input;

        if (this.replacements != null && replacements != null) {
            this.replacements.putAll(replacements);

            for (Entry<String, Object> entry : replacements.entrySet()) {
                if (entry.getValue() instanceof String) {
                    this.rawText = rawText.replace(entry.getKey(), (String) entry.getValue());
                }
            }
        }

        this.rawText = rawText.replace(ChatColor.COLOR_CHAR, '&');

        // Identify text groups
        int lastIndex = 0;

        final Matcher matcher = PATTERN_TEXT_GROUP.matcher(input);
        while (matcher.find()) {
            TempPart part = new TempPart(matcher.group(1));
            if (!parts.isEmpty()) {
                TempPart prevPart = parts.get(parts.size() - 1);
                TextPiece lastPiece = prevPart.text.get(prevPart.text.size() - 1);

                for (TextPiece piece : part.text) {
                    if (piece.color == null) {
                        piece.color = lastPiece.color;
                        piece.italicize = lastPiece.italicize;
                        piece.bold = lastPiece.bold;
                        piece.underline = lastPiece.underline;
                        piece.strikeout = lastPiece.strikeout;
                        piece.magic = lastPiece.magic;
                    }
                }

            }

            if (matcher.start() > lastIndex) {
                // Handle ungrouped text
                TempPart ungroupedPart = new TempPart(rawText.substring(lastIndex, matcher.start()));

                parts.add(ungroupedPart);
            }

            lastIndex = matcher.end();

            // Check for event
            int offset = rawText.length() - input.substring(lastIndex).length();
            final Matcher eventMatcher = PATTERN_EVENT.matcher(input.substring(lastIndex));
            if (eventMatcher.find()) {
                handleEvent(part, eventMatcher);

                lastIndex = eventMatcher.end() + offset;

                offset = rawText.length() - input.substring(lastIndex).length();
                final Matcher secEventMatcher = PATTERN_EVENT.matcher(input.substring(lastIndex));
                if (secEventMatcher.find()) {
                    handleEvent(part, secEventMatcher);

                    lastIndex = secEventMatcher.end() + offset;
                }
            }

            parts.add(part);
        }

        if (lastIndex != rawText.length()) {
            TempPart ungroupedPart = new TempPart(rawText.substring(lastIndex));

            if (!parts.contains(ungroupedPart)) {
                parts.add(ungroupedPart);
            }
        }
    }

    private void handleEvent(TempPart part, Matcher matcher) {
        Event event = Event.parseText(this, matcher.group(1), matcher.group(2));
        if (event instanceof EventClick) {
            part.clickEvent = event;
        } else {
            part.hoverEvent = event;
        }
    }

    public Object getReplacement(String key) {
        Validate.notNull(key, "Key cannot be null.");

        return replacements.get(key);
    }

    /**
     * @return the output FancyMessage instance.
     */
    public FancyMessage getFancyMessage() {
        if (fancyMessage != null) {
            return fancyMessage;
        }

        fancyMessage = new FancyMessage();

        for (int i = 0; i < parts.size(); i++) {
            TempPart part = parts.get(i);

            if (part.text.isEmpty()) {
                continue;
            }

            part.buildOn(fancyMessage);

            if (i < parts.size() - 1) {
                fancyMessage.then();
            }
        }

        return fancyMessage;
    }

    /**
     * @return the raw message that was inputted into this builder.
     */
    public String getRawMessage() {
        return rawText;
    }

}
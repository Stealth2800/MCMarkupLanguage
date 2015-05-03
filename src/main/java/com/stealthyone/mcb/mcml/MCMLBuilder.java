/*
 * Minecraft Markup Language (MCML)
 * Copyright (C) 2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

public class MCMLBuilder {

    private String rawText;

    private MCMLTempPart curPart;
    private List<MCMLTempPart> parts = new ArrayList<>();

    private Map<String, Object> itemReplacements = new HashMap<>();

    final static Pattern EVENT_BEGIN_PATTERN = Pattern.compile("(\\((ach|itm|txt)|\\[(cmd|scmd|url)):(.*)");
    final static Pattern COLOR_PATTERN = Pattern.compile("&[a-f0-9]");
    final static Pattern FORMAT_PATTERN = Pattern.compile("&[l-o|kr]");
    final static Pattern CHATCOLOR_PATTERN = Pattern.compile("&[a-f0-9l-o|kr]");

    public MCMLBuilder(String inputText) {
        this(inputText, null);
    }

    public MCMLBuilder(String inputText, Map<String, String> replacements) {
        this(inputText, replacements, null);
    }

    public MCMLBuilder(String inputText, Map<String, String> replacements, Map<String, Object> itemReplacements) {
        Validate.notNull(inputText, "Input text cannot be null.");

        this.rawText = inputText;

        if (replacements != null) {
            for (Entry<String, String> entry : replacements.entrySet()) {
                rawText = rawText.replace(entry.getKey(), entry.getValue());
            }
        }

        this.rawText = rawText.replace(ChatColor.COLOR_CHAR, '&');

        if (itemReplacements != null) {
            this.itemReplacements.putAll(itemReplacements);
        }

        // Separate into different parts
        curPart = new MCMLTempPart();

        char[] chars = rawText.toCharArray();
        int lastTextIndex = -1;
        for (int i = 0; i < chars.length; i++) {
            if (MCMLBuilder.EVENT_BEGIN_PATTERN.matcher(rawText.substring(i)).matches()) {
                // Attempt to get event
                int balancedEnd = getBalancedGroupingSymbol(chars[i], rawText, i);
                if (balancedEnd != -1) {
                    if (curPart.chars.size() > 0) {
                        handleRawText(curPart.chars);
                    }

                    String trimmedEvent = rawText.substring(i, balancedEnd + 1);
                    i = balancedEnd;
                    if (trimmedEvent.startsWith("[")) {
                        // Get click event
                        MCMLClickEvent clickEvent;
                        try {
                            clickEvent = MCMLClickEvent.parseText(this, trimmedEvent);
                        } catch (Exception ex) {
                            // Not valid syntax
                            continue;
                        }

                        curPart.clickEvent = clickEvent;
                    } else {
                        // Get hover event
                        MCMLHoverEvent hoverEvent;
                        try {
                            hoverEvent = MCMLHoverEvent.parseText(this, trimmedEvent);
                        } catch (Exception ex) {
                            // Not valid syntax
                            continue;
                        }

                        curPart.hoverEvent = hoverEvent;
                    }
                    continue;
                }
            }

            // Just a normal character, add to the string.
            if (lastTextIndex != -1 && lastTextIndex != i - 1) {
                handleRawText(curPart.chars);
                advanceCurPart();
            }
            curPart.chars.add(chars[i]);
            lastTextIndex = i;
        }

        handleRawText(curPart.chars);
        parts.add(curPart);
    }

    public MCMLBuilder useReplacements(Map<String, String> replacements) {
        return new MCMLBuilder(rawText, replacements);
    }

    public MCMLBuilder useAllReplacements(Map<String, String> replacements, Map<String, Object> itemReplacements) {
        return new MCMLBuilder(rawText, replacements, itemReplacements);
    }

    public MCMLBuilder useItemReplacements(Map<String, Object> itemReplacements) {
        return new MCMLBuilder(rawText, null, itemReplacements);
    }

    /**
     * Gets the index of the next grouping symbol that balances out the first.
     *
     * @param balChar Character to balance, either [ or (.
     * @param text Text to check.
     * @param fromIndex What index of the text to start from. Should match the balChar.
     * @return The index of the balancing symbol.
     *         -1 if no balance was found.
     */
    private int getBalancedGroupingSymbol(char balChar, String text, int fromIndex) {
        char otherChar = balChar == '[' ? ']' : ')';

        char[] array = text.toCharArray();
        int bal = 0;
        for (int i = fromIndex; i < array.length; i++) {
            if (array[i] == balChar) {
                bal++;
            } else if (array[i] == otherChar) {
                if (--bal == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Advances the current part of the message we're working on.
     */
    private void advanceCurPart() {
        parts.add(curPart);
        curPart = new MCMLTempPart();
    }

    private void handleRawText(List<Character> characterList) {
        StringBuilder string = new StringBuilder();
        for (Character ch : characterList) {
            string.append(ch);
        }
        handleRawText(string.toString());
    }

    /**
     * Colors and formats specified text before adding it to the curPart.
     *
     * @param text Text to color and format.
     */
    private void handleRawText(String text) {
        if (curPart.getText() != null) return;

        if (!matchChatColors(text)) {
            addFinalText(text);
        }
    }

    /**
     * Adds finalized text to the cur part.
     *
     * @param text Text to add.
     */
    private void addFinalText(String text) {
        if (curPart.getText() != null) {
            advanceCurPart();
        }
        curPart.text.append(text);
    }

    private boolean matchChatColors(String text) {
        Matcher codeMatcher = MCMLBuilder.CHATCOLOR_PATTERN.matcher(text);
        boolean matches = false;
        int lastIndex = 0;

        while (codeMatcher.find()) {
            matches = true;

            // Text before the code
            String subStr = text.substring(lastIndex, codeMatcher.start());
            if (subStr.length() > 0) {
                // If there actually is text before the color code, check for formats
                addFinalText(subStr);
            }
            lastIndex = codeMatcher.end();

            String rawCode = codeMatcher.group();
            applyChatColors(ChatColor.getByChar(rawCode.charAt(1)));
        }

        if (matches) {
            // If there were matches, the last part of the text was not added
            String last = text.substring(lastIndex, text.length());
            if (last.length() > 0) {
                // There actually is text after the last color code
                curPart.text.append(last);
                /*if (!matchFormats(last)) {
                    // If there aren't any formats, the text wasn't added, so lets add it here
                    curPart.text.append(last);
                }*/
            }
        }

        return matches;
    }

    /**
     * Applies chat colors to the text in the curPart.
     *
     * @param chatColor ChatColor to apply.
     */
    private void applyChatColors(ChatColor chatColor) {
        // Valid chat color, now is it a format or color?
        if (curPart.getText() != null) {
            // This causes the color formatting to behave like ChatColor.translateAlternateColorCodes formats strings.
            ChatColor oldColor = curPart.color;
            advanceCurPart();
            curPart.color = oldColor;
        }

        if (chatColor.isColor()) {
            // It is a color
            if (curPart.color != null) {
                // The current part already has a color, create a new part
                advanceCurPart();
            }
            curPart.color = chatColor;
        } else {
            // Not a color code, must be a formatting code
            switch (chatColor) {
                case BOLD:
                    if (curPart.isBold) advanceCurPart();
                    curPart.isBold = true;
                    break;

                case ITALIC:
                    if (curPart.isItalic) advanceCurPart();
                    curPart.isItalic = true;
                    break;

                case MAGIC:
                    if (curPart.isMagic) advanceCurPart();
                    curPart.isMagic = true;
                    break;

                case RESET:
                    // If it's reset, the color is also reset, so next section
                    advanceCurPart();
                    break;

                case STRIKETHROUGH:
                    if (curPart.isStrikethrough) advanceCurPart();
                    curPart.isStrikethrough = true;
                    break;

                case UNDERLINE:
                    if (curPart.isUnderline) advanceCurPart();
                    curPart.isUnderline = true;
                    break;
            }
        }
    }

    Object getItemReplacement(String key) {
        return itemReplacements.get(key);
    }

    private ChatColor[] getStyles(MCMLTempPart part) {
        List<ChatColor> styles = new ArrayList<>();

        if (part.isBold)
            styles.add(ChatColor.BOLD);
        if (part.isItalic)
            styles.add(ChatColor.ITALIC);
        if (part.isMagic)
            styles.add(ChatColor.MAGIC);
        if (part.isStrikethrough)
            styles.add(ChatColor.STRIKETHROUGH);
        if (part.isUnderline)
            styles.add(ChatColor.UNDERLINE);

        return styles.toArray(new ChatColor[styles.size()]);
    }

    /**
     * Builds the text into Fanciful's FancyMessage class.
     *
     * @return New FancyMessage instance.
     */
    public FancyMessage buildFancyMessage() {
        FancyMessage message = new FancyMessage();

        boolean started = false;
        for (MCMLTempPart part : parts) {
            if (part.getText() == null) {
                // No text = invalid
                continue;
            }

            // Add text
            if (!started) {
                message.text(part.getText());
                started = true;
            } else {
                message.then(part.getText());
            }

            // Add color
            if (part.color != null) {
                message.color(part.color);
            }

            // Add styles
            message.style(getStyles(part));

            // Add click event
            if (part.clickEvent != null) {
                part.clickEvent.buildOn(message);
            }

            // Add hover event
            if (part.hoverEvent != null) {
                part.hoverEvent.buildOn(message);
            }
        }

        return message;
    }

    /**
     * Builds a string version of the message in order to be able to send messages to the console from this class.
     *
     * @param colors Whether or not to include colors.
     * @param formatting Whether or not to include the formatting codes.
     * @return The simplified string.
     */
    public String buildNormalMessage(boolean colors, boolean formatting) {
        StringBuilder sb = new StringBuilder();

        for (MCMLTempPart part : parts) {
            if (part.getText() == null) {
                continue;
            }

            if (colors) sb.append(part.color);

            if (formatting) {
                if (part.isBold) sb.append(ChatColor.BOLD);
                if (part.isItalic) sb.append(ChatColor.ITALIC);
                if (part.isMagic) sb.append(ChatColor.MAGIC);
                if (part.isUnderline) sb.append(ChatColor.UNDERLINE);
                if (part.isStrikethrough) sb.append(ChatColor.STRIKETHROUGH);
            }

            sb.append(part.getText());
        }

        return sb.toString();
    }

    /**
     * Doesn't really 'build' anything, just returns the raw message.  The name is to be consistent with the other two 'build' methods.
     *
     * @return The raw string.
     */
    public String buildRawMessage() {
        return rawText;
    }

}
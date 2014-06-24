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

public class MCMLBuilder {

    private MCMLTempPart curPart;
    private List<MCMLTempPart> parts = new ArrayList<>();

    private Map<String, Object> replacements;

    public MCMLBuilder(String inputText) {
        this(inputText, new HashMap<String, Object>());
    }

    public MCMLBuilder(String inputText, Map<String, Object> replacements) {
        Validate.notNull(inputText, "Input text cannot be null.");
        Validate.notNull(replacements, "Replacements cannot be null.");
        Validate.noNullElements(replacements.keySet(), "Replacements map cannot contain null keys.");
        this.replacements = replacements;

        char[] inputChars = inputText.toCharArray();

        curPart = new MCMLTempPart();
        int lastTextIndex = -1;

        mainLoop:
        for (int i = 0; i < inputChars.length; i++) {
            if (inputChars[i] == '[') {
                // Click event?
                // Try to find the end
                for (int j = i; j < inputChars.length; j++) {
                    if (inputChars[j] == ']') {
                        // Found the end
                        MCMLClickEvent clickEvent;
                        try {
                            clickEvent = MCMLClickEvent.parseText(this, inputText.substring(i, j + 1));
                        } catch (Exception ex) {
                            // Not valid syntax
                            ex.printStackTrace();
                            break;
                        }

                        curPart.clickEvent = clickEvent;
                        i = j;

                        continue mainLoop;
                    }
                }
            } else if (inputChars[i] == '(') {
                // Hover event?
                // Try to find the end
                for (int j = i; j < inputChars.length; j++) {
                    if (inputChars[j] == ')') {
                        // Found the end
                        MCMLHoverEvent hoverEvent;
                        try {
                            hoverEvent = MCMLHoverEvent.parseText(this, inputText.substring(i, j + 1));
                        } catch (Exception ex) {
                            // Not valid syntax
                            ex.printStackTrace();
                            break;
                        }

                        curPart.hoverEvent = hoverEvent;
                        i = j;

                        continue mainLoop;
                    }
                }
            } else if (inputChars[i] != '&') {
                // Character is not an ampersand, add to text
                if (lastTextIndex != -1 && lastTextIndex != i - 1) {
                    // There was some sort of break between text, end this section
                    advanceCurPart();
                }
                curPart.chars.add(inputChars[i]);
                lastTextIndex = i;
                continue;
            }

            // Character is an ampersand, check to see if it's a color or formatting code
            char next;
            try {
                next = inputChars[i + 1];
            } catch (IndexOutOfBoundsException ex) {
                // End of string, end the loop.
                break;
            }

            ChatColor chatColor = ChatColor.getByChar(next);
            if (chatColor == null) {
                // Invalid chat color, ignore the code
                continue;
            }
            checkChatColor(chatColor);
            lastTextIndex = -1;
            i++;
        }
        parts.add(curPart);
    }

    private void advanceCurPart() {
        parts.add(curPart);
        curPart = new MCMLTempPart();
    }

    private void checkChatColor(ChatColor chatColor) {
        // Valid chat color, now is it a format or color?
        if (curPart.getText() != null) {
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
                    curPart.isBold = true;
                    break;

                case ITALIC:
                    curPart.isItalic = true;
                    break;

                case MAGIC:
                    curPart.isMagic = true;
                    break;

                case RESET:
                    // If it's reset, the color is also reset, so next section
                    advanceCurPart();
                    break;

                case STRIKETHROUGH:
                    curPart.isStrikethrough = true;
                    break;

                case UNDERLINE:
                    curPart.isUnderline = true;
                    break;
            }
        }
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

    Object getReplacement(String key) {
        return replacements.get(key);
    }

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

}
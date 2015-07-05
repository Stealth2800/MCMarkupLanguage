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
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

final class TempPart {

    final List<TextPiece> text = new ArrayList<>();

    Event clickEvent;
    Event hoverEvent;

    TempPart(String text) {
        Matcher matcher = MCMLBuilder.PATTERN_CHATCOLOR.matcher(text);

        TextPiece curPiece = new TextPiece();
        int lastEnd = 0;
        while (matcher.find()) {
            int curStart = matcher.start();

            if (curStart > lastEnd) {
                // Text

                curPiece.text = text.substring(lastEnd, curStart);

                if (matcher.end() != text.length()) {
                    this.text.add(curPiece);
                    curPiece = new TextPiece();

                    curPiece.handleColor(ChatColor.getByChar(matcher.group(1)));
                }
            } else {
                curPiece.handleColor(ChatColor.getByChar(matcher.group(1)));
            }

            lastEnd = matcher.end();
        }

        if (lastEnd != text.length()) {
            if (curPiece.text != null) {
                // The current piece already has text, advance to the next piece.
                this.text.add(curPiece);
                curPiece = new TextPiece();
            }

            curPiece.text = text.substring(lastEnd);
        }

        if (!this.text.contains(curPiece)) {
            this.text.add(curPiece);
        }
    }

    String getTestOutput() {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n  ");

        builder.append("text: [\n    ");

        for (TextPiece piece : text) {
            builder.append(piece.getTestOutput()).append(",\n  ");
        }

        builder.append("],\n  ");

        builder.append("clickEvent: ").append(clickEvent != null ? clickEvent.getClass().getCanonicalName() : "null");

        builder.append(", ");

        builder.append("\n  ");

        builder.append("hoverEvent: ").append(hoverEvent != null ? hoverEvent.getClass().getCanonicalName() : "null");

        builder.append("\n");

        builder.append("}");

        return builder.toString();
    }

    void buildOn(FancyMessage message) {
        for (int i = 0; i < text.size(); i++) {
            TextPiece piece = text.get(i);
            piece.buildOn(message);

            if (clickEvent != null) {
                clickEvent.buildOn(message);
            }

            if (hoverEvent != null) {
                hoverEvent.buildOn(message);
            }

            if (i < text.size() - 1) {
                message.then();
            }
        }
    }

}
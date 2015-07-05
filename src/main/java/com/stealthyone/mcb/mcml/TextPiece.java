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

final class TextPiece {

    String text;

    ChatColor color;

    boolean underline;
    boolean strikeout;
    boolean italicize;
    boolean bold;
    boolean magic;

    void handleColor(ChatColor color) {
        if (color == ChatColor.RESET) {
            this.color = ChatColor.RESET;
            underline = false;
            strikeout = false;
            italicize = false;
            bold = false;
            magic = false;
            return;
        }

        if (color.isColor()) {
            this.color = color;
            return;
        }

        switch (color) {
            case UNDERLINE:
                underline = true;
                break;

            case STRIKETHROUGH:
                strikeout = true;
                break;

            case ITALIC:
                italicize = true;
                break;

            case BOLD:
                bold = true;
                break;

            case MAGIC:
                magic = true;
                break;

            default:
                throw new IllegalArgumentException("This shouldn't have happened.");
        }
    }

    String getTestOutput() {
        return "{ text=\"" + text +
                "\", color=" + color +
                ", underline=" + underline +
                ", bold=" + bold +
                ", italicized=" + italicize +
                ", magic=" + magic +
                " }";
    }

    void buildOn(FancyMessage message) {
        message.text(text);

        if (color != null && color.isColor()) {
            message.color(color);
        }

        if (underline) {
            message.style(ChatColor.UNDERLINE);
        }

        if (strikeout) {
            message.style(ChatColor.STRIKETHROUGH);
        }

        if (italicize) {
            message.style(ChatColor.ITALIC);
        }

        if (bold) {
            message.style(ChatColor.BOLD);
        }

        if (magic) {
            message.style(ChatColor.MAGIC);
        }
    }

}
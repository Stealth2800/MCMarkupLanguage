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

class MCMLHoverEventText extends MCMLHoverEvent {

    private String text;

    MCMLHoverEventText(String rawText) {
        Validate.notNull(rawText, "Raw text cannot be null.");
        if (rawText.length() == 6) {
            throw new IllegalArgumentException("Text hover event must have input.");
        }
        text = rawText.substring(5, rawText.length() - 1);
    }

    @Override
    void buildOn(FancyMessage message) {
        message.formattedTooltip(new MCMLBuilder(text).buildFancyMessage());
    }

}
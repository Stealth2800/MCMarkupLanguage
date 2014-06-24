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

abstract class MCMLHoverEvent {

    public static MCMLHoverEvent parseText(MCMLBuilder builder, String rawText) {
        Validate.notNull(rawText, "Raw text cannot be null.");
        if (!rawText.matches("\\((ach|itm|txt):(.+)\\)")) {
            throw new IllegalArgumentException("Invalid hover event syntax: '" + rawText + "'");
        }

        switch (rawText.substring(1, 4)) {
            case "ach":
                return new MCMLHoverEventAchievement(rawText);

            case "itm":
                return new MCMLHoverEventItem(builder, rawText);

            case "txt":
                return new MCMLHoverEventText(rawText);
        }
        return null;
    }

    public abstract void buildOn(FancyMessage message);

}
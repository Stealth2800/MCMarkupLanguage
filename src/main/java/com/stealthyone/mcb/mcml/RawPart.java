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

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

final class RawPart {

    int index = -1;

    String text = null;
    ChatColor color = ChatColor.RESET;
    List<ChatColor> formats = new ArrayList<>();
    HoverEvent hoverEvent = null;
    ClickEvent clickEvent = null;

    void handleColor(ChatColor color) {
        System.out.println("Handling color: " + color.name());
        this.color = color;
        if (color == ChatColor.RESET) {
            formats.clear();
        }
    }

    void handleFormat(ChatColor format) {
        System.out.println("Handling format: " + format.name());
        formats.add(format);
    }

    ChatColor[] getFormats() {
        return formats.toArray(new ChatColor[formats.size()]);
    }

}
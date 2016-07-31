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
    ChatColor color = ChatColor.WHITE;
    List<ChatColor> formats = new ArrayList<>();
    HoverEvent hoverEvent = null;
    ClickEvent clickEvent = null;

    void handleColor(ChatColor color) {
        if (color == ChatColor.RESET) {
            this.color = ChatColor.WHITE;
            formats.clear();
        } else {
            this.color = color;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("RawPart{");
        sb.append("text='").append(text).append("',");
        sb.append("color='").append(color.name()).append("',");
        sb.append("index=").append(index).append(",");
        sb.append("hoverEvent=").append(hoverEvent != null).append(",");
        sb.append("clickEvent=").append(hoverEvent != null);
        sb.append("}");

        return sb.toString();
    }

    void handleFormat(ChatColor format) {
        formats.add(format);
    }

    ChatColor[] getFormats() {
        return formats.toArray(new ChatColor[formats.size()]);
    }

}
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

// TODO: Implement item and achievement hover events
abstract class HoverEvent {

    static HoverEvent parse(String input) {
        return new TextHoverEvent(input);
    }

    abstract void apply(FancyMessage message);

    static final class TextHoverEvent extends HoverEvent {

        final FancyMessage tooltip;

        TextHoverEvent(String input) {
            MCMLBuilder builder = new MCMLBuilder();
            builder.process(input, false, 0);
            tooltip = builder.toFancyMessage();
        }

        @Override
        void apply(FancyMessage message) {
            message.formattedTooltip(tooltip);
        }

    }

    static final class ItemHoverEvent extends HoverEvent {

        @Override
        void apply(FancyMessage message) {

        }

    }

    static final class AchievementHoverEvent extends HoverEvent {

        @Override
        void apply(FancyMessage message) {

        }

    }

}
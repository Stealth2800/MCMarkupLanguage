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
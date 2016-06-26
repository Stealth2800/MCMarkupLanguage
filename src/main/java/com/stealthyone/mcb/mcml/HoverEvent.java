package com.stealthyone.mcb.mcml;

import java.util.ArrayList;
import java.util.List;

// TODO: Implement item and achievement hover events
abstract class HoverEvent {

    static HoverEvent parse(String input) {
        return new TextHoverEvent(input);
    }

    static final class TextHoverEvent extends HoverEvent {

        final List<RawPart> parts = new ArrayList<>();

        TextHoverEvent(String input) {
            MCMLBuilder builder = new MCMLBuilder();
            builder.process(input, false, 0);
            parts.addAll(builder.getParts());
        }

    }

    static final class ItemHoverEvent extends HoverEvent {

    }

    static final class AchievementHoverEvent extends HoverEvent {

    }

}
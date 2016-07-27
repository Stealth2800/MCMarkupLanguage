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

final class ClickEvent {

    private static final String TYPE_RUN_COMMAND = "!";
    private static final String TYPE_SUGGEST_COMMAND = "?";
    private static final String TYPE_OPEN_URL = ">";

    static ClickEvent parse(String type, String input) {
        return new ClickEvent(type, input);
    }

    final String type;
    final String value;

    ClickEvent(String type, String value) {
        this.type = type;
        this.value = value;
    }

    void apply(FancyMessage message) {
        switch (type) {
            case TYPE_RUN_COMMAND:
                message.command(value);
                break;

            case TYPE_SUGGEST_COMMAND:
                message.suggest(value);
                break;

            case TYPE_OPEN_URL:
                message.link(value);
                break;
        }
    }

}
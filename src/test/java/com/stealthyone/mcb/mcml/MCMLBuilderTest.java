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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MCMLBuilderTest {

    @Test
    public void execute() {
        List<String> strings = Arrays.asList(
                "Hello world!",
                "&cHello world!",
                "Hello &cworld!",
                "Hello [world](!\"/say Hi\")",
                "&cHello [world](!\"/say Hi\")",
                "Hello [&cworld](!\"/say Hi\")"
        );

        for (String str : strings) {
            System.out.println("----- BEGIN TEST -----\n");

            System.out.println("Input: " + str + "\n");
            testBuilder(str);

            System.out.println("\n----- END TEST -----\n");
        }
    }

    private void testBuilder(String input) {
        MCMLBuilder builder = new MCMLBuilder(input);

        for (RawPart part : builder.getParts()) {
            System.out.println(part.toString() + "\n");
        }
    }

}
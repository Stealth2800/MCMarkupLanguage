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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MCMLBuilderTest {

    @Test
    public void execute() {
        List<String> strings = Arrays.asList(
            "This is test #1",
            "&aThis is test #2",
            "&aThis is &ctest #3",
            "&a&lThis is test #4",
            "&b&aThis is test #5",
            "&b&l&rThis is test #6",
            "&aThis is &b&ltest &c&l&n#7",
            "&a",
            "Hello [Alt text](!\"/say Hello\" \"Hi!\")"
        );

        for (String string : strings) {
            System.out.println("----BEGIN TEST----\n");
            System.out.println("Input: " + string + "\n");
            testBuilder(string);
            System.out.println("\n----END TEST----\n");
        }
    }

    private void testBuilder(String input) {
        MCMLBuilder builder = new MCMLBuilder(input);

        for (RawPart part : builder.getParts()) {
            System.out.println(part.toString() + "\n\n");
        }
    }

}
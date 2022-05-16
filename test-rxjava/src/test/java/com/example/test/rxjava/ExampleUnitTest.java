package com.example.test.rxjava;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    String allowCharIOS = "^[A-Za-z0-9€£¥•\\{\\}'\\\"_~!@#$%^&*()+,.:;=<>?\\/|\\-\\[\\]\\\\]*$";
    String allowV1 = "^[\\w~!@#$%^&*()+,.:;=<>?\\/|\\-\\[\\]\\\\]*$";
    String allowV2 = "^[\\w€£¥•{}'\"~!@#$%^&*()+,.:;=<>?\\/|\\-\\[\\]\\\\]*$";

    @Test
    public void testCount() {
        String text = "中文";
        System.out.println("ios regex: " + Pattern.compile(allowCharIOS).matcher(text).matches());
        System.out.println("v1 regex: " + Pattern.compile(allowV1).matcher(text).matches());
        System.out.println("v2 regex: " + Pattern.compile(allowV2).matcher(text).matches());
    }

    @Test
    public void testDivide() {
        KotlinTest.testDivide();
    }

}
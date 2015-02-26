package com.example.exercise.numberprinter;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

/**
 * NB these test rely on the values in the number_translations.properties file.
 * Changes to that file will require changes to these tests.
 */
public class NumberPrinterTest {
    NumberPrinter instance;

    @Before
    public void setup() {

        instance = new NumberPrinter(
                new Translator(
                        ResourceBundle.getBundle("number_translations")));
    }

    @Test
    public void testConversion() {
        assertEquals("one hundred twenty-three and 04/100 dollars",
                instance.numberToWords(new BigDecimal("123.04")));
        assertEquals("thirteen thousand four hundred fifty-five and 99/100 dollars",
                instance.numberToWords(new BigDecimal("13455.99")));
    }

    @Test
    public void testSkippedZeros() {
        assertEquals("thirteen thousand and 00/100 dollars",
                instance.numberToWords(new BigDecimal("13000.00")));
        assertEquals("one thousand twelve and 00/100 dollars",
                instance.numberToWords(new BigDecimal("1012.0")));
    }

    @Test
    public void testZero() {
        assertEquals("zero and 00/100 dollars",
                instance.numberToWords(new BigDecimal("0.0")));
    }

    @Test
    public void testCentsOnly() {
        assertEquals("zero and 04/100 dollars",
                instance.numberToWords(new BigDecimal("0.04")));
    }

    @Test
    public void testDollarsOnly() {
        assertEquals("one hundred twenty-three and 00/100 dollars",
                instance.numberToWords(new BigDecimal("123.0")));
    }

}


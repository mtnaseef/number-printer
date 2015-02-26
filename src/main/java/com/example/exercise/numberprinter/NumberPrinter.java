package com.example.exercise.numberprinter;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Convert a numeric value, assumed to represent a dollar and cents amount,
 * to a string containing the English word equivalent for that amount.
 */
public class NumberPrinter {
    private Translator translator;

    public NumberPrinter(Translator translator) {
        this.translator = translator;
    }

    /**
     * Convert the numeric amount to the English word representation of the
     * amount, assuming the amount represents a positive dollar value
     * with 1-cent precision. If the fractional part of the value represents a value
     * less than 1 cent, that additional amount is silently ignored.
     * TODO - should we round this instead or consider this an error?
     * This class currently only supports numbers up to 999,999,999,999,999.99.
     * @param amount The amount to convert in dollars with 1-cent precision.
     * @return A string containing the English words for the amount specified.
     * @throws java.lang.IllegalArgumentException if a negative value is passed.
     */
    public String numberToWords(final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Amount must be a non-negative value");
        }
        final long dollars = amount.longValue();
        final int cents = amount.subtract(BigDecimal.valueOf(dollars))
                // We silently ignore any sub-cent amount
                .movePointRight(2).intValue();
        return dollarsToWords(dollars)
                + " and "
                + centsToWords(cents)
                + " dollars";
    }

    private String dollarsToWords(final long dollars) {
        if (dollars == 0) {
            return "zero";
        }
        final StringBuilder result = new StringBuilder();
        return leftToRight(result, dollars, 0).toString();
    }

    /**
     * Recursively build the word name for this value starting
     * at the specified exponent for the right-most digit. The
     * result represents the number read from left to right.
     * @param builder The buffer containing the result.
     * @param amount The amount to convert
     * @param exponent The exponent (base-10) of the least-significant digit.
     * @return A StringBuilder containing the words for the given amount.
     */
    private StringBuilder leftToRight(final StringBuilder builder,
                                      final long amount,
                                      final int exponent) {
        long left = amount / 1000L;
        int right = (int)(amount % 1000L);
        if (left > 0) {
            leftToRight(builder, left, exponent + 3 );
        }

        if (right == 0) {
            return builder;
        }

        final int ones = right % 10;
        final int tens = (right / 10) % 10;
        final int hundreds = right / 100 ;
        if (hundreds != 0 ) {
            builder.append(' ')
                    .append(translator.translateOne(hundreds))
                    .append(" hundred");
        }
        if (tens == 1) {
            builder.append(' ').append(translator.translateTeen(ones));
        } else {
            char tensSeparator = ' ';
            if (tens != 0) {
                builder.append(' ').append(translator.translateTen(tens));
                tensSeparator = '-';
            }
            if (ones != 0) {
                builder.append(tensSeparator).append(translator.translateOne(ones));
            }
        }

        String exponentPart = translator.translateExponent(exponent);
        if (exponentPart != null) {
            builder.append(' ').append(exponentPart);
        }

        // Trim leading space.
        if (builder.charAt(0) == ' ') {
            builder.deleteCharAt(0);
        }
        return builder;
    }

    private String centsToWords(final int cents) {
        return String.format("%02d/100", cents);
    }


    /**
     * Perform a number to word conversion on a single command line argument.
     * @param args should contain a single string containing a non-negative
     *             dollar (and cents) value.
     */
    public static void main(String[] args) {
        BigDecimal value = null;
        if (args.length != 1) {
            System.err.println("Usage: NumberPrinter value");
            System.exit(1);
        }
        try {
            value = new BigDecimal(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("value must be a valid decimal number.");
            System.exit(1);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Value must be non-negative.");
            System.exit(2);
        }
        final ResourceBundle translationConfig = ResourceBundle.getBundle("number_translations");
        Translator translator = new Translator(translationConfig);
        System.out.println(new NumberPrinter(translator).numberToWords(value));
    }
}

/**
 * Provides translation facilities for converting digits to words.
 */
class Translator {
    private String[] ones = new String[10];
    private String[] tens = new String[10];
    private String[] teens = new String[10];
    private Map<Integer, String> exponents = new HashMap<Integer, String>();

    /**
     * Load translation mappings from properties named one.#, ten.#, and teen.#
     * where # is the relevant digit and exponent.# where # is the power of ten
     * to translate.
     * Properties one.[1..9] are the translation for a single digit.
     * Properties ten.[2..9] are the translation for tens digits.
     * Properties for teen.[0..9] are translations for numbers from 10-19.
     * Properties for exponent.# are the names for the thousands, millions, etc.
     * positions. This class only supports translations for exponents 3, 6, 9, and 12.
     * @param config the properties defining the translations.
     */
    public Translator(final ResourceBundle config) {

        ones[1] = config.getString("one.1");
        teens[0] = config.getString("teen.0");
        teens[1]= config.getString("teen.1");
        for (int i = 2; i < 10; i++) {
            ones[i] = config.getString("one." + i);
            tens[i] = config.getString("ten." + i);
            teens[i] = config.getString("teen." + i);
        }
        for (int e = 3; e < 15; e += 3) {
            exponents.put(e, config.getString("exponent." + e));
        }
    }

    public String translateOne(Integer i) {
        return ones[i];
    }

    public String translateTen(Integer i) {
        return tens[i];
    }

    public String translateTeen(Integer i) {
        return teens[i];
    }

    public String translateExponent(Integer e) {
        return exponents.get(e);
    }
}

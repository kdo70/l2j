package net.sf.l2j.gameserver.expander.helpers;

import java.text.DecimalFormat;
import java.util.Locale;

public class Str {
    public static String number(long value) {
        String prefix = "";

        if (value >= 1000000000) {
            value /= 1000000000;
            prefix = "B";
        } else if (value >= 1000000) {
            value /= 1000000;
            prefix = "M";
        }

        return String.format(Locale.US, "%,d%s", value, prefix);
    }

    public static String morph(int num, String... arr) {
        int num100 = num % 100;
        int num10 = num100 % 10;

        String result;
        if (num10 == 1 && (num100 < 11 || num100 > 20)) {
            result = arr[0];
        } else if (num10 >= 2 && num10 <= 4 && (num100 < 11 || num100 > 20)) {
            result = arr[1];
        } else {
            result = arr[2];
        }

        return result;
    }

    public static String percent(double chance) {
        String formatPattern = (chance <= 0.001) ? "#.####" : (chance <= 0.01) ? "#.###" : "##.##";
        return new DecimalFormat(formatPattern).format(chance);
    }

    public static String numFormat(int value) {
        return String.format(Locale.US, "%,d", value);
    }
}
package me.noxerek.scuti.util;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author netindev
 */
public class StringUtil {

    private static final StringBuilder MASSIVE_STRING;

    static {
        MASSIVE_STRING = new StringBuilder(Byte.MAX_VALUE - 1);
    }

    public static String getMassiveString() {
        if (MASSIVE_STRING.length() > 0) {
            return MASSIVE_STRING.toString();
        }
        for (int i = 0; i < 126; i++) {
            String s;
            s = IntStream.range(0, 1).mapToObj(i2 -> (ThreadLocalRandom.current().nextBoolean()) ? "0x6d 0x61 0x74 0x74 0x65 0x72 0x0a 0x61 0x75 0x6e 0x74 0x0a 0x76 0x61 0x6c 0x75 0x65 0x0a 0x68 0x61 0x72 0x61 0x73 0x73 0x0a 0x61 0x70 0x70 0x6c 0x69 0x65 0x64\n" : "\n0x64 0x65 0x73 0x65 0x72 0x76 0x65 0x0a 0x73 0x68 0x65 0x6c 0x66 0x0a 0x63 0x61 0x73 0x74 0x6c 0x65 0x0a 0x68 0x65 0x61 0x72 0x0a 0x66 0x75 0x72\n").collect(Collectors.joining());
            MASSIVE_STRING.append(s);
        }
        return MASSIVE_STRING.toString();
    }

    public static String getRandomString() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public static String getFileSize(final long size) {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        final float kilobytes = 1024.0F, megabytes = kilobytes * kilobytes;
        return size < megabytes ? decimalFormat.format(size / kilobytes) + " KB"
                : decimalFormat.format(size / megabytes) + " MB";
    }

}

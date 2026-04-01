package util;

import java.time.Duration;

public class DurationStringFormatter {
    public static String formatDuration(Duration duration) {
        if (duration == null || duration.isNegative()) return "0m";

        long h = duration.toHours();
        long m = duration.toMinutes() % 60;

        if (h > 0 && m > 0) {
            return String.format("%d h %d min", h, m);
        }

        else if (h > 0) {
            return h + " h";
        }
        else {
            return m + " min";
        }
    }
}

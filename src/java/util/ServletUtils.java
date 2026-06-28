package util;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Small helpers to safely parse request parameters without littering
 * every servlet with try/catch NumberFormatException blocks.
 */
public class ServletUtils {

    public static int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static BigDecimal parseDecimalOrDefault(String value, BigDecimal defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    public static int getParamInt(HttpServletRequest req, String name, int defaultValue) {
        return parseIntOrDefault(req.getParameter(name), defaultValue);
    }

    public static String getParamTrimmed(HttpServletRequest req, String name) {
        return trimOrNull(req.getParameter(name));
    }
}

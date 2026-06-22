package lld.practice.infra.notificationservice.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Substitutes {{varName}} placeholders with payload values.
 * Missing variables fall back to empty string.
 */
public final class TemplateRenderer {
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    private TemplateRenderer() {}

    public static String render(String template, Map<String, Object> vars) {
        if (template == null) return "";
        Matcher m = VAR_PATTERN.matcher(template);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            Object val = vars != null ? vars.get(key) : null;
            m.appendReplacement(out, Matcher.quoteReplacement(val == null ? "" : val.toString()));
        }
        m.appendTail(out);
        return out.toString();
    }
}

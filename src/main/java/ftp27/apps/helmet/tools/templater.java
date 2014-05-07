package ftp27.apps.helmet.tools;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by ftp27 on 08.05.14.
 */
public class templater {
    public static String Compile(String template, Map<String, String> values) {
        for (String Key: values.keySet()) {
            template = pasteString(template, "\\$\\{"+Key+"\\}", values.get(Key));
        }
        return template;
    }

    public static String pasteString(String template, String key, String value) {
        Pattern pattern = Pattern.compile(key);
        String[] parts = pattern.split(template);
        String result = "";
        if (parts.length > 1) {
            for (int i=0; i<parts.length-1; i++) {
                result += parts[i]+value;
            }
            result += parts[parts.length-1];
        } else {
            result = template;
        }

        return result;
    }
}

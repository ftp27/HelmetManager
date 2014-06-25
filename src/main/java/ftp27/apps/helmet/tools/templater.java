package ftp27.apps.helmet.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by ftp27 on 08.05.14.
 */
public class templater {
    private static String LOG_TAG = "Class [templater]";

    public static String Compile(String template, Map<String, String> values) {
        for (String Key: values.keySet()) {
            Log.d(LOG_TAG,"values["+Key+"] = "+values.get(Key));
            template = pasteString(template, "\\$\\{"+Key+"\\}", values.get(Key));
        }
        return template;
    }

    public static String pasteString(String template, String key, String value) {
        Pattern pattern = Pattern.compile(key);
        String[] parts = pattern.split(template);
        String result = "";
        Log.d(LOG_TAG, "Key = ["+key+"]; parts = "+parts.length);
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

    public String getTemplate(String templateName) {
        Log.d(LOG_TAG, "Checking file " + "/templates/" + templateName + ".html");
        InputStream stream = this.getClass().getResourceAsStream("/templates/"+templateName+".html");
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();
        String aux = "";
        String message = "";

        try {
            while ((aux = input.readLine()) != null) {
                builder.append(aux);
            }
            message = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return message;
    }
}

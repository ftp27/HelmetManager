package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;
import ftp27.apps.helmet.tools.templater;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class site {
    private boolean DEBUG = true;

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                                 Properties parms, Properties files) {
        String msg = getTemplate("header");
        String[] uris = uri.split("/");

        if (DEBUG) {
            Map<String,String> debuginfo = new HashMap<String,String>();
            String debug ="";
            debug += "URI: " + uri + "<br>";
            debug += "method: " + method + "<br>";
            debug += "URI length: " + uris.length + "<br>";
            if (uris.length > 0) {
                for (int i = 0; i < uris.length; i++) {
                    debug += "URI[" + i + "]: " + uris[i] + "<br>";
                }
            }

            debuginfo.put("debuginfo",debug);
            msg += templater.Compile(getTemplate("debug"),debuginfo);
        }

        msg += "<div class=\"content\">\n" +
                getTemplate("navpanel") +
                "<div class=\"nav-content\">";
        if (uris.length > 2) {
            String action = uris[2].toLowerCase();
            if (action.equals("file")) {
                msg += getTemplate("file");
            }
        }
            msg += "</div>";
        msg += "</div>";


        msg += getTemplate("footer");
        msg += "</body></html>\n";

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }

    private String getTemplate(String templateName) {
        Log.d("Class [site]", "Checking file " + "/templates/" + templateName + ".html");
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

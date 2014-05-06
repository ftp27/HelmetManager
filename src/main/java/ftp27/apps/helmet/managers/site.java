package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.io.*;
import java.util.Properties;

public class site {
    private boolean DEBUG = true;

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                                 Properties parms, Properties files) {
        String msg = "<html><head>" +
                "<title>Helmet Manager</title>" +
                "<script src=\"/res/site/jquery.min.js\"></script>" +
                "<script src=\"/res/site/script.js\"></script>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/res/site/style.css\">" +
                "</head>" +
                "<body>";
        msg += getHeader();
        String[] uris = uri.split("/");

        if (DEBUG) {
            msg += "<div class=\"debug\">";
            msg += "URI: " + uri + "<br>";
            msg += "method: " + method + "<br>";
            msg += "URI length: " + uris.length + "<br>";
            if (uris.length > 0) {

                for (int i = 0; i < uris.length; i++) {
                    msg += "URI[" + i + "]: " + uris[i] + "<br>";
                }
            }
            msg += "</div>";
        }

        msg += "<div class=\"content\">\n" +
                getNavPanel() +
                "<div class=\"nav-content\">";
        if (uris.length > 2) {
            String action = uris[2].toLowerCase();
            if (action.equals("file")) {
                msg += getTemplate("file");
            }
        }
            msg += "</div>";
        msg += "</div>";


        msg += getFooter();
        msg += "</body></html>\n";

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }

    private String getHeader() {
        String header = "<div class=\"header\"><img height=\"90%\" src=\"/res/site/img/test.png\">Helmet Manager\n" +
                "<!-- Header -->\n" +
                "</div>\n";
        return header;
    }

    private String getFooter() {
        String footer = "<div class=\"footer\">" +
                "<a href=\"http://ftp27host.ru\">http://ftp27host.ru</a>\n" +
                "<!-- Footer -->\n" +
                "</div>";
        return footer;
    }

    private String getNavPanel() {
        String msg = "<div class=\"nav-panel\">"+
                "</div>";
        return msg;
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

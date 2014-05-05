package ftp27.apps.helmet.managers;

import ftp27.apps.helmet.server.NanoHTTPD;

import java.util.Properties;

public class site {

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                                 Properties parms, Properties files) {
        String msg = "<html><head>" +
                "<title>Helmet Manager</title>" +
                "<script src=\"/res/site/jquery.min.js\"></script>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/res/site/style.css\">" +
                "</head>" +
                "<body>";
        msg += getHeader()+"<hr>";
        msg += "URI: "+uri+"<br>";
        msg += "method: "+method+"<br>";


        String[] uris = uri.split("/");
        msg += "URI length: "+uris.length + "<br>";

        if (uris.length > 0) {
            for (int i = 0; i < uris.length; i++) {
                msg += "URI[" + i + "]: " + uris[i] + "<br>";
            }
            msg += "<hr>";
        }

        msg += "<hr>"+getFooter();
        msg += "</body></html>\n";

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, msg);
    }

    private String getHeader() {
        String header = "<div class=\"header\"><img height=\"90%\" src=\"/res/site/img/test.png\">Helmet Manager\n" +
                "<!-- Header -->\n" +
                "</div>\n" +
                "<div class=\"content\">";
        return header;
    }

    private String getFooter() {
        String footer = "</div>\n" +
                "<div class=\"footer\">" +
                "<a href=\"http://ftp27host.ru\">http://ftp27host.ru</a>\n" +
                "<!-- Footer -->\n" +
                "</div>";
        return footer;
    }
}

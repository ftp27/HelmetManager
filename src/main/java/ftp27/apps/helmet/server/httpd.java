package ftp27.apps.helmet.server;

import android.util.Log;
import ftp27.apps.helmet.managers.fileManager;
import ftp27.apps.helmet.managers.phoneManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ftp27 on 04.05.14.
 */
public class httpd extends NanoHTTPD {
    private phoneManager Phone;


    public httpd(int port, File rootDir) throws IOException {
        super(port, rootDir);
        this.startServer();

        Phone = new phoneManager();

        Log.d("httpd", "startServer");
    }

    @Override
    public Response serve(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        Log.d("httpd", method + " '" + uri + "' ");

        String msg = "<html><title>Helmet Manager</title>" +
                "<body style=\"\n" +
                "    min-width: 700px;\n" +
                "\">";
        msg += getHeader()+"<hr>";
        msg += "URI: "+uri+"<br>";
        msg += "method: "+method+"<br>";


        String[] uris = uri.split("/");
        msg += "URI length: "+uris.length + "<br>";
        if (uris.length > 0) {
            for (int i = 0; i < uris.length; i++) {
                msg += "URI["+i+"]: "+uris[i] + "<br>";
            }
            msg += "<hr>";
            if (uris[1].toLowerCase().equals("file")) {
                msg += fileManager.request(uri, method, header, parms, files);
            } else if (uris[1].toLowerCase().equals("info")) {
                msg += Phone.request(uri, method, header, parms, files);
            }

            msg += "<hr>";
        }


        msg += "<hr>"+getFooter();
        msg += "</body></html>\n";

        return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
    }

    private String getHeader() {
        String header = "<div style=\"\n" +
                "    /* background-color: red; */\n" +
                "    height: 50px;\n" +
                "    /* width: 100%; */\n" +
                "    font-size: 40px;\n" +
                "    font-style: italic;\n" +
                "    margin-left: 20px;\n" +
                "\">Helmet Manager\n" +
                "\t\t\t<!-- Header -->\n" +
                "\t\t</div>\n" +
                "\t\t<div style=\"\n" +
                "    /* background-color: green; */ \n" +
                "    min-height: 100px; \n" +
                "    /* width: 100%; */\n" +
                "\">";
        return header;
    }

    private String getFooter() {
        String footer = "</div>\n" +
                "\t\t<div style=\"\n" +
                "    /* background-color: blue; */ \n" +
                "    height: 20px; width: 100%;\n" +
                "    align-content: center;\n" +
                "            \n" +
                "    \n" +
                "    text-align: center;\n" +
                "\"><a href=\"http://ftp27host.ru\" style=\"\n" +
                "    text-decoration: none;\n" +
                "    font-size: 15px;\n" +
                "    color: black;\n" +
                "\">http://ftp27host.ru</a>\n" +
                "\t\t\t<!-- Footer -->\n" +
                "\t\t</div>";
        return footer;
    }

}

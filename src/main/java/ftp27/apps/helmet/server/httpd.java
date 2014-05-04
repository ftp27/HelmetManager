package ftp27.apps.helmet.server;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ftp27 on 04.05.14.
 */
public class httpd extends NanoHTTPD {
    public httpd(int port, File rootDir) throws IOException {
        super(port, rootDir);
        this.startServer();
        Log.d("httpd", "startServer");
    }

    @Override
    public Response serve(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        Log.d("httpd", method + " '" + uri + "' ");

        String msg = "<html><body><h1>Hello server</h1>\n";

        if (parms.get("username") == null)
            msg +=
                    "<form action='?' method='get'>\n" +
                            "  <p>Your name: <input type='text' name='username'></p>\n" +
                            "</form>\n";
        else
            msg += "<p>Hello, " + parms.get("username") + "!</p>";

        msg += "</body></html>\n";

        return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, msg);
    }

}

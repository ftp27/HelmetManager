package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.io.*;
import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class res {
    public NanoHTTPD.Response request(String uri, String method, Properties header,
                                 Properties parms, Properties files) {

        String[] uris = uri.split("/");
        String Address = "/";//File.pathSeparator;
        if (uris.length > 2) {
            for (int i = 2; i < uris.length; i++) {
                Address += uris[i];//File.pathSeparator;
                if (i != uris.length - 1) {
                    Address += "/";
                }
            }
        }

        Log.d("Class [res]", "Checking file "+Address);

        InputStream in = getClass().getResourceAsStream(Address);

       String mime = NanoHTTPD.MIME_HTML;
       String[] nameFile = uris[uris.length-1].split("\\.");
        if (nameFile.length > 0) {
            String extension = nameFile[nameFile.length - 1];
            if (extension.equals("js")) {
                mime = NanoHTTPD.MIME_JAVASCRIPT;
            } else if (extension.equals("css")) {
                mime = NanoHTTPD.MIME_CSS;
            } else if (extension.equals("png")) {
                mime = NanoHTTPD.MIME_PNG;
            } else if ((extension.equals("jpg")) ||
                    (extension.equals("jpeg"))) {
                mime = NanoHTTPD.MIME_JPEG;
            }
        }


        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, mime, in);

    }
}

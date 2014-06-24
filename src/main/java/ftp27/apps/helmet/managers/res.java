package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;
import ftp27.apps.helmet.server.NanoHTTPD.Response;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class res {
    private static String LOG_TAG = "Class [res]";

    public Response request(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms,
                            Map<String, String> files) {

        String[] uris = uri.split("/");
        String Address = "/site"+getAddress(uri);//File.pathSeparator;

        Log.d(LOG_TAG, "Checking file "+Address);

        InputStream in = getClass().getResourceAsStream(Address);

        String mime = getMimeType(uris[uris.length-1],NanoHTTPD.MIME_HTML);

        return new Response(Response.Status.OK, mime, in);
    }

    public NanoHTTPD.Response download(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms,
                                       Map<String, String> files) {
        String[] uris = uri.split("/");
        String Address = getAddress(uri);

        try {
            InputStream in =  new FileInputStream(Address);
            String mime = getMimeType(uris[uris.length-1],NanoHTTPD.MIME_DEFAULT_BINARY);
            return new NanoHTTPD.Response(Response.Status.OK, mime, in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new NanoHTTPD.Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_DEFAULT_BINARY, "");
    }

    public static String getAddress(String uri) {
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
        return Address;
    }

    public static String getMimeType(String fileName, String defaultType) {
        String mime = defaultType;
        String[] nameFile = fileName.split("\\.");
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
            } else if ((extension.equals("gif"))) {
                mime = NanoHTTPD.MIME_GIF;
            } else if ((extension.equals("tiff"))) {
                mime = NanoHTTPD.MIME_TIFF;
            } else if ((extension.equals("pdf"))) {
                mime = NanoHTTPD.MIME_PDF;
            }
        }
        return mime;
    }
}

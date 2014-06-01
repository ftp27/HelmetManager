package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.io.*;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class file {
    private static final String LOG_TAG = "Class [file]";
    private static final String ERROR_BADINPUT = "Bad input data";
    private static final String ERROR_NOINPUT = "Not enought input data";

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        String message = "";

        if (method.equals("POST")) {
            String Action = parms.getProperty("action");
            if (Action != null) {
                if ((Action.equals("cut")) || (Action.equals("copy"))) {

                    String Source = parms.getProperty("source");
                    String Dest = parms.getProperty("dest");
                    if ((Source != null) && (Dest != null)) {
                        if (Action.equals("cut")) {
                            message = moveFile(new File(Source).toPath(), new File(Dest).toPath());
                        } else {
                            message = copyFile(new File(Source).toPath(), new File(Dest).toPath());
                        }
                    } else {
                        message = getErrorCode(ERROR_NOINPUT);
                    }

                } else if (Action.equals("delete")) {

                    String file = parms.getProperty("file");
                    if (file != null) {
                        message = deleteFile(new File(file).toPath());
                    } else {
                        message = getErrorCode(ERROR_NOINPUT);
                    }

                } else if (Action.equals("newdir")) {

                    String file = parms.getProperty("file");
                    if (file != null) {
                        message = createDirectory(new File(file).toPath());
                    } else {
                        message = getErrorCode(ERROR_NOINPUT);
                    }

                }
            } else {
                message = getErrorCode(ERROR_NOINPUT);
            }
        } else {

            String[] uris = uri.split("/");
            String Address = "";//File.pathSeparator;
            String Parent = "";

            if (uris.length > 2) {
                for (int i = 2; i < uris.length; i++) {
                    Address += uris[i] + "/";
                    if (i == uris.length - 2) {
                        Parent = new String(Address);
                    }
                }
            }

            Log.d(LOG_TAG, Address);

            message = "{ \"fileName\": \"" + uris[uris.length - 1] + "\",";//Address+"<br>";

            if (Address.length() == 0) {
                Address = "/";
            }
            File file = new File(Address);
            //message += "[]: "+toLink(uri,"..")+"<br>";

            if (file.exists()) {
                message += "\"fileAddress\":\"" + Address + "\",";
                if (file.isDirectory()) {
                    message += "\"fileType\":\"directory\",";
                    message += "\"files\" : [";

                    File[] dir_files = file.listFiles();

                    if (Address.length() > 1) {
                        message += "{ \"fileName\": \"..\",";
                        message += "\"fileAddress\":\"" + Parent + "\",";
                        message += "\"fileType\":\"directory\"";
                        message += "}";
                        if (dir_files.length > 0) {
                            message += ",";
                        }
                    }

                    if (dir_files.length > 0) {
                        for (int i = 0; i < dir_files.length; i++) {
                            message += "{ \"fileName\": \"" + dir_files[i].getName() + "\",";
                            message += "\"fileAddress\":\"" + Address + dir_files[i].getName() + "/\",";
                            if (dir_files[i].isDirectory()) {
                                message += "\"fileType\":\"directory\"";
                            } else {
                                message += "\"fileType\":\"file\"";
                            }
                            message += "}";
                            if (i != dir_files.length - 1) {
                                message += ",";
                            }
                            //message += "["+i+"]: "+toLink(uri,dir_files[i])+"<br>";
                        }
                    }
                    message += "]";
                } else {
                    message += "\"fileType\":\"file\"";
                }
            } else {
                message += "\"fileType\":\"none\"";
            }

            message += "}";
        }

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_JSON, message);
    }

    private String copyFile(Path source, Path dest) {
        String checking = checkfiles(source, dest);
        if (!checking.equals("")) {
            return  checking;
        }

        try {
            Files.copy(source, dest, REPLACE_EXISTING, COPY_ATTRIBUTES);
            return getOkCode();
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String moveFile(Path source, Path dest) {
        String checking = checkfiles(source, dest);
        if (!checking.equals("")) {
            return  checking;
        }

        try {
            Files.move(source, dest, REPLACE_EXISTING, COPY_ATTRIBUTES);
            return getOkCode();
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String deleteFile(Path file) {
        if ((!new File(file.toUri()).exists())) {
            return getErrorCode(ERROR_BADINPUT);
        }

        try {
            Files.delete(file);
            return getOkCode();
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String createDirectory(Path directory) {
        if ((!new File(directory.getParent().toUri()).isDirectory()) ||
            (!new File(directory.toUri()).isDirectory())) {
            return getErrorCode(ERROR_BADINPUT);
        }

        try {
            Files.createDirectory(directory);
            return getOkCode();
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String checkfiles(Path source, Path dest) {
        if ((!new File(source.toUri()).exists()) || (!new File(dest.toUri()).isDirectory())) {
            return getErrorCode(ERROR_BADINPUT);
        } else {
            return "";
        }
    }

    private String getErrorCode(String status) {
        return "{" +
                    "\"Status\":\"error\"," +
                    "\"Code\":\""+status+"\"" +
                "}";
    }

    private String getOkCode() {
        return "{" +
                    "\"Status\":\"ok\""+
                "}";
    }

}

package ftp27.apps.helmet.managers;

import android.util.Log;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.io.*;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;

import java.util.Properties;

/**
 * Created by ftp27 on 05.05.14.
 */
public class file {
    private static final String LOG_TAG = "Class [file]";
    private static final String ERROR_BADADDRESS = "Wrong address";
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
                        Log.d(LOG_TAG, "POST: action = "+Action+"; Source = "+Source+"; Dest = "+Dest);

                        message = copyFile(
                                    new File(Source),
                                    new File(Dest),
                                    Action
                        );
                    } else {
                        message = getErrorCode(ERROR_NOINPUT+": 'source' or 'dest'");
                    }

                } else if (Action.equals("delete")) {

                    String file = parms.getProperty("file");
                    if (file != null) {
                        Log.d(LOG_TAG, "POST: action = "+Action+"; file = "+file);

                        message = deleteFile(
                                new File(file)
                        );
                    } else {
                        message = getErrorCode(ERROR_NOINPUT+": 'file' do not exist");
                    }

                } else if (Action.equals("newdir")) {

                    String file = parms.getProperty("file");
                    if (file != null) {
                        Log.d(LOG_TAG, "POST: action = "+Action+"; file = "+file);

                        message = createDirectory(
                                new File(file)
                        );
                    } else {
                        message = getErrorCode(ERROR_NOINPUT+": 'file' do not exist");
                    }

                } else {
                    message = getErrorCode(ERROR_NOINPUT+": wrong 'action'");
                }
            } else {
                message = getErrorCode(ERROR_NOINPUT+": 'action' do not exist");
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

            message = "{ \"fileName\": \"" + uris[uris.length - 1] + "\",";

            if (Address.length() == 0) {
                Address = "/";
            }
            File file = new File(Address);

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
                            message += "\"fileAddress\":\"" + Address + dir_files[i].getName() + "\",";
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
                message += "\"fileType\":\"file\"";
            }

            message += "}";
        }

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_JSON, message);
    }

    public NanoHTTPD.Response upload(String uri, String method, Properties header,
                                             Properties parms, Properties files) {
        String Address = res.getAddress(uri);
        String FileName = URLDecoder.decode(parms.getProperty("files[]"));

        File source = new File(files.getProperty("files[]"));
        File dest = new File(Address+"/"+FileName);

        String answer =
            "{" +
                "\"files\":" +
                    "[" +
                        "{" +
                            "\"url\": \"/res/"+Address+"/"+FileName+"\"," +
                            "\"name\": \""+FileName+"\"," +
                            "\"type\": \""+
                                res.getMimeType(FileName,NanoHTTPD.MIME_DEFAULT_BINARY)+"\"," +
                            "\"size\": \""+
                                source.length()+"\"" +
                        "}" +
                    "]" +
            "}";

        try {
            CopyOrCutFile(source, dest, "cut");
        } catch (IOException e) {
            e.printStackTrace();
            answer =
                "{\"files\": [\n" +
                    "{" +
                        "\"name\": \""+FileName+"\",\n" +
                        "\"size\": "+source.length()+",\n" +
                        "\"error\": \""+e.toString()+"\"\n" +
                    "}" +
                "]}";
        }
        Log.d(LOG_TAG, answer);
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_JSON, answer);
    }

    private String copyFile(File source, File dest, String Action) {
        if (!source.exists())  {
            return getErrorCode(ERROR_BADADDRESS+":'"+source.getPath()+"'");
        }
        if (!dest.getParentFile().exists())  {
            return getErrorCode(ERROR_BADADDRESS+":'"+dest.getPath()+"'");
        }

        if (source.getAbsoluteFile().equals(dest.getAbsoluteFile())) {
            return getErrorCode(ERROR_BADADDRESS+":'source' and 'dest' can't be equals");
        }

        if (dest.getAbsoluteFile().getPath().startsWith(source.getAbsoluteFile().getPath())) {
            return getErrorCode(ERROR_BADADDRESS+":'dest' can't be in 'source'");
        }

        try {
            CopyOrCutFile(source, dest, Action);
            return getOkCode(dest.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String deleteFile(File file) {
        if (!file.exists())  {
            return getErrorCode(ERROR_BADADDRESS+":'"+file.getPath()+"'");
        }

        try {
            recursiveDelete(file);
            return getOkCode(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String createDirectory(File directory) {
        if ((!new File(directory.getParent()).isDirectory()) ||
            (directory.exists())) {
            return getErrorCode(ERROR_BADADDRESS+":'"+directory.getPath()+"'");
        }

        try {
            directory.mkdir();
            return getOkCode(directory.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return getErrorCode(e.toString());
        }
    }

    private String checkFile(File file) {
        if (!file.exists())  {
            return getErrorCode(ERROR_BADADDRESS+":'"+file.getPath()+"'");
        } else {
            return "";
        }
    }

    private String getErrorCode(String code) {
        return "{" +
                    "\"Status\":\"error\"," +
                    "\"Code\":\""+code+"\"" +
                "}";
    }

    private String getOkCode(String code) {
        return "{" +
                    "\"Status\":\"ok\"," +
                    "\"Code\":\""+code+"\"" +
                "}";
    }

    private static void CopyOrCutFile(File source, File dest, String Action)
            throws IOException {
        Log.d(LOG_TAG, Action+": "+source.getPath()+" to "+dest.getPath());
        if (source.isDirectory()) {
            if (!dest.exists()){
                dest.mkdir();
            }

            String[] childrens = source.list();
            for (String child: childrens) {
                CopyOrCutFile(new File(source, child), new File(dest, child), Action);
            }

            if (Action.equals("cut")) {
                source.delete();
            }
        } else {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                if (Action.equals("cut")) {
                    source.delete();
                }
            } finally {
                inputChannel.close();
                outputChannel.close();
            }
        }
    }

    private void recursiveDelete(File file) {
        if (file.isDirectory()) {
            File[] childrens = file.listFiles();
            for (File child: childrens) {
                recursiveDelete(child);
            }
        }
        file.delete();
    }
}

package ftp27.apps.helmet.tools;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ftp27 on 04.05.14.
 */
public class logger {
    private static final String COLOR_SERVER = "green";
    private static final String COLOR_ERROR = "red";
    private static final String COLOR_AUTH = "#CF8500";
    private static final String COLOR_STATUS = "#616161";

    private Handler handler;

    public logger() {
        /*
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                logView.append(Html.fromHtml((String) msg.obj));
            }
        };
        */
    }

    public void serverMessage(String message) {
        append(getColorText(message,COLOR_SERVER));
    }

    public void errorMessage(String message) {
        append(getColorText(message,COLOR_ERROR));
    }

    public void authMessage(String message) {
        append(getColorText(message,COLOR_AUTH));
    }

    public void statusMessage(String message) {
        append(getColorText(message,COLOR_STATUS));
    }

    private void append(CharSequence message) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String date = format.format(d);
        Message msg = new Message();
        msg.obj = "["+date+"]: "+message+"<br>";
        //handler.sendMessage(msg);
    }

    private String getColorText(String Text, String Color) {
        return "<font color='"+Color+"'>"+Text+"</font>";
    }
}

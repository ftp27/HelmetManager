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
    private final TextView logView;
    private Handler handler;

    public logger(TextView logView2) {
        this.logView = logView2;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                logView.append((CharSequence) msg.obj);
            }
        };
    }

    public void append(CharSequence message) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        String date = format.format(d);
        Message msg = new Message();
        msg.obj = "["+date+"]: "+message+"\n\r";
        handler.sendMessage(msg);
    }

    public void statusMessage(String message) {
        append(message);
    }

    public void errorMessage(String message) {
        append(Html.fromHtml("<font color=red>"+message+"</font>"));
    }
}

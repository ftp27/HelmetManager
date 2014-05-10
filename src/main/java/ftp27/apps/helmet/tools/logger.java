package ftp27.apps.helmet.tools;

import android.text.Html;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ftp27 on 04.05.14.
 */
public class logger {
    private TextView logView;

    public logger(TextView logView) {
        this.logView = logView;
    }

    public void append(CharSequence message) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        String date = format.format(d);
        logView.append((CharSequence) "["+date+"]: "+message+"\n\r");
    }

    public void statusMessage(String message) {
        append(message);
    }

    public void errorMessage(String message) {
        append(Html.fromHtml("<font color=red>"+message+"</font>"));
    }
}

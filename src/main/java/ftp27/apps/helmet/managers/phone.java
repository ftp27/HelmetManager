package ftp27.apps.helmet.managers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import ftp27.apps.helmet.server.NanoHTTPD;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ftp27 on 05.05.14.
 */
public class phone {
    private Context context;

    public phone(Context context) {
        this.context = context;
    }

    public NanoHTTPD.Response request(String uri, String method, Properties header,
                          Properties parms, Properties files) {
        String[] uris = uri.split("/");
        String action = "";
        String message = "";

        if (uris.length>2) {
            action = uris[2];
        }

        if (action.equals("header")) {
            message = getHeaderInfo();
        } else if (action.equals("build")) {
            message = getBuild();
        }

        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, NanoHTTPD.MIME_HTML, message);
    }

    public String getDeviceName() {
        return Build.BRAND+" "+Build.DEVICE;
    }

    public String getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = 100*level / (float)scale;
        return Float.toString(batteryPct)+"%";
    }

    public String getHeaderInfo() {
        String message = "{";
            message += "\"phoneName\":\""+getDeviceName()+"\",";
            message += "\"batteryLevel\":\""+getBatteryLevel()+"\"";
        message += "}";
        return message;
    }

    public String getBuild() {
        String Logo = Build.BRAND;
        //Logo = Logo.split("\\s+|_+|[0-9]+")[0];
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(Logo);
        if (matcher.find()) {
            Logo = matcher.group(0);
        }
        Logo = Logo.toLowerCase()+".png";

        String message = "{";
            message += "\"BOARD\":\""+Build.BOARD+"\",";
            message += "\"BOOTLOADER\":\""+Build.BOOTLOADER+"\",";
            message += "\"BRAND\":\""+Build.BRAND+"\",";
            message += "\"CPU_ABI\":\""+Build.CPU_ABI+"\",";
            message += "\"CPU_ABI2\":\""+Build.CPU_ABI2+"\",";
            message += "\"DEVICE\":\""+Build.DEVICE+"\",";
            message += "\"DISPLAY\":\""+Build.DISPLAY+"\",";
            message += "\"FINGERPRINT\":\""+Build.FINGERPRINT+"\",";
            message += "\"HARDWARE\":\""+Build.HARDWARE+"\",";
            message += "\"HOST\":\""+Build.HOST+"\",";
            message += "\"ID\":\""+Build.ID+"\",";
            message += "\"MANUFACTURER\":\""+Build.MANUFACTURER+"\",";
            message += "\"MODEL\":\""+Build.MODEL+"\",";
            message += "\"PRODUCT\":\""+Build.PRODUCT+"\",";
            message += "\"SERIAL\":\""+Build.SERIAL+"\",";
            message += "\"TAGS\":\""+Build.TAGS+"\",";
            message += "\"TYPE\":\""+Build.TYPE+"\",";
            message += "\"USER\":\""+Build.USER+"\",";
            message += "\"LOGOIMAGE\":\""+Logo+"\",";
            message += "\"VERSION\":"+getBuildVersion()+"";
        message += "}";
        return message;
    }

    public String getBuildVersion() {
        String message = "{";
            message += "\"CODENAME\":\""+Build.VERSION.CODENAME+"\",";
            message += "\"INCREMENTAL\":\""+Build.VERSION.INCREMENTAL+"\",";
            message += "\"RELEASE\":\""+Build.VERSION.RELEASE+"\"";
            //message += "\"CPU_ABI2\":\""+Build.VERSION_CODES.+"\"";
        message += "}";
        return message;
    }
}

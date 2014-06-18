package ftp27.apps.helmet;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class mainActivity extends Activity implements View.OnClickListener {
    private static final String LOG_TAG = "Class [main]";

    private TextView ServerButton;
    private TextView textIP, textPort, textPass, textConType;
    private ImageView statusCicrle;
    private Integer ServerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerButton = (TextView) findViewById(R.id.server_control);

        textIP = (TextView) findViewById(R.id.textIP);
        textPort = (TextView) findViewById(R.id.textPort);
        textPass = (TextView) findViewById(R.id.textPasskey);
        textConType = (TextView) findViewById(R.id.textConType);
        statusCicrle = (ImageView) findViewById(R.id.imageMainCircle);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateDisplays();
            }
        }, 0L, 10L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.

        //Set port
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_PORT, 8080));
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_START));
        //startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_STOP));
        ServerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.server_control:
                Log.d(LOG_TAG, "OnClick!");
                if (ServerStatus == null || ServerStatus == serverService.SERVER_STOP) {
                    Log.d(LOG_TAG, "Start Server");
                    startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_START));
                } else {
                    startService(new Intent(this, serverService.class).putExtra(serverService.PARAM_ACTION, serverService.ACTION_STOP));
                }
                updateDisplays();
                break;
        }
    }

    private void updateDisplays() {
        Intent intent = new Intent(this, serverService.class);
        startService(
                intent.putExtra(
                        serverService.PARAM_PINTENT,
                        createPendingResult(serverService.TASK_GETDATA, new Intent(this, serverService.class), 0)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == serverService.STATUS_OK) {
            String port = data.getStringExtra(serverService.PARAM_PORT);
            String ip = data.getStringExtra(serverService.PARAM_IP);
            String passkey = data.getStringExtra(serverService.PARAM_PASSKEY);
            String network = data.getStringExtra(serverService.PARAM_NETWORK);
            ServerStatus = data.getIntExtra(serverService.PARAM_STATUS, serverService.SERVER_STOP);

            if (    port    != null &&
                    ip      != null &&
                    passkey != null &&
                    network != null) {

                if (ServerStatus == serverService.SERVER_STOP) {
                    if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        textIP.setText(R.string.press);
                    } else {
                        textIP.setText((CharSequence) "----");
                    }
                    if (network.equals(serverService.NETWORK_NONE)) {
                        statusCicrle.setImageResource(R.drawable.red_button);
                    } else {
                        statusCicrle.setImageResource(R.drawable.yellow_button);
                    }
                } else {

                    textIP.setText((CharSequence) ip);
                    statusCicrle.setImageResource(R.drawable.green_button);
                }
                textConType.setText((CharSequence) network);
                textPort.setText((CharSequence) port);
                textPass.setText((CharSequence) passkey);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateDisplays();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

        private int getScreenOrientation() {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int orientation;
            // if the device's natural orientation is portrait:
            if ((rotation == Surface.ROTATION_0
                    || rotation == Surface.ROTATION_180) && height > width ||
                    (rotation == Surface.ROTATION_90
                            || rotation == Surface.ROTATION_270) && width > height) {
                switch(rotation) {
                    case Surface.ROTATION_0:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case Surface.ROTATION_90:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case Surface.ROTATION_180:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                    case Surface.ROTATION_270:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown screen orientation. Defaulting to " +
                                "portrait.");
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                }
            }
            // if the device's natural orientation is landscape or if the device
            // is square:
            else {
                switch(rotation) {
                    case Surface.ROTATION_0:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case Surface.ROTATION_90:
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case Surface.ROTATION_180:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    case Surface.ROTATION_270:
                        orientation =
                                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown screen orientation. Defaulting to " +
                                "landscape.");
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                }
            }

            return orientation;
        }



//    protected void onStart() {}
//    protected void onRestart() {}
//    protected void onPause() {}

}

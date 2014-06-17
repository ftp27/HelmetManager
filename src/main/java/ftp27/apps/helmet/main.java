package ftp27.apps.helmet;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ftp27.apps.helmet.managers.auth;
import ftp27.apps.helmet.tools.dataBase;
import ftp27.apps.helmet.tools.logger;
import ftp27.apps.helmet.server.server;


public class main extends Activity implements View.OnClickListener {

    private logger Logger;
    private server Server;
    private dataBase DB;
    private auth AccessManager;

    private Button StartButton, StopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartButton = (Button) findViewById(R.id.startButton);
        StopButton = (Button) findViewById(R.id.stopButton);

        Logger = new logger( (TextView) findViewById(R.id.logView) );
        DB = new dataBase(this);
        AccessManager = new auth(Logger, DB);
        Server = new server(8080, AccessManager);

        StartButton.setOnClickListener(this);
        StopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                Server.start();
                break;
            case R.id.stopButton:
                Server.stop();
                break;
        }
    }
}

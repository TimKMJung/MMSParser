package ssoim.com.mmsparser.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ssoim.com.mmsparser.R;
import ssoim.com.mmsparser.services.ParseMMSService;


public class ParserActivity extends AppCompatActivity {

    private Button inbox;
    private TextView parseTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);

        init();

        requestSmsPermission();
    }

    private void init() {
        inbox = (Button) findViewById(R.id.parse_btn);
        parseTxt = (TextView) findViewById(R.id.parse_text);

        requestSmsPermission();

        setButton();


    }

    private void setButton() {
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParseTxt();
            }
        });

    }

    private void startParseMMSService() {
        Intent mServiceIntent = new Intent(this, ParseMMSService.class);
        this.startService(mServiceIntent);

    }

    private void setParseTxt() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String parse = ParseMMSService.parseString;

                parseTxt.setText(parse);
            }
        });

    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.READ_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        } else {
            startParseMMSService();
        }
    }

}

package se.github.closebitcon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.service.BgScannerActivity;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences preferences;
    private TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoLog.introduce();
        logText = (TextView) findViewById(R.id.log);
        logText.setMovementMethod(new ScrollingMovementMethod());
        preferences = getSharedPreferences(InitFormActivity.getPrefKey(), MODE_PRIVATE);
        Receiver receiver = new Receiver();

        if(!(preferences.getAll().containsKey("FIRST_NAME") && preferences.getAll().containsKey("LAST_NAME")))
        {
            Intent intent = new Intent(MainActivity.this, InitFormActivity.class);
            startActivity(intent);
        }

        IntentFilter filter = new IntentFilter(BgScannerActivity.LOG_ENTRY_ACTION);
        filter.addAction(BgScannerActivity.LOG_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //Debug
        Map<String, ?> prefMap = new HashMap<>(preferences.getAll());
        for(Map.Entry entry : prefMap.entrySet())
        {
            AutoLog.debug("key: "+entry.getKey() + " value: " +entry.getValue());
        }
    }

    public void changeName(View view)
    {
        startActivity(new Intent(MainActivity.this, InitFormActivity.class));
    }

    public void startScan(View view)
    {
        startService(new Intent(MainActivity.this, BgScannerActivity.class));
        //disable button after first open
        Button button = (Button) findViewById(R.id.sendButton);
        button.setClickable(false);
        button.setAlpha(.5f);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    private class Receiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            logText.append(intent.getStringExtra(BgScannerActivity.LOG_ENTRY_ACTION) + "\n");
        }
    }
}

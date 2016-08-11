package se.github.closebitcon;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Map;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.Toasters;
import se.github.closebitcon.extra.bluetooth.BluetoothMaster;

/**
 * Created by MelEnt on 2016-08-11.
 */
public class BgScannerActivity extends IntentService
{
    public static final String LOG_ACTION = "se.github.closebitcon.LOG_ACTION";
    public static final String LOG_ENTRY_ACTION = "se.github.closebitcon.LOG_ACTION";
    //BluetoothMaster bleMaster;
    BgScannerActivity()
    {
        super("BgScannerActivity");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //bleMaster = new BluetoothMaster(this);
        SharedPreferences preferences = getSharedPreferences(InitFormActivity.getPrefKey(), MODE_PRIVATE);
        for(Map.Entry entry : preferences.getAll().entrySet())
        {
            AutoLog.debug("key: "+entry.getKey() + " value: " +entry.getValue());
        }
        Intent localIntent = new Intent(LOG_ACTION).putExtra(LOG_ENTRY_ACTION, "here is a log entry!");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
        //bleMaster.enable(true);


    }
}

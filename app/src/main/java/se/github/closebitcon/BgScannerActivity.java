package se.github.closebitcon;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Map;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.bluetooth.BluetoothMaster;

/**
 * Created by MelEnt on 2016-08-11.
 */
public class BgScannerActivity extends IntentService
{
    //BluetoothMaster bleMaster;
    BgScannerActivity()
    {
        super(null);
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
        //bleMaster.enable(true);


    }
}

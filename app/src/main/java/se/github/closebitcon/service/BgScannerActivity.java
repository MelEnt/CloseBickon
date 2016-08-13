package se.github.closebitcon.service;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.github.closebitcon.InitFormActivity;
import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.CountingHashMap;
import se.github.closebitcon.service.network.NetworkHandler;

/**
 * Created by MelEnt on 2016-08-11.
 */
public class BgScannerActivity extends IntentService implements BluetoothAdapter.LeScanCallback
{
    public static final String LOG_ACTION = "se.github.closebitcon.LOG_ACTION";
    public static final String LOG_ENTRY_ACTION = "se.github.closebitcon.LOG_ENTRY_ACTION";
    private static final int REQUIRED_SCORE = 600;
    private final CountingHashMap<BluetoothDevice> foundDevices = new CountingHashMap<>();
    private final CountingHashMap<BluetoothDevice> deviceScores = new CountingHashMap<>();
    private static final Integer TIMEOUT_SECONDS = 5;
    private BluetoothAdapter bluetoothAdapter;
    private Set<String> ownedBeacons = new HashSet<>();
    private Set<String> rejectedBeacons = new HashSet<>();
    private BluetoothDevice activeBeacon;
    private NetworkHandler network;
    private Intent intent;

    BgScannerActivity()
    {
        super("BgScannerActivity");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intent = new Intent(LOG_ACTION);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences preferences = getSharedPreferences(InitFormActivity.getPrefKey(), MODE_PRIVATE);
        String firstname = (String) preferences.getAll().get("FIRST_NAME");
        String lastname =  (String) preferences.getAll().get("LAST_NAME");
        try
        {
            network = new NetworkHandler(ownedBeacons, rejectedBeacons, firstname, lastname, "83.251.214.125", 36963);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        bluetoothAdapter.startLeScan(this);
        loopService();
    }



    private void pingDevices()
    {
        for(Map.Entry<BluetoothDevice, Integer> entry : foundDevices)
        {
            if(entry.getValue() <= 0)
            {
                BluetoothDevice device = entry.getKey();
                foundDevices.remove(device);
                deviceScores.remove(device);
                continue;
            }
            foundDevices.dec(entry.getKey());
        }
        queryActiveBeacon();
    }

    private void queryActiveBeacon()
    {
        if(activeBeacon != null)
        {
            if(foundDevices.contains(activeBeacon) == false)
            {
                try
                {
                    network.onLeaveBeacon(activeBeacon.getAddress());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                activeBeacon = null;
                deviceScores.clear();
            }
        }
    }

    private void loopService()
    {
        int count = 0;
        while(true)
        {
            count++;
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            pingDevices();
            checkScores();
            if(count % 30 == 0)
            {
                sendActiveBeacon();
            }
        }
    }

    private void sendActiveBeacon()
    {
        if(activeBeacon != null)
        {
            try
            {
                network.onActiveBeaconProximity(activeBeacon.getAddress());
                AutoLog.debug("Beacon entered proximity! " + activeBeacon.getAddress());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void checkScores()
    {
        if(activeBeacon == null)
        {
            for (Map.Entry<BluetoothDevice, Integer> entry : deviceScores)
            {
                if (entry.getValue() >= REQUIRED_SCORE)
                {
                    activeBeacon = entry.getKey();
                    try
                    {
                        network.onActiveBeaconProximity(activeBeacon.getAddress());
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy()
    {
        bluetoothAdapter.stopLeScan(this);
        super.onDestroy();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {
        String address = device.getAddress();
        if (ownedBeacons.contains(address))
        {
            foundDevices.set(device, TIMEOUT_SECONDS);      //add the device to the timeout queue check
            deviceScores.inc(device, calculateScore(rssi)); //increase the score for persistent devices in the proximity
            intent.putExtra(LOG_ENTRY_ACTION, device.getAddress());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return;
        }
        if(rejectedBeacons.contains(address) == false)
        {
            queryUnknownBeacon(address);
            return;
        }
    }

    private void queryUnknownBeacon(String beaconAddress)
    {
        try
        {
            network.infoRequest(beaconAddress);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        AutoLog.debug("Beacon address not registered (" + beaconAddress + ")");
        AutoLog.debug("Packet sent");
    }

    private int calculateScore(int rssi)
    {
        //increase score for persistent by +100 for each bluetooth scan cycle
        rssi += 100;
        if(rssi>0)
        {
            return rssi;
        }
        return 0;
    }
}

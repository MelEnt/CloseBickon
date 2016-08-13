package se.github.closebitcon.extra;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.NetworkOnMainThreadException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by EnderCrypt on 11/08/16.
 */
public abstract class CallbackWorker implements Runnable
{
	public CallbackWorker()
	{
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run()
	{
		try
		{
			callback();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public abstract void callback() throws Exception;
}

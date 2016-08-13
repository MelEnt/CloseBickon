package se.github.closebitcon.bluetooth;

import java.util.List;

/**
 * Created by EnderCrypt on 18/05/16.
 */
public interface OnConnectionsChanged
{
	public void changed(List<BluetoothConnectionInfo> devices);
}

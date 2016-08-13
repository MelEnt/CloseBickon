package se.github.closebitcon.service.network;

import android.bluetooth.BluetoothDevice;
import android.os.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.ByteUtil;
import se.github.closebitcon.extra.ContainedThread;

/**
 * Created by MelEnt on 2016-08-13.
 */
public final class NetworkHandler
{
    private Set<String> ownedBeacons;
    private Set<String> rejectedBeacons;
    private final Receiver receiver = new Receiver();
    private final String firstname;
    private final String lastname;
    private final DatagramSocket socket;
    private final InetAddress ipAddress;
    private final int port;
    private static final int API_KEY = 902_658_982;

    public NetworkHandler(Set<String> ownedBeacons, Set<String> rejectedBeacons, String firstname, String lastname, String ipAddress, int port) throws SocketException, UnknownHostException
    {
        this.ownedBeacons       = ownedBeacons;
        this.rejectedBeacons    = rejectedBeacons;
        this.firstname          = firstname;
        this.lastname           = lastname;
        this.ipAddress          = InetAddress.getByName(ipAddress);
        this.port               = port;
        socket                  = new DatagramSocket(port);
        receiver.start();
    }

    public void onLeaveBeacon(String beaconAddress) throws IOException
    {
        ByteArrayOutputStream output = newPacketStream(MessageType.LEAVE);
        ByteUtil.writeString(output, firstname);
        ByteUtil.writeString(output, lastname);
        ByteUtil.writeString(output, beaconAddress);

        sendData(output.toByteArray());
    }

    public void onActiveBeaconProximity(String beaconAddress) throws IOException
    {
        ByteArrayOutputStream output = newPacketStream(MessageType.BEACON);
        ByteUtil.writeString(output, firstname);
        ByteUtil.writeString(output, lastname);
        ByteUtil.writeString(output, beaconAddress);

        sendData(output.toByteArray());
    }

    public void infoRequest(String beaconAddress) throws IOException
    {
        ByteArrayOutputStream output = newPacketStream(MessageType.INFO_REQUEST);
        ByteUtil.writeString(output, beaconAddress);

        sendData(output.toByteArray());
    }

    private void sendData(byte[] bytes) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length, ipAddress, port);
        socket.send(packet);
    }

    private ByteArrayOutputStream newPacketStream(MessageType type) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteUtil.writeInt(output, API_KEY);
        ByteUtil.writeByte(output, (byte) type.ordinal());

        return output;
    }

    private class Receiver extends ContainedThread
    {
        @Override
        public void run() throws IOException
        {
            while(true)
            {
                byte[] bytes = new byte[1024];
                int apiKey;
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                AutoLog.debug("RECEIVED DATA!");
                InputStream stream = new ByteArrayInputStream(bytes);
                apiKey = ByteUtil.readInt(stream);
                if(apiKey != API_KEY)
                {
                    continue;
                }
                MessageType type = MessageType.get(ByteUtil.readByte(stream));

                switch (type)
                {
                    case INFO_REQUEST:
                        String beaconAddress = ByteUtil.readString(stream);
                        String beaconName    = ByteUtil.readString(stream);
                        if(beaconName == null)
                        {
                            rejectedBeacons.add(beaconAddress);
                        }
                        else
                        {
                            ownedBeacons.add(beaconAddress);
                            AutoLog.debug("detected known beacon: " + beaconName);
                        }
                        break;
                    default:
                        throw new RuntimeException("unknown network message type");
                }
            }
        }
    }
}

/**
 * This file is part of spreadit-android. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.tcp;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.eggze.spreadit.BuildConfig;
import com.eggze.spreadit.R;
import com.eggze.spreadit.common.packet.SpreaditPacketWorker;

import java.util.logging.Level;

import ch.dermitza.securenio.TCPClient;
import ch.dermitza.securenio.packet.PacketListener;
import ch.dermitza.securenio.util.logging.LoggerHandler;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ClientHandler {

    private Thread clientThread = null;
    private TCPClient client = null;
    private SpreaditPacketWorker pw = null;
    private Context appContext = null;
    private String host = null;
    private int port = -1;

    private final static String TAG = ClientHandler.class.getSimpleName();

    public ClientHandler() {
    }

    public void setParams(Context context, String host, int port) {
        this.appContext = context;
        this.host = host;
        this.port = port;
    }

    public TCPClient getClient() {
        return this.client;
    }

    private void createClient(TCPStatusListener tcpStatusListener, PacketListener packetListener) {
        Log.e(TAG, "createClient(): Creating client");
        if (pw == null) { // need a new packetworker
            Log.e(TAG, "createClient(): packetWorker null");
            pw = new SpreaditPacketWorker();
        }
        client = new TCPClient(host, port, pw, true, true);
        Log.e(TAG, client.toString());
        int bks_version;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            client.setupSSL(appContext, BuildConfig.truststorePass.toCharArray(), null);
        } else {
            client.setupSSL(appContext, BuildConfig.truststoreV1Pass.toCharArray(), null);
        }
        if (tcpStatusListener != null) { // If we have a listener reference, set it to the newly created client
            Log.e(TAG, "createClient(): registering TCPStatusListener");
            client.registerStatusListener(tcpStatusListener);
        }
        if (packetListener != null) { // If we have a listener reference, set it to the newly created client
            Log.e(TAG, "createClient(): registering PacketListener");
            client.addListener(packetListener);
        }
        pw.setSender(client);
        LoggerHandler.setLevel(Level.CONFIG);
    }

    private void destroyClient(TCPStatusListener tcpStatusListener, PacketListener packetListener) {
        Log.e(TAG, "destroyClient(): Destroying client");
        if (client != null) {
            if (tcpStatusListener != null) {
                Log.e(TAG, "destroyClient(): Unregistering TCPStatusListener");
                client.unregisterStatusListener(tcpStatusListener);
            }
            if (packetListener != null) {
                Log.e(TAG, "destroyClient(): Removing PacketListener");
                client.removeListener(packetListener);
            }
            Log.e(TAG, "destroyClient(): Stopping client from running");
            if(client.isRunning()) {
                // client.setRunning() throws the following NullPointerException:
                //
                // java.lang.NullPointerException: Attempt to invoke virtual method
                // 'java.nio.channels.Selector java.nio.channels.Selector.wakeup()'
                // on a null object reference
                // AbstractSelector.closeSocket()
                //
                // when the isRunning() check is not performed.
                // TODO reason unknown, it could be the selector should be made transient
                // TODO or the socket we are trying to close needs additional error checks
                client.setRunning(false);
            }
            clientThread.setContextClassLoader(null);
            int retries=0;
            final int TIMEOUT = 10; // ms
            final int MAX_RETRIES = 10; // 20 retries of 10ms each, 200ms total
            while (clientThread.isAlive() && (retries < MAX_RETRIES)) {
                // wait for the thread to die gracefully, using a timeout in case it
                // does not want to.
                // TODO "it does not want to" indicates someone still keeps a reference
                try{
                    Thread.sleep(TIMEOUT);
                } catch(InterruptedException ie){
                }
                retries++;
            }
            // Nullify instances and restart the whole process
            Log.e(TAG, "destroyClient(): Nullifying instances");
            pw = null;
            client = null;
            clientThread = null;
        }
    }

    public void connect(TCPStatusListener tcpStatusListener, PacketListener packetListener) {
        if (clientThread == null) {
            Log.e(TAG, "connect(): clientThread null");
            if (client == null) {
                Log.e(TAG, "connect(): client null");
                createClient(tcpStatusListener, packetListener);
            } // need a new thread
            Log.e(TAG, "connect(): Creating clientThread");
            clientThread = new Thread(client, "clientThread");
        } else {
            Log.e(TAG, "connect(): clientThread exists");
            // there is a client thread already running
            // if the client is connected, we do not need to recreate it, everything is still functioning
            if (client != null) {
                Log.e(TAG, "connect(): client exists");
                if (client.isConnected()) {
                    Log.e(TAG, "connect(): client connected");
                    return;
                } else {
                    Log.e(TAG, "connect(): client exists, not connected");
                    // TODO: Try to save and re-use the client instance
                    //  (at some point, possible SecureNIO changes needed for this to work)
                    destroyClient(tcpStatusListener, packetListener);
                    createClient(tcpStatusListener, packetListener);
                    Log.e(TAG, "connect(): Re-creating clientThread");
                    clientThread = new Thread(client, "clientThread");
                }
            } else {
                Log.e(TAG, "connect(): client does not exist");
                // Client is null but thread is running, need to recreate
                clientThread.setContextClassLoader(null);
                clientThread = null;
                createClient(tcpStatusListener, packetListener);
                Log.e(TAG, "connect(): Re-creating clientThread");
                clientThread = new Thread(client, "clientThread");
            }
        }

        // This is needed to stop the client calling the default
        // dummy classloader, see
        // http://stackoverflow.com/a/13455154
        Log.e(TAG, "connect(): Starting clientThread");
        clientThread.setContextClassLoader(appContext.getClassLoader());
        clientThread.start();
    }

    public void disconnect(TCPStatusListener tcpStatusListener, PacketListener packetListener) {
        destroyClient(tcpStatusListener, packetListener);
    }

    public boolean isConnected() {
        return (client == null) ? false : client.isConnected();
    }

}

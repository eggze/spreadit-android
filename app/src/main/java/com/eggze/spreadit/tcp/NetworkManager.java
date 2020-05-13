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
import android.util.Log;

import com.eggze.spreadit.BuildConfig;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.packet.client.user.UserHelloPacket;
import com.eggze.spreadit.common.packet.server.ServerPacket;
import com.eggze.spreadit.common.packet.server.impl.ServerErrorPacket;
import com.eggze.spreadit.tcp.requests.Request;
import com.eggze.spreadit.util.SpreaditExecutors;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.dermitza.securenio.TCPClient;
import ch.dermitza.securenio.packet.PacketIF;
import ch.dermitza.securenio.packet.PacketListener;
import ch.dermitza.securenio.socket.SocketIF;

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
@Singleton
public class NetworkManager implements TCPStatusListener, PacketListener {

    private Context context;
    private SpreaditExecutors executors;
    private ResponseHandler responseHandler;

    private ClientHandler clHandler = null;
    private ScheduledThreadPoolExecutor exec;
    private ScheduledFuture<?> tcpDiscFuture;
    private static final long DISCONNECT_TIMEOUT = 15000; // 15 secs
    private volatile boolean isBound = false;
    private final static String TAG = NetworkManager.class.getSimpleName();

    private ConcurrentHashMap<String, TCPStatusListener> tcpStatusListeners = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ResponseListener> responseListeners = new ConcurrentHashMap<>();
    private static HashMap<Short, Short> requestMap = new HashMap<>();

    // Any attempt to change status should be done within a synchronized(statusLock) call
    private final Object statusLock = new Object();
    private int status = STATUS_DISCONNECTED;

    static {
        requestMap.put((short) ServerPacket.T_USER_CREATED, (short) UserHelloPacket.T_NEW_USER);
        requestMap.put((short) ServerPacket.T_SERVER_HELLO, (short) UserHelloPacket.T_USER_HELLO);
        requestMap.put((short) ServerPacket.T_SCAN_OK, (short) UserHelloPacket.T_USER_SCAN);
        requestMap.put((short) ServerPacket.T_RESULT_OK, (short) UserHelloPacket.T_USER_RESULT);
        requestMap.put((short) ServerPacket.T_SEND_STATS, (short) UserHelloPacket.T_REQ_STATS);
        requestMap.put((short) ServerPacket.T_SEND_DATES, (short) UserHelloPacket.T_REQ_DATES);
        requestMap.put((short) ServerPacket.T_DATES_RECEIVED_OK, (short) UserHelloPacket.T_DATES_RECEIVED);
        requestMap.put((short) ServerPacket.T_SEND_RESULTS, (short) UserHelloPacket.T_REQ_RESULTS);
    }

    @Inject
    public NetworkManager(Context context, SpreaditExecutors executors, ResponseHandler responseHandler) {
        this.context = context;
        this.executors = executors;
        this.responseHandler = responseHandler;
        exec = new ScheduledThreadPoolExecutor(1);
    }

    public void connect() {
        Log.e(TAG, "connect(): Connecting");
        if (clHandler == null){ // Lazy initialization
            Log.e(TAG, "connect(): Creating clientHandler");
            clHandler = new ClientHandler();
            clHandler.setParams(context, BuildConfig.host, BuildConfig.port);
        }
        if(!clHandler.isConnected()){ // attempt a connect
            Log.e(TAG, "connect(): Calling clientHandler.connect()");
            clHandler.connect(this, this);
        }
        startDisconnectTimer();
    }

    /**
     * Called by network workers attempting to connect. If there is already a client that has a
     * STATUS_CONNECTED or STATUS_CONNECTING, nothing is done, otherwise a new client
     * connection is attempted.
     *
     * Note that if(STATUS_CONNECTING && !client.isRunning()), the previous connection attempt has
     * already failed and should be treated as a STATUS_CONN_FAILED.
     */
    public void tryConnect(){
        Log.e(TAG, "tryConnect()");
        synchronized(statusLock) {
            switch (this.status) {
                case STATUS_DISCONNECTED:
                case STATUS_CONN_FAILED:
                    Log.e(TAG, "tryConnect(): Disconnected or connection failed");
                    connect();
                    break;
                case STATUS_CONNECTED: // do nothing
                    Log.e(TAG, "tryConnect(): Connected, do nothing");
                    break;
                case STATUS_CONNECTING: // do nothing
                    if(clHandler.getClient().isRunning()){
                        Log.e(TAG, "tryConnect(): Connecting, do nothing");
                    } else{
                        synchronized (statusLock){
                            this.status = STATUS_DISCONNECTED;
                        }
                        Log.e(TAG, "tryConnect(): Previous connection attempt failed");
                        connect();
                    }
                    break;
            }
        }
    }

    private Runnable discRunnable = new Runnable() {

        @Override
        public void run() {
            // This will not stop the service
            // until it is unbound.
            if (!isBound) {
                Log.e("discRunnable", "Timer expired, calling NetworkManager.onDestroy()");
                onDestroy();
                return;
            }
        }
    };

    public TCPClient getClient() {
        return this.clHandler.getClient();
    }

    private void startDisconnectTimer() {
        Log.e(TAG, "startDisconnectTimer(): Starting disconnect timer");
        tcpDiscFuture = exec.schedule(discRunnable, DISCONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void resetDisconnectTimer() {
        Log.e(TAG, "resetDisconnectTimer(): Resetting disconnect timer");
        if (tcpDiscFuture != null) {
            Log.e(TAG, "resetDisconnectTimer(): future not null, cancelling..");
            tcpDiscFuture.cancel(true);
        }
        tcpDiscFuture = exec.schedule(discRunnable, DISCONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private void stopDisconnectTimer() {
        Log.e(TAG, "stopDisconnectTimer(): Stopping disconnect timer");
        if (tcpDiscFuture != null) {
            Log.e(TAG, "stopDisconnectTimer(): future not null, cancelling..");
            tcpDiscFuture.cancel(true);
        }
    }

    public void onResume() {
        Log.e(TAG, "onResume(): Setting bound to true, calling tryConnect()");
        isBound = true;
        resetDisconnectTimer();
        tryConnect();
    }

    public void onDestroy() {
        Log.e(TAG, "onDestroy(): Removing listeners and disconnecting clHandler");
        stopDisconnectTimer(); // stop the disconnect timer
        if (clHandler != null) {
            clHandler.disconnect(this, this);
        }
        // Set the disconnected status manually as we have (1) deregistered ourselves as listeners
        // and (2) we requested the disconnect ourselves
        synchronized (statusLock) {
            this.status = STATUS_DISCONNECTED;
        }
    }

    public void onStop() {
        Log.e(TAG, "onStop(): Setting bound to false, resetting disconnect timer");
        resetDisconnectTimer();
        isBound = false;
//        if (clHandler != null) {
//            clHandler.getClient().unregisterStatusListener(this); // TODO, this has to happen at a lower level
//            clHandler.getClient().removeListener(this); // TODO, this has to happen at a lower level
//            // Unregister listener to the client on unbind, TODO
//        }
    }

    public void registerStatusListener(TCPStatusListener statusListener) {
        if (statusListener != null) {
            tcpStatusListeners.putIfAbsent(statusListener.toString(), statusListener);
//            statusListener.statusChanged(status);
        }
    }

    public void unregisterStatusListener(TCPStatusListener statusListener) {
        if (statusListener != null) {
            tcpStatusListeners.remove(statusListener.toString());
        }
    }

    public void send(Request request) {
        Log.d(TAG, "request " + request.getClass().getSimpleName());

        Log.d(TAG, "clHandler != null " + (clHandler != null));
        Log.d(TAG, "clHandler.getClient() != null " + (clHandler.getClient() != null));
        Log.d(TAG, "clHandler.getClient().isConnected() " + (clHandler.getClient().isConnected()));

        if (isConnected()) {

            ResponseListener responseListener = request.getResponseListener();
            if (responseListener != null) {
                responseListeners.put(request.getTag(), responseListener);
            }
            Log.d(TAG, "send");

            clHandler.getClient().send(request.getRequestPacket());
        }
    }

    public boolean isConnected() {
        // On every call of isConnected(), reset the disconnect timer. The thread that just
        // called tryConnect() is looking to send more data. If the client is indeed connected,
        // we should allow it to live a little longer.
        resetDisconnectTimer();
        return clHandler != null && clHandler.getClient() != null && clHandler.getClient().isConnected();
    }

    @Override
    public void statusChanged(int status) {
        Log.e(TAG, "statusChanged(): OLD " + STATUS_NAMES[this.status] + " NEW " + STATUS_NAMES[status]);
        if (this.status != status) {
            synchronized (statusLock) {
                this.status = status;
            }
            for (TCPStatusListener tcpStatusListener : tcpStatusListeners.values()) {
                tcpStatusListener.statusChanged(status);
            }
            if (status == TCPStatusListener.STATUS_DISCONNECTED && !isBound) {
                tcpStatusListeners.clear();
            }
        }
    }

    @Override
    public void paketArrived(final SocketIF socket, final PacketIF packet) {
        Log.e(TAG, "packetArrived()");
        if (packet.getHeader() == SpreaditPacket.TYPE_SERVER) {
            Log.e(TAG, "packetArrived(): Server packet");
            ServerPacket sp = (ServerPacket) packet;
            String key = String.valueOf(requestMap.get((short) sp.getServerPacketType())) + sp.getPacketIndex();
            ResponseListener responseListener = responseListeners.get(key);
            if (responseListener == null) {
                Log.e(TAG, "packetArrived(): responseListener null");
                // Try error
                try {
                    key = String.valueOf(requestMap.get(((ServerErrorPacket) sp).getClientPacketType())) + sp.getPacketIndex();
                    responseListener = responseListeners.get(key);

                } catch (ClassCastException cce) {
                }
            }
            switch (sp.getServerPacketType()) {
                case ServerPacket.T_SERVER_ERROR:
                case ServerPacket.T_SERVER_INTERNAL_ERROR: {
                    Log.e(TAG, "packetArrived(): responseHandler handleError()");
                    responseHandler.handleError((ServerErrorPacket) sp, responseListener);
                    break;
                }
                default: {
                    Log.e(TAG, "packetArrived(): responseHandler handleSuccess()");
                    responseHandler.handleSuccess(sp, responseListener);
                    break;
                }
            }
            if (responseListener != null) {
                responseListeners.remove(key, responseListener);
            }
        }
    }
}
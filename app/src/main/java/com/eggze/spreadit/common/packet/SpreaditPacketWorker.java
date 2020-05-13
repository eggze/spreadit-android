/**
 *  This file is part of spreadit-backend. Copyright (C) 2020 eggze Technik GmbH
 *
 * spreadit-backend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * spreadit-backend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.eggze.spreadit.common.packet;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.packet.worker.VariableLengthPacketWorker;
import ch.dermitza.securenio.socket.SocketIF;
import ch.dermitza.securenio.util.logging.LoggerHandler;

import com.eggze.spreadit.common.packet.server.ServerPacketIF;
import com.eggze.spreadit.common.packet.server.scans.ScanOKPacket;
import com.eggze.spreadit.common.packet.server.scans.SendDatesPacket;
import com.eggze.spreadit.common.packet.server.stats.SendStatsPacket;
import com.eggze.spreadit.common.packet.server.impl.ServerErrorPacket;
import com.eggze.spreadit.common.packet.server.user.ServerHelloPacket;
import com.eggze.spreadit.common.packet.server.impl.ServerInfoPacket;
import com.eggze.spreadit.common.packet.server.results.ResultOKPacket;
import com.eggze.spreadit.common.packet.server.user.UserCreatedPacket;
import com.eggze.spreadit.common.packet.client.user.NewUserPacket;
import com.eggze.spreadit.common.packet.client.scans.UserScanPacket;
import com.eggze.spreadit.common.packet.client.scans.ReqDatesPacket;
import com.eggze.spreadit.common.packet.client.stats.ReqStatsPacket;
import com.eggze.spreadit.common.packet.client.user.UserHelloPacket;
import com.eggze.spreadit.common.packet.client.results.UserResultPacket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.eggze.spreadit.common.packet.client.ClientPacketIF;
import com.eggze.spreadit.common.packet.client.results.ReqResultsPacket;
import com.eggze.spreadit.common.packet.client.scans.DatesReceivedPacket;
import com.eggze.spreadit.common.packet.server.results.SendResultsPacket;
import com.eggze.spreadit.common.packet.server.scans.DatesReceivedOKPacket;

/**
 *
 * A custom-built implementation of a SecureNIO VariableLengthPacketWorker
 * for the spreadit system.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class SpreaditPacketWorker extends VariableLengthPacketWorker {

    private static final Logger LOGGER = LoggerHandler
            .getLogger(SpreaditPacketWorker.class.getName());

    private SenderIF sender;
    private String packetLog;

    /**
     * Set the sender of this packet worker. Each packet worker has one sender
     * reference that is added to each packet to ensure a reply on that packet
     * if necessary.
     * 
     * @param sender the sender of this packet worker
     */
    public void setSender(SenderIF sender) {
        this.sender = sender;
        packetLog = "";
    }

    @Override
    protected void assemblePacket(SocketIF socket, short head, byte[] data) {
        packetLog = "RCV:";
        SpreaditPacket p;
        // Switch the head in order of expected frequency of requests
        // Depending where this is implemented. Different order is needed for
        // different implementation locations.
        switch (head) {
            case SpreaditPacket.TYPE_CLIENT:
                packetLog += "CLIENT:";
                // NOTE: This should never be called from the client
                // as the client SHOULD NOT be receiving client packets
                switch (data[0]) {
                    case ClientPacketIF.T_USER_HELLO:
                        packetLog += "USER_HELLO";
                        p = new UserHelloPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_USER_SCAN:
                        packetLog += "USER_SCAN";
                        p = new UserScanPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_NEW_USER:
                        packetLog += "NEW_USER";
                        p = new NewUserPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_REQ_DATES:
                        packetLog += "REQ_DATES";
                        p = new ReqDatesPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_DATES_RECEIVED:
                        packetLog += "DATES_RECEIVED";
                        p = new DatesReceivedPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_REQ_RESULTS:
                        packetLog += "REQ_RESULTS";
                        p = new ReqResultsPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_USER_RESULT:
                        packetLog += "USER_RESULT";
                        p = new UserResultPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ClientPacketIF.T_REQ_STATS:
                        packetLog += "REQ_STATS";
                        p = new ReqStatsPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    default:
                        // Invalid type, log and disconnect TODO
                        LOGGER.log(Level.WARNING, "Invalid header received: {0}",
                                head);
                        //securityManager.addPacket((ServerPacket)packet);
                        packetLog += "INVALID, head: " + head;
                        break;
                }
                break;
            case SpreaditPacket.TYPE_SERVER:
                packetLog += "SERVER:";
                // NOTE: This should never be called from the server
                // as the server generally SHOULD NOT be receiving server
                // packets (soon to be changed)
                // SORT BY FREQUENCY
                switch (data[0]) {
                    case ServerPacketIF.T_SERVER_HELLO:
                        packetLog += "SERVER_HELLO";
                        p = new ServerHelloPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SCAN_OK:
                        packetLog += "SCAN_OK";
                        p = new ScanOKPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_USER_CREATED:
                        packetLog += "USER_CREATED";
                        p = new UserCreatedPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SEND_DATES:
                        packetLog += "SEND_DATES";
                        p = new SendDatesPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_DATES_RECEIVED_OK:
                        packetLog += "DATES_RECEIVED_OK";
                        p = new DatesReceivedOKPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SEND_RESULTS:
                        packetLog += "SEND_RESULTS";
                        p = new SendResultsPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SERVER_ERROR:
                        packetLog += "SERVER_ERROR";
                        p = new ServerErrorPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_RESULT_OK:
                        packetLog += "RESULT_OK";
                        p = new ResultOKPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SEND_STATS:
                        packetLog += "SEND_STATS";
                        p = new SendStatsPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    case ServerPacketIF.T_SERVER_INFO:
                        packetLog += "SERVER_INFO";
                        p = new ServerInfoPacket(sender, socket);
                        p.reconstruct(ByteBuffer.wrap(data));
                        fireListeners(socket, p);
                        break;
                    default:
                        // Invalid type, log and disconnect TODO
                        LOGGER.log(Level.WARNING, "Invalid header received: {0}",
                                head);
                        //securityManager.addPacket((ServerPacket)packet);
                        packetLog += "INVALID, head: " + head;
                        break;
                }
                break;
            default:
                // Invalid type, log and disconnect TODO
                LOGGER.log(Level.WARNING, "Invalid header received: {0}",
                        head);
                //securityManager.addPacket((ServerPacket)packet);
                packetLog += "INVALID, head: " + head;
                break;
        }
        LOGGER.config(packetLog);
    }
}

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
import ch.dermitza.securenio.socket.SocketIF;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 * @author K. Dermitzakis <kde at eggze.com>
 * 
 * @version 0.1
 * @since 0.1
 */
public abstract class SpreaditPacket implements SpreaditPacketIF {

    protected short packetIndex = Short.MAX_VALUE; // MAX VALUE IS ERROR
    //protected byte protocolVersion = Byte.MAX_VALUE; // MAX VALUE IS INVALID
    protected byte protocolVersion = PROTOCOL_VERSION; // TODO check

    private final SocketIF socket; // The local socket reference
    private final SenderIF sender; // The local sender reference
    private final String ip; // The local socket IP reference

    public SpreaditPacket(SenderIF sender, SocketIF socket) {
        this.sender = sender;
        this.socket = socket;
        if (socket != null) {
            SocketChannel sc = socket.getSocket();
            if (sc != null) {
                InetAddress addr = sc.socket().getInetAddress();
                if (addr != null) {
                    // TODO CHECK IF GUAVA METHODS ARE FASTER/BETTER
                    ip = addr.getHostAddress();
                } else {
                    this.ip = "0.0.0.0";
                }
            } else {
                this.ip = "0.0.0.0";
            }
        } else {
            this.ip = "0.0.0.0";
        }
    }

    public SenderIF getSender() {
        return this.sender;
    }

    public SocketIF getSocket() {
        return this.socket;
    }

    public String getIP() {
        return this.ip;
    }

    @Override
    public short getHeader() {
        return TYPE_INVALID;
    }

    @Override
    public void setPacketIndex(short packetIndex) {
        this.packetIndex = packetIndex;
    }

    @Override
    public short getPacketIndex() {
        return this.packetIndex;
    }

    @Override
    public byte getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Currently only being used for unit testing of the packets. Candidate to
     * be deprecated
     * 
     * @param source The source ByteBuffer to be reconstructed
     * @param isContained Whether or not this is an envelope packet
     */
    public void reconstructPadded(ByteBuffer source, boolean isContained) {
        byte[] src = source.array();
        int offset = 3;

        // If we have a container packet consisting of smaller packets
        // TODO offset is most probably wrong, to be checked, not a priority
        if (isContained) {
            offset = offset--;
        }
        //if(isContained){
        //    offset = 2;
        //}else{
        //    offset = 3;
        //}
        byte[] dest = new byte[src.length - offset];
        System.arraycopy(src, offset, dest, 0, dest.length);
        reconstruct(ByteBuffer.wrap(dest));
    }
}

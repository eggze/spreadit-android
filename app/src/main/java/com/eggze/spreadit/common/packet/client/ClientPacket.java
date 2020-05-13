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
package com.eggze.spreadit.common.packet.client;

import ch.dermitza.securenio.SenderIF;
import ch.dermitza.securenio.socket.SocketIF;
import com.eggze.spreadit.common.packet.SpreaditPacket;
import com.eggze.spreadit.common.util.UUIDUtil;
import java.util.UUID;

/**
 * The base ClientPacket for the spreadit implementation. All client packets
 * are a subclass of this.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public abstract class ClientPacket extends SpreaditPacket implements ClientPacketIF {

    // Local reference of the UserUUID associate with this packet
    protected byte[] userUUID;

    public ClientPacket(SenderIF sender, SocketIF socket) {
        super(sender, socket);
    }

    @Override
    public final short getHeader() {
        return SpreaditPacket.TYPE_CLIENT;
    }

    @Override
    public byte getClientPacketType() {
        return ClientPacketIF.T_INVALID;
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        System.arraycopy(UUIDUtil.toBytes(userUUID), 0, this.userUUID, 0, 16);
    }

    @Override
    public void setUserUUID(byte[] uuid) {
        System.arraycopy(uuid, 0, this.userUUID, 0, 16);
    }

    @Override
    public byte[] getUserUUIDBytes() {
        return this.userUUID;
    }

    @Override
    public UUID getUserUUID() {
        return UUIDUtil.toUUID(this.userUUID);
    }

}

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
package com.eggze.spreadit.common.packet.client.user;

import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 * An interface describing the client packet where a client periodically 
 * communicates with the UserManager. At each such communication, if a user
 * has contacts in any of the scans they have sent, this will be indicated in
 * the ServerHelloPacket sent as a response.
 * 
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface UserHelloPacketIF extends ClientPacketIF{
    
}

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
package com.eggze.spreadit.common.packet.server.scans;

import com.eggze.spreadit.common.packet.server.ServerPacketIF;

/**
 * An interface describing the server packet where the ScanManager acknowledges
 * the reception of scans with dates by the client. Upon sending of this packet,
 * these scans are guaranteed to be marked as sent in the database.
 * 
 * Failure to receive this packet from the ScanManager has no effect on the
 * client.
 * 
 * This is the last step in the following packet chain
 * 
 * Client                                        Server
 * ----------------------------------------------------
 * ReqDatesPacket          ->
 *                         <-           SendDatesPacket
 * DatesReceivedPacket     ->
 *                         <-     DatesReceivedOKPacket
 * 
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface DatesReceivedOKPacketIF extends ServerPacketIF{
}

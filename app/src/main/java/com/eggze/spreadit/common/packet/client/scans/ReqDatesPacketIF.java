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
package com.eggze.spreadit.common.packet.client.scans;

import com.eggze.spreadit.common.packet.client.ClientPacketIF;

/**
 * An interface describing the client packet where a user requests dates of
 * scans that have contacts. This packet is only requested by the client in
 * the event the client sees they have contacts in a ClientHello packet.
 * 
 * Failure to receive a response also triggers a periodic client task to request
 * scans again at a later time.
 * 
 * This is the first step in the following packet chain
 * 
 * Client                                        Server
 * ----------------------------------------------------
 * ReqDatesPacket          ->
 *                         <-           SendDatesPacket
 * DatesReceivedPacket     ->
 *                         <-     DatesReceivedOKPacket
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public interface ReqDatesPacketIF extends ClientPacketIF{
}

/**
 * This file is part of SecureNIO. Copyright (C) 2014 K. Dermitzakis
 * <dermitza@gmail.com>
 *
 * SecureNIO is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * SecureNIO is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with SecureNIO. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dermitza.securenio;

import ch.dermitza.securenio.packet.PacketIF;
import ch.dermitza.securenio.socket.SocketIF;

/**
 * TODO
 *
 * @author K. Dermitzakis
 * @version 0.20
 * @since 0.20
 */
public interface SenderIF {
    
    public void send(SocketIF socket, PacketIF packet);
    public boolean isRunning();
}

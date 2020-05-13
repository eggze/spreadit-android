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

/**
 *
 * @author D. Kalyvas <dimitris at kalyvasd.eu>
 * @version 0.1
 * @since 0.1
 */
public interface TCPStatusListener {

	public static final int STATUS_DISCONNECTED = 0;
	public static final int STATUS_CONNECTING   = 1;
	public static final int STATUS_CONNECTED    = 2;
	public static final int STATUS_CONN_FAILED  = 3;

	public static final String[] STATUS_NAMES = {
			"STATUS_DISCONNECTED",
			"STATUS_CONNECTING",
			"STATUS_CONNECTED",
			"STATUS_CONN_FAILED"};
	
	public void statusChanged(int status);

}

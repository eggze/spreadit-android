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
package com.eggze.spreadit.common.util;


//import com.google.common.net.InetAddresses;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Network helper static functions.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class NetUtil  {
    
    public static byte[] getIP(String ip) throws NullPointerException, UnknownHostException{
        
        if(ip == null){
            throw new NullPointerException("Host provided is null");
        }
        try{
            byte[] b = InetAddress.getByName(ip).getAddress();
            return b;
        }catch(UnknownHostException uhe){
            UnknownHostException e = new UnknownHostException("Unknown host provided");
            // TODO, workaround addSuppressed requiring API 19. Possibly exclude
            e.addSuppressed(uhe);
            throw e;
        }
    }
    
    public static String getIP(byte[] ip) {
        //String s = InetAddress.getByAddress(ip).toString();
        //InetAddress a = InetAddress.getByName("2001:0DB8:AC10:FE01:0000:0000:0000:0000");
        //byte[] bytes = a.getAddress();
        return null;
    }

}

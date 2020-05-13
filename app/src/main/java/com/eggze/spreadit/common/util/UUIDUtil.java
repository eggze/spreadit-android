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

import java.util.UUID;

/**
 * UUID conversion helper static functions.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class UUIDUtil {

    /**
     * Helper function to convert a byte array to a (Version 4) UUID.
     * 
     * @param bytes The byte array to convert
     * @return The converted UUID
     */
    public static UUID toUUID(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException(
                    "UUID length mismatch - length should be 16 bytes");
        }
        int i = 0;
        long msl = 0;
        for (; i < 8; i++) {
            msl = (msl << 8) | (bytes[i] & 0xFF);
        }
        long lsl = 0;
        for (; i < 16; i++) {
            lsl = (lsl << 8) | (bytes[i] & 0xFF);
        }
        return new UUID(msl, lsl);
    }
    
    /**
     * Helper function to convert a (Version 4) UUID to a byte array.
     * 
     * @param uuid The UUID to convert
     * @return The converted byte array
     */
    public static byte[] toBytes(UUID uuid){
        byte[] ret = new byte[16];
        long msl = uuid.getMostSignificantBits();
        long lsl = uuid.getLeastSignificantBits();
        
        for(int i=0; i < 16; i++){
            if(i < 8){
                ret[i] = (byte)((msl >> 8 * (7 - i)) & 0xFF);
            }else{
                ret[i] = (byte)((lsl >> 8 * (7 - i)) & 0xFF);
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        UUID test = UUID.randomUUID();
        System.out.println("Original: " + test.toString());
        System.out.println("Reconstructed: " + UUIDUtil.toUUID(UUIDUtil.toBytes(test)));
        System.out.println("Equal: " + (test.compareTo(UUIDUtil.toUUID(UUIDUtil.toBytes(test)))==0));
    }
}

/*
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

import java.nio.ByteBuffer;

/**
 * Byte conversion helper static functions.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ByteUtil {
    
    public static byte[] longToBytes(long x) {
        ByteBuffer b = ByteBuffer.allocate(Long.BYTES);
        b.putLong(0, x);
        return b.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer b = ByteBuffer.allocate(Long.BYTES);
        b.put(bytes, 0, bytes.length);
        b.flip();//need flip 
        return b.getLong();
    }

    public static byte[] longToBytesBitwise(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLongBitwise(final byte[] bytes, final int offset) {
        long result = 0;
        for (int i = offset; i < Long.BYTES + offset; i++) {
            result <<= Long.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static long bytesToLongUnrolled(byte[] bytes) {
        long l = ((long) bytes[7] << 56)
                | ((long) bytes[6] & 0xff) << 48
                | ((long) bytes[5] & 0xff) << 40
                | ((long) bytes[4] & 0xff) << 32
                | ((long) bytes[3] & 0xff) << 24
                | ((long) bytes[2] & 0xff) << 16
                | ((long) bytes[1] & 0xff) << 8
                | ((long) bytes[0] & 0xff);

        return l;
    }

    public static byte[] longToBytesUnrolled(long lng) {
        byte[] b = new byte[]{
            (byte) lng,
            (byte) (lng >> 8),
            (byte) (lng >> 16),
            (byte) (lng >> 24),
            (byte) (lng >> 32),
            (byte) (lng >> 40),
            (byte) (lng >> 48),
            (byte) (lng >> 56)};
        return b;
    }
    
}

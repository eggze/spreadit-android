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
package com.eggze.spreadit.common.util.scan;

import android.util.Base64; // Android

import java.nio.charset.Charset;
import java.util.Arrays;
//import java.util.Base64; // Standard Java
import java.util.List;
import java.util.UUID;

/**
 * The codec that converts and verifies Base64 encoded Location-QRs and Test-QRs
 * and their representations in the spreadit system.
 * 
 * @author K. Dermitzakis <kde at eggze.com>
 * @version 0.1
 * @since 0.1
 */
public class ClientScanCodec {

    /**
     * Return a LocationScan from a Base64 encoded string if the string is
     * formatted correctly, null otherwise.
     * 
     * @param input The Base64 encoded string to use
     * @return a LocationScan from the given Base64 encoded string if the string
     * is formatted correctly, null otherwise
     */
    public static LocationScan locationScanFromBase64(String input) {
        String decoded = ClientScanCodec.fromBase64(input);
        return locationScanFromString(decoded);
    }

    /**
     * Return a LocationScan from a string if the string is formatted correctly,
     * null otherwise.
     * 
     * @param locationScanStr The string to use
     * @return a LocationScan from the given string if the string is formatted
     * correctly, null otherwise
     */
    public static LocationScan locationScanFromString(String locationScanStr) {
        LocationScan res = null;
        List<String> contents = Arrays.asList(locationScanStr.split(","));
        if (contents.size() == 3) {
            try {
                UUID uuid = UUID.fromString(contents.get(0));
                float lat = Float.valueOf(contents.get(1));
                float lon = Float.valueOf(contents.get(2));
                res = new LocationScan(uuid, lat, lon);
            } catch (IllegalArgumentException iae) {
                // also catches NumberFormatException
                res = null;
            }
        }
        return res;
    }

    /**
     * Convert a LocationScan to a Base64 encoded string.
     * 
     * @param locationScan The LocationScan to be converted
     * @return The Base64 encoded string corresponding to the given LocationScan
     */
    public static String locationScanToBase64(LocationScan locationScan) {
        return ClientScanCodec.toBase64(
                ClientScanCodec.locationScanToString(locationScan));
    }

    /**
     * Convert a LocationScan to a string.
     * 
     * @param locationScan The LocationScan to be converted
     * @return The string corresponding to the given LocationScan
     */
    public static String locationScanToString(LocationScan locationScan) {
        if (locationScan.getScanUUID() == null) {
            throw new IllegalArgumentException(
                    "Cannot encode a LocationScan with no scanUUID");
        }
        return locationScan.getScanUUID().toString()
                + "," + locationScan.getLat()
                + "," + locationScan.getLon();
    }

    /**
     * Return a ResultScan from a Base64 encoded string if the string is
     * formatted correctly, null otherwise.
     * 
     * @param input The Base64 encoded string to use
     * @return a ResultScan from the given Base64 encoded string if the string
     * is formatted correctly, null otherwise
     */
    public static ResultScan resultScanFromBase64(String input) {
        String decoded = ClientScanCodec.fromBase64(input);
        return resultScanFromString(decoded);
    }

    /**
     * Return a ResultScan from a string if the string is formatted correctly,
     * null otherwise.
     * 
     * @param resultScanStr The string to use
     * @return a ResultScan from the given string if the string is formatted
     * correctly, null otherwise
     */
    public static ResultScan resultScanFromString(String resultScanStr) {
        ResultScan res = null;
        List<String> contents = Arrays.asList(resultScanStr.split(","));
        if (contents.size() == 2) {
            try {
                UUID uuid = UUID.fromString(contents.get(0));
                int scanSerial = Integer.valueOf(contents.get(1));
                res = new ResultScan(uuid, scanSerial);
            } catch (IllegalArgumentException iae) {
                // also catches NumberFormatException
                res = null;
            }
        }
        return res;
    }

    /**
     * Convert a ResultScan to a Base64 encoded string.
     * 
     * @param resultScan The ResultScan to be converted
     * @return The Base64 encoded string corresponding to the given ResultScan
     */
    public static String resultScanToBase64(ResultScan resultScan) {
        return toBase64(resultScanToString(resultScan));
    }

    /**
     * Convert a ResultScan to a string.
     * 
     * @param resultScan The ResultScan to be converted
     * @return The string corresponding to the given ResultScan
     */
    public static String resultScanToString(ResultScan resultScan) {
        if (resultScan.getScanUUID() == null) {
            throw new IllegalArgumentException(
                    "Cannot encode a ResultScan with no scanUUID");
        }
        return resultScan.getScanUUID().toString()
                + "," + resultScan.getScanSerial();
    }

    /**
     * Convert a string to its Base64 encoded equivalent. The given string is
     * first formatted to UTF-8.
     * 
     * @param input The string to convert
     * @return The Base64 encoded string
     */
    public static String toBase64(String input) {
        //System.out.println("Original: " + input);
        String temp = String.format(input, Charset.forName("UTF-8"));
        //System.out.println("Formatted: " + temp);
        //temp = Base64.getEncoder().encodeToString(temp.getBytes()); // Standard Java
        temp = Base64.encodeToString(temp.getBytes(), Base64.DEFAULT); // Android
        //System.out.println("Encoded: " + temp);
        return temp;
    }

    /**
     * Decode a Base64 encoded string. The decoded string is first formatted to
     * UTF-8 before being returned.
     * 
     * @param input The Base64 encoded string
     * @return The decoded string, UTF-8 formatted
     */
    public static String fromBase64(String input) {
        //System.out.println("Original: " + input);
        //byte[] tmp = Base64.getDecoder().decode(input); // Standard Java
        byte[] tmp = Base64.decode(input, Base64.DEFAULT); // Android
        String tmp2 = new String(tmp);
        //System.out.println("Decoded: " + tmp2);
        tmp2 = String.format(tmp2, Charset.forName("UTF-8"));
        //System.out.println("Formatted: " + tmp2);
        return tmp2;
    }

    public static void main(String[] args) {
        // Format: UUID,Lat,Lon - lat/lon is xx.xxxxxx
        // Formatted with UTF8 beforehand (does it matter?)
        // Encoded with https://zxing.appspot.com/generator
        // ISO-8859-1
        UUID locUUID = UUID.randomUUID();
        String locUUIDStr = locUUID.toString();
        float lat = 12.345678f;
        float lon = 87.654321f;
        
        LocationScan locOriginal = new LocationScan(UUID.fromString(locUUIDStr), lat, lon);
        System.out.println(
                "Original: Location Scan scanUUID " + locOriginal.getScanUUID()
                + " lat " + locOriginal.getLat()
                + " lon " + locOriginal.getLon());
        
        String locEncoded = locationScanToBase64(locOriginal);
        System.out.println("Encoded: Location Scan " + locEncoded);
        
        LocationScan locDecoded = ClientScanCodec.locationScanFromBase64(locEncoded);
        if (locDecoded == null) {
            System.out.println("Null Parse");
        } else {
            System.out.println(
                    "Decoded: Location Scan scanUUID " + locDecoded.getScanUUID()
                    + " lat " + locDecoded.getLat()
                    + " lon " + locDecoded.getLon());
        }

        UUID resUUID = UUID.randomUUID();
        String resUUIDstr = resUUID.toString();
        int scanSerial = 12345;

        ResultScan resOriginal = new ResultScan(UUID.fromString(resUUIDstr), scanSerial);
        
        System.out.println(
                "Original: Result Scan scanUUID " + resOriginal.getScanUUID()
                + " scanSerial " + resOriginal.getScanSerial());
        
        String resEncoded = resultScanToBase64(resOriginal);
        System.out.println("Encoded: Result Scan " + resEncoded);
        
        ResultScan resDecoded = ClientScanCodec.resultScanFromBase64(resultScanToBase64(resOriginal));
        if (resDecoded == null) {
            System.out.println("Null Parse");
        } else {
            System.out.println(
                    "Decoded: Result Scan scanUUID " + resDecoded.getScanUUID()
                    + " scanSerial " + resDecoded.getScanSerial());
        }
    }
}

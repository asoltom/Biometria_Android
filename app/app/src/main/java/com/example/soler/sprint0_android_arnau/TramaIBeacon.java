
package com.example.soler.sprint0_android_arnau;

import java.util.Arrays;

// -------------------------------------------------------------------------------------------------
// @author: Arnau Soler Tomás
// Clase: TramaIBeacon
// Descripción: Clase de la TramaIBeacon con la que podremos crear objetos que almacenen información
// de los beacons BTLE que detectamos en el Servicio
// -------------------------------------------------------------------------------------------------
public class TramaIBeacon {
    private final byte[] prefijo; // 9 bytes
    private final byte[] uuid; // 16 bytes
    private final byte[] major; // 2 bytes
    private final byte[] minor; // 2 bytes
    private final byte txPower; // 1 byte

    private final byte[] losBytes;

    private final byte[] advFlags; // 3 bytes
    private final byte[] advHeader; // 2 bytes
    private final byte[] companyID; // 2 bytes
    private final byte iBeaconType; // 1 byte
    private final byte iBeaconLength; // 1 byte

    // -------------------------------------------------------------------------------
    // --> getPrefijo() --> <byte>
    // Descripción: Método que devuelve el prefijo de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getPrefijo() {
        return prefijo;
    }

    // -------------------------------------------------------------------------------
    // --> getUUID() --> <byte>
    // Descripción: Método que devuelve el UUID de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getUUID() {
        return uuid;
    }

    // -------------------------------------------------------------------------------
    // --> getMajor() --> <byte>
    // Descripción: Método que devuelve el valor 'Major' (el valor máximo) de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getMajor() {
        return major;
    }

    // -------------------------------------------------------------------------------
    // --> getMinor() --> <byte>
    // Descripción: Método que devuelve el valor 'Minor' (el valor mínimo) de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getMinor() {
        return minor;
    }

    // -------------------------------------------------------------------------------
    // --> getTxPower() --> byte
    // Descripción: Método que devuelve el valor de la potencia de transmisión (TxPower) de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte getTxPower() {
        return txPower;
    }

    // -------------------------------------------------------------------------------
    // --> getAdvFlags() --> <byte>
    // Descripción: Método que devuelve el valor 'advFlags' de la trama iBeacon. Esto significa
    // que devuelve los Flags de advertencia para poder contactar con el dispositivo Arduino
    // -------------------------------------------------------------------------------
    public byte[] getAdvFlags() {
        return advFlags;
    }

    // -------------------------------------------------------------------------------
    // --> getAdvHeader() --> <byte>
    // Descripción: Método que devuelve la cabecera de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getAdvHeader() {
        return advHeader;
    }

    // -------------------------------------------------------------------------------
    // --> getCompanyID() --> <byte>
    // Descripción: Método que devuelve la ID de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte[] getCompanyID() {
        return companyID;
    }

    // -------------------------------------------------------------------------------
    // --> getiBeaconType() --> byte
    // Descripción: Método que devuelve el tipo de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte getiBeaconType() {
        return iBeaconType;
    }

    // -------------------------------------------------------------------------------
    // --> getiBeaconLength() --> byte
    // Descripción: Método que devuelve el tamaño de la trama iBeacon
    // -------------------------------------------------------------------------------
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    // -------------------------------------------------------------------------------
    // <byte> --> TramaIBeacon()
    // Descripción: Constructor de la clase TramaIBeacon que permite crear una tramaIBeacon
    // -------------------------------------------------------------------------------
    public TramaIBeacon(byte[] bytes ) {
        this.losBytes = bytes;

        prefijo = Arrays.copyOfRange(losBytes, 0, 8+1 ); // 9 bytes
        uuid = Arrays.copyOfRange(losBytes, 9, 24+1 ); // 16 bytes
        major = Arrays.copyOfRange(losBytes, 25, 26+1 ); // 2 bytes
        minor = Arrays.copyOfRange(losBytes, 27, 28+1 ); // 2 bytes
        txPower = losBytes[ 29 ]; // 1 byte

        advFlags = Arrays.copyOfRange( prefijo, 0, 2+1 ); // 3 bytes
        advHeader = Arrays.copyOfRange( prefijo, 3, 4+1 ); // 2 bytes
        companyID = Arrays.copyOfRange( prefijo, 5, 6+1 ); // 2 bytes
        iBeaconType = prefijo[ 7 ]; // 1 byte
        iBeaconLength = prefijo[ 8 ]; // 1 byte

    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------



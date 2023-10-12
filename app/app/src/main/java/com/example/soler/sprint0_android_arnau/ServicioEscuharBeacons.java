package com.example.soler.sprint0_android_arnau;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

// -------------------------------------------------------------------------------------------------
// @author: Arnau Soler Tomás
// Clase: ServicioEscuharBeacons
// Descripción: Servicio que nos srive para detectar y mostrar los beacons BTLE
// -------------------------------------------------------------------------------------------------
public class ServicioEscuharBeacons extends IntentService {

    //Constantes y Variables Globales
    private static final String ETIQUETA_LOG = ">>>>";
    private long tiempoDeEspera = 10000;
    private boolean seguir = true;

    private BluetoothLeScanner elEscanner = null;
    private ScanCallback callbackDelEscaneo = null;

    BluetoothManager bluetoothManager = null;
    BluetoothAdapter bluetoothAdapter = null;
    private boolean scanning;
    private final Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    //private String uuidABuscar="45505347-2d47-5449-2d50-524f592d3341"; //Nuestra UUID
    //private String uuidABuscar="45505347-2d47-5449-2d50-524f592d3341"; //Charlie UUID
    //private String uuidABuscar="45:50:53:47:47:54:49:2d:43:52:49:53:54:49:41:4e:"; //Cristian UUID
    //private String uuidABuscar="45505347-2d47-5449-2d50-524f592d3341"; //Arduino UUID
    private final String uuidABuscar="44:29:2d:55:41:52:54:02:01:05:0f:09:41:50:36:38:"; //Test UUID

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        elEscanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    // ---------------------------------------------------------------------------------------------
    //  ServicioEscuharBeacons() --> void
    //
    //  Descripción: Constructor de la clase ServicioEscuharBeacons
    // ---------------------------------------------------------------------------------------------
    public ServicioEscuharBeacons() {
        super("HelloIntentService");

        Log.d(ETIQUETA_LOG, "ServicioEscucharBeacons.constructor: termina");
    }
    // ---------------------------------------------------------------------------------------------
    //  parar()
    //
    //  Descripción: Método de la clase ServicioEscuharBeacons que permite parar la recepción de beacons
    // ---------------------------------------------------------------------------------------------
    public void parar () {

        Log.d(ETIQUETA_LOG,"ServicioEscucharBeacons.parar()");


        if (!this.seguir){
            return;
        }

        this.seguir = false;
        //this.detenerBusquedaDispositivosBTLE();
        this.stopSelf();

        Log.d(ETIQUETA_LOG,"ServicioEscucharBeacons.parar() : acaba");
    }

    // ---------------------------------------------------------------------------------------------
    //  --> onDestroy()
    //
    //  Descripción: Método para detener completamente el servicio.
    // ---------------------------------------------------------------------------------------------
    public void onDestroy() {

        Log.d(ETIQUETA_LOG,"ServicioEscucharBeacons.onDestroy()");
        this.detenerBusquedaDispositivosBTLE();
        this.parar(); //posiblemente no haga falta, si stopService() ya se carga el servicio y su worker thread
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onHandleIntent(Intent intent) {

        this.tiempoDeEspera = intent.getLongExtra("tiempoDeEspera", /* default */ 50000);
        this.seguir = true;

        // esto lo ejecuta un WORKER THREAD !

        long contador = 1;

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );

        try {
            while ( this.seguir ) {
                Thread.sleep(tiempoDeEspera);
                Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: tras la espera:  " + contador );
                contador++;
                this.buscarTodosLosDispositivosBTLE();
            }

            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent : tarea terminada ( tras while(true) )" );

        } catch (InterruptedException e) {
            // Restore interrupt status.
            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: problema con el thread");

            Thread.currentThread().interrupt();
        }
        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: termina");
    }

    // ---------------------------------------------------------------------------------------------
    // buscarTodosLosDispositivosBTLE()
    // Descripción: Método que sirve para buscar todos los dispositivos Bluetooth que hay cerca.
    // En caso de haber alguno, llamaremos a mostrarInformacionDispositivoBTLE() para saber cual
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                mostrarInformacionDispositivoBTLE(result);
            }
        };

        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                elEscanner.stopScan(leScanCallback);
            }, SCAN_PERIOD);

            scanning = true;
            elEscanner.startScan(leScanCallback);
        } else {
            scanning = false;
            elEscanner.stopScan(leScanCallback);
        }

        //this.elEscanner.startScan(this.callbackDelEscaneo);

        Log.d(ETIQUETA_LOG,"final de buscar");

    } // ()

    // ---------------------------------------------------------------------------------------------
    // ScanResult --> mostrarInformacionDispositivoBTLE()
    // Descripción: Método que sirve para mostrar la información del dispositivo Bluetooth que se
    // haya encontrado previamente con el método buscarTodosLosDispositivosBTLE
    //
    // A su vez, si el dispositivo emite un beacon con la UUID que queremos, llamaremos a un método
    // para enviar la información al servidor mediante una consulta @POST
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {

        //BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ");
        Log.d(ETIQUETA_LOG, " ");
        Log.d(ETIQUETA_LOG, " ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        //Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " nombre = " + resultado.getDevice().getName());

        Log.d(ETIQUETA_LOG, " dirección = " + resultado.getDevice().getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);

        Log.d(ETIQUETA_LOG, " bytes = " + Utilidades.bytesToString(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        String prefijo = Utilidades.bytesToHexString(tib.getPrefijo());
        String uuid = Utilidades.bytesToHexString(tib.getUUID());
        String major = Utilidades.bytesToHexString(tib.getMajor()) + "( " + Utilidades.bytesToInt(tib.getMajor()) + " ) ";
        String minor = Utilidades.bytesToHexString(tib.getMinor()) + "( " + Utilidades.bytesToInt(tib.getMinor()) + " ) ";

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + prefijo);
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + uuid);
        //Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + major);
        Log.d(ETIQUETA_LOG, " minor  = " + minor);
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        if (uuid.equals(uuidABuscar)){
            Log.d("Busqueda", "Busqueda encontrada");
            //Utilidades.iniciarPost(prefijo, uuid, major, minor);
            Utilidades.enviarPOST(prefijo, uuid, major, minor);
        }

    } // ()

    // --------------------------------------------------------------
    // detenerBusquedaDispositivosBTLE()
    // Descripción: Sirve para deter la busqueda de dispositivos BTLE. Aunque no hace falta del todo
    // porque se detiene al cabo de un tiempo
    // --------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void detenerBusquedaDispositivosBTLE() {
        if(this.callbackDelEscaneo==null){ return; }
        this.elEscanner.stopScan(this.callbackDelEscaneo);
        this.callbackDelEscaneo=null;
    } // ()
} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
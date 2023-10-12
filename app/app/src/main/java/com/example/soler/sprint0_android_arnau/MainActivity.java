package com.example.soler.sprint0_android_arnau;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ingenieriajhr.blujhr.BluJhr;

// -------------------------------------------------------------------------------------------------
// @author: Arnau Soler Tomás
// Clase: MainActivity
// Descripción: Clase inicial de la App
// -------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {
    // ---------------------------------------------------------------------------------------------
   // Variables y Constantes Globales
    private static final String ETIQUETA_LOG = ">>>>";
    private Intent elIntentDelServicio = null;

    //Bluetooth y Beacons
    BluJhr blue;
    private static final long DEFAULT_SCAN_PERIOD_MS = 5000L; //6000 ms o 6 segundos

    // ---------------------------------------------------------------------------------------------
    //  v:View --> botonArrancarServicioPulsado() --> void
    //
    //  Descripción: Función relacionada con el botón "Arrancar Servicio" para arrancar el
    //               servicio "ServicioEscucharBeacons"
    // ---------------------------------------------------------------------------------------------
    public void botonArrancarServicioPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton arrancar servicio Pulsado" );

        if ( this.elIntentDelServicio != null ) {
            // ya estaba arrancado
            return;
        }

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : voy a arrancar el servicio");

        this.elIntentDelServicio = new Intent(this, ServicioEscuharBeacons.class);

        this.elIntentDelServicio.putExtra("tiempoDeEspera", DEFAULT_SCAN_PERIOD_MS);

        startService( this.elIntentDelServicio );

    } // ()

    // ---------------------------------------------------------------------------------------------
    //  v:View --> botonDetenerServicioPulsado() --> void
    //
    //  Descripción: Función relacionada con el botón "Detener Servicio" para detener el
    //               servicio "ServicioEscucharBeacons"
    // ---------------------------------------------------------------------------------------------
    public void botonDetenerServicioPulsado( View v ) {

        if ( this.elIntentDelServicio == null ) {
            // no estaba arrancado
            return;
        }
        stopService( this.elIntentDelServicio );

        this.elIntentDelServicio = null;
        Log.d(ETIQUETA_LOG, " boton detener servicio Pulsado" );
    } // ()
    // ---------------------------------------------------------------------------------------------
    //  savedInstanceState:Bundle --> OnCreate() --> void
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : empieza");
        Log.d(ETIQUETA_LOG, " MainActivity.constructor : acaba");

        //Variables del Bluetooth

        blue = new BluJhr(this);
        blue.onBluetooth();
    }

    //----------------------------------------------------------------------------------------------
    //  requestCode:N, permissions:<String>, grantResults:<N> --> onRequestPermissionsResult() --> void
    //----------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //-----------------------------------------------------------------------------------------------------------------------
        //Pedir permiso de Bluetooth
        //-----------------------------------------------------------------------------------------------------------------------
        if (blue.checkPermissions(requestCode,grantResults)){
            blue.initializeBluetooth();
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth();
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }

        int btleScan = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN);

        if(btleScan!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1122);
        }

        int btleAdv = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADVERTISE);

        if(btleAdv!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, 1122);
        }
        //-----------------------------------------------------------------------------------------------------------------------
        //Pedir permiso de Localización
        //-----------------------------------------------------------------------------------------------------------------------
        int lperm = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(lperm!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1122);
        }
        //-----------------------------------------------------------------------------------------------------------------------
        //Pedir permiso de Internet
        //-----------------------------------------------------------------------------------------------------------------------
        int intperm = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);

        if(intperm!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET}, 1122);
        }
        //-----------------------------------------------------------------------------------------------------------------------
        //Pedir permiso de Network
        //-----------------------------------------------------------------------------------------------------------------------
        int netperm = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE);

        if(netperm!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1122);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //----------------------------------------------------------------------------------------------
    //  requestCode:N, resultCode:N, data:Intent --> onActivityResult() --> void
    //
    // Descripción: Cuando la actividad se ha cargado, esta función comprobará si el Bluetooth se ha conectado o no
    //              En caso negativo, se cierra por falta de permisos.
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

} // class MainActivity()
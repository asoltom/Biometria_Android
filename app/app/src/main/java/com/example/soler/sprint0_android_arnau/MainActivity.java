package com.example.soler.sprint0_android_arnau;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {
    // ---------------------------------------------------------------------------------------------
   // Variables y Constantes Globales
    private static final String ETIQUETA_LOG = ">>>>";
    private Intent elIntentDelServicio = null;

    // ---------------------------------------------------------------------------------------------
    //  View:v --> void:botonArrancarServicioPulsado() --> void
    // ---------------------------------------------------------------------------------------------
    public void botonArrancarServicioPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton arrancar servicio Pulsado" );

        if ( this.elIntentDelServicio != null ) {
            // ya estaba arrancado
            return;
        }

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : voy a arrancar el servicio");

        this.elIntentDelServicio = new Intent(this, ServicioEscuharBeacons.class);

        this.elIntentDelServicio.putExtra("tiempoDeEspera", (long) 5000);
        startService( this.elIntentDelServicio );

    } // ()

    // ---------------------------------------------------------------------------------------------
    //  View:v --> void:botonDetenerServicioPulsado() --> void
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
    //  View:v --> void:OnCreate() --> void
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : empieza");
        Log.d(ETIQUETA_LOG, " MainActivity.constructor : acaba");

    }
} // class MainActivity()
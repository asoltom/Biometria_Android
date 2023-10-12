package com.example.soler.sprint0_android_arnau;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

// -------------------------------------------------------------------------------------------------
// @author: Arnau Soler Tomás
// Clase: Utilidades
// Descripción: Clase en la que recopilamos diversos métodos generales que nos sirven en las demás clases,
// tales como convertir información de los beacons en cadenas de texto, y hacer de puente entre la
// clase PeticionarioREST.java y el Servicio para solicitar una consulta @POST al Servidor
// -------------------------------------------------------------------------------------------------
public class Utilidades extends AppCompatActivity {
    //private static final String nuestraIP = "192.168.117.29"; //Ip con el Redmi Note 9
    //private static final String URL1 = "http://"+nuestraIP+"/EsqueletoWebAppEnPHPConSesion/rest/enviarMediciones.php";

    //-------------------------
    /*
    ------ANOTACIONES------
    Tras realizar muchisimas pruebas con mi móvil, llegué a la conclusión de que no se podia conectar al localhost fuese como fuese
    (con la ip, con la ip:puerto, ambos casos con la misma red y con la red propia del telefono, etc)
    No se si es porque me falta alguna configuración extra en XAMPP, en el móvil,
    si es por el Eset Antivirus y su gestión de permisos relacionado con Firewall,
    o si es porque tengo un Redmi Note 9 y tengo que configurar algo en especial.

    Tras buscar, encontré la herramienta NGROK con la que puedo hacer mi localhost público y, así, que mi móvil pueda acceder
    donde debe acceder.

    ------INSTRUCCIONES------
    Si te sirve con la ip, descomenta las lineas con las constantes "nuestraIP" y "URL1" que hay más arriba.
    Si a ti tampoco te sirve, prueba a descargar NGROK. Yo usé este tutorial: https://www.youtube.com/watch?v=OeB_sUFUM_A
    */
    //-------------------------
    private static final String extNGROK = "https://f5c8-37-133-162-135.ngrok-free.app";
    private static final String URL1 = extNGROK+"/EsqueletoWebAppEnPHPConSesion/rest/enviarMediciones.php";

    // -------------------------------------------------------------------------------
    // <byte> --> bytesToString() --> Txt
    // Descripción: Método que recibe una lista de bytes y devuelve esa misma lista en una cadena de texto
    // -------------------------------------------------------------------------------
    public static String bytesToString( byte[] bytes ) {
        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append( (char) b );
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------
    // <byte> --> bytesToInt() --> N
    // Descripción: Método que recibe una lista de bytes y los convierte en un único valor natural
    // -------------------------------------------------------------------------------
    public static int bytesToInt( byte[] bytes ) {
        return new BigInteger(bytes).intValue();
    }

    // -------------------------------------------------------------------------------
    // <byte> --> bytesToHexString() --> Txt | Txt vacio
    // Descripción: Si la lista de bytes no esta vacia, la transforma en una cadena de texto.
    // -------------------------------------------------------------------------------
    public static String bytesToHexString( byte[] bytes ) {

        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()

    // -------------------------------------------------------------------------------
    // Txt,Txt,Txt,Txt --> enviarPOST() --> @POST
    // Descripción: Método para enviar a la API REST los datos obtenido de la trama iBeacon
    // (prefijo, UUID, major, minor).
    // Concretamente, esta preparado para que se envien como parte de la query mediante otro metodo
    // de la clase PeticionarioREST.java ya que tenia problemas al enviarlo como JSON
    // -------------------------------------------------------------------------------
    public static void enviarPOST(String prefijo, String uuid, String major, String minor) {
        PeticionarioREST elPeticionario = new PeticionarioREST();

        elPeticionario.hacerPeticionREST("POST",  URL1,

                "?prefijo="+prefijo+"&uuid="+uuid+"&major="+major+"&minor="+minor,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d( "pruebasPeticionario", "TENGO RESPUESTA:\ncodigo = " + codigo + "\ncuerpo: \n" + cuerpo);

                    }
                }
        );
    } // ()

} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------



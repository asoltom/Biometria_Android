package com.example.soler.sprint0_android_arnau;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// -------------------------------------------------------------------------------------------------
// @author: Arnau Soler Tomás
// Clase: PeticionarioREST
// Descripción: Clase que nos sirve para realizar consultas @ET, (en caso de ser necesario en un futuro);
// y @POST para enviar la información de los beacons al Servidor
// -------------------------------------------------------------------------------------------------
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    // ---------------------------------------------------------------------------------------------
    // Variables Globales
    // ---------------------------------------------------------------------------------------------
    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;
    private int codigoRespuesta;
    private String cuerpoRespuesta = "";

    // ---------------------------------------------------------------------------------------------
    // Constructor vacio.
    //
    // No hace falta, por lo menos en esta versión del programa en la que se está realizando con Android Studio,
    // pero en otros lenguajes no viene mal tenerla. La dejo por si acaso
    // ---------------------------------------------------------------------------------------------
    public PeticionarioREST() {
        Log.d("clienterestandroid", "constructor()");
    }

    // ---------------------------------------------------------------------------------------------
    // Constructor con parametros
    //
    // En la versión que uso, el metodo this.execute se menosprecia. Pero por si acaso se tiene una versión más vieja, lo dejo
    // ---------------------------------------------------------------------------------------------
    public void hacerPeticionREST(String metodo, String urlDestino, String cuerpo, RespuestaREST laRespuesta) {
        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    // ---------------------------------------------------------------------------------------------
    // URL:String, elCuerpo:String --> direccionQuery() --> String
    //
    // Descripción: Método que me devuelve la URL final a la que me conectaré. La existencia de este
    // metodo radica en que le paso al servidor el contenido de la trama beacon mediante la propia Query.
    // Por lo tanto, a la URL por defecto, le sumo los parametros que vendrán en el cuerpo
    // ---------------------------------------------------------------------------------------------
    private String direccionQuery(){
        return this.urlDestino+this.elCuerpo;
    }
    // ---------------------------------------------------------------------------------------------
    // doInBackground() --> VoF
    //
    // Descripción: Este método viene por defecto de la extensión AsyncTask. Nos sirve para detectar
    // en "segundo plano" que clase de petición estamos realizando (@GET, @POST, @PUT, etc), a que
    // URL deseamos conectarnos, y que queremos enviar o recibir segun el método.
    // ---------------------------------------------------------------------------------------------
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");

        try {

            // envio la peticion
            String queryFinal=direccionQuery();

            Log.d("clienterestandroid", "doInBackground() me conecto a >" + queryFinal + "<");

            URL url = new URL(queryFinal);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestMethod(this.elMetodo);
            connection.setDoInput(true);

            // Si el metodo no es @GET y el Cuerpo no esta vacio...
            if (!this.elMetodo.equals("GET") && this.elCuerpo != null) {
                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true);
                // si no es GET, pongo el cuerpo que me den en la peticion
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                Log.d("elCuerpo","elCuerpo: "+this.elCuerpo);
                dos.writeBytes(this.elCuerpo);
                dos.flush();
                dos.close();
            }

            // Para aquí ya se debería haber enviado la peticion
            Log.d("clienterestandroid", "doInBackground(): peticion enviada ");

            // Por lo que ahora obtengo la respuesta

            int rc = connection.getResponseCode();
            String rm = connection.getResponseMessage();
            String respuesta = "" + rc + " : " + rm;
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);
            this.codigoRespuesta = rc;

            try {

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    Log.d("clienterestandroid", linea);
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
                Log.d("clienterestandroid", "cuerpo recibido=" + this.cuerpoRespuesta);

                connection.disconnect();

            } catch (IOException ex) {
                // dispara excepcin cuando la respuesta REST no tiene cuerpo y yo intento getInputStream()
                Log.d("clienterestandroid", "doInBackground() : parece que no hay cuerpo en la respuesta");
            }

            return true; // doInBackground() termina bien

        } catch (Exception ex) {
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
        }

        return false; // doInBackground() NO termina bien
    } // ()

    // ---------------------------------------------------------------------------------------------
    // Boolean --> onPostExecute() --> VoF
    // Descripción: Cuando se haya ejecutado la trama POST, sabremos si ha ido bien o no con este metodo
    // ---------------------------------------------------------------------------------------------
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);
        this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
    }

    // ---------------------------------------------------------------------------------------------
    // Descripción: Literalmente una interfaz que usa un callback para devolvernos el cuerpo
    // que se había enviado, y el código de respuesta (200 si OK, 404 si no encontrado, etc)
    // ---------------------------------------------------------------------------------------------
    public interface RespuestaREST {
        void callback(int codigo, String cuerpo);
    }

} // class



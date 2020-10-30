package com.example.readingfromapi

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILogginAsycnTask().execute()
    }

    private inner class CallAPILogginAsycnTask(): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            Log.i("Respuesta JSON", result)

            /* Leer json con librería GSON*/

            val responseData = Gson().fromJson(result, ResponseData::class.java) // Los datos entran a mi data class



            Log.i("ID", "${responseData.id}") /* Recuerda usar esta sintaxis para los tipo int*/
            Log.i("Nombre", responseData.nombre)
            Log.i("Edad", "${responseData.edad}")
            Log.i("email", responseData.email)

            idTextEdit.setText("${responseData.id}")
            nombreTextEdit.setText("${responseData.nombre}")
            edadTextView.setText("${responseData.edad}")
            emailTextView.setText("${responseData.email}")

        }

        override fun doInBackground(vararg params: Any?): String {
            var result:String

            var connection: HttpURLConnection? = null

            /* Empieza el proceso para leer un archivo json desde una api*/

            try {
                val url = URL("https://run.mocky.io/v3/e5c19a02-f98d-4f76-a050-bc1cfedc229b")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult: Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while(reader.readLine().also {line = it} != null){ /* "it" es el resultaod de readLine()*/
                            stringBuilder.append((line + "\n")) /* Se lee línea por línea el documento html que es en sí un json*/
                        }
                    }catch (e: IOException){
                        e.printStackTrace() /* Cacho errores*/
                    }finally {
                        try {
                            inputStream.close() /* Cierro la conexión del inputStream */
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString() /* Se pasa lo leído a string*/
                }else{
                    result = connection.responseMessage /* Cacho el mensaje de la conexión si la conexión no fue exitosa*/
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timeout" /* Cacha el tipo de error de tiempo agotaddo*/
            }catch (e: Exception){
                result = "Error: "+ e.message /* Para cachar cualquier tipo de error*/
            }finally {
                connection?.disconnect() /* Me desconecto de la conexión*/
            }
            return result /* Json contruido ó mensaje de error si hubo alguno*/
        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.dismiss()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }

}
package com.proyectos.angie.biometricauthapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),BiometricAuthCallback {
    companion object{
        //Definimos las claves como ctes:
        private const val USER_DATA="user_data"
        private const val NAME="name"
        private const val PHONE="phone"
        private const val MAIL="mail"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //1.Comprobamos si el dispositivo tiene capacidad biométrica, si no la tiene salimos:
        checkBiometricCapability()
        showBiometricPrompt()
        fillUserData()

        btnSave.setOnClickListener{saveUserData()}
    }
    private fun checkBiometricCapability(){
        if(!BiometricUtils.isDeviceReady(this)){
            finish() //Finalizamos la app
        }else{
            Toast.makeText(this,"BIOMETRÍA DISPONIBLE",Toast.LENGTH_LONG).show()
        }
    }
    private fun showBiometricPrompt(){
        BiometricUtils.showPrompt("Titulo","Subtitulo","Descripción","Cancelar",this,this)
    }
    private fun fillUserData(){
        //Accedemos a la shared:
        val sharedPreferences: SharedPreferences
                =getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
        etName.setText(sharedPreferences.getString(NAME,""))
        etPhoneNumber.setText(sharedPreferences.getString(PHONE,""))
        etMailAddress.setText(sharedPreferences.getString(MAIL,""))
    }
    private fun saveUserData(){
        //Accedemos a la shared:
        val sharedPreferences: SharedPreferences
            =getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)
        //Context.MODE_PRIVATE Pone los datos en modo privado para que sólo pueda acceder a ellos nuestra app
        val editor= sharedPreferences.edit()
        editor.putString(NAME,etName.text.toString())
        editor.putString(PHONE,etPhoneNumber.text.toString())
        editor.putString(MAIL,etMailAddress.text.toString())
        editor.apply()
    }

    override fun onSuccess() {
        Toast.makeText(this,"AUTH OK",Toast.LENGTH_LONG).show()
        layout.visibility= View.VISIBLE
    }

    override fun onError() { //Entra cuando el usuario se ha intentado autenticar muchas veces sin éxito
        Toast.makeText(this,"NO SE HA PODIDO COMPROBAR SU IDENTIDAD",Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onNotRecognized() {
       Log.d("MainActivity","Huella no reconocida")
    }

}
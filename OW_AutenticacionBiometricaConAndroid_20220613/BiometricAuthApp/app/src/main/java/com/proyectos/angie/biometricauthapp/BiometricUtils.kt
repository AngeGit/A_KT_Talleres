package com.proyectos.angie.biometricauthapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationError
import androidx.core.content.ContextCompat

/*PARA PROBAR EN EMULADOR:
        SI NO TENEMOS DADA DE ALTA UNA HUELLA, NOS VA A DAR EL isDeviceReady() false.
        1. Vamos a los ajustes del emulador =>Seguridad=> Fingerprint. Nos va a ofrecer como backup de fingerprint otro método
        Elegimos Fingerprint +PIN (en este caso he puesto 1111)
        2. Avanzamos las configuraciones hasta la ventana de Unlock with fingerprint. Le damos a NEXT.
        3. Abrimos una cmd =>    cd C:\Users\Angie\AppData\Local\Android\Sdk\platform-tools
        4.Creamos la huella =>   adb -e emu finger touch 111
        El 111 es el id de la huella, siempre que queramos usarla por consola hay que llamarla con este id.
        Lanzamos este comando cada vez que nos lo pida el emulador.
 */
interface BiometricAuthCallback{
    fun onSuccess()
    fun onError()
    fun onNotRecognized()
}
//Clase con  lo relacionado don operaciones de biometría. La declaramos como object para no tener que instanciarla después

object BiometricUtils{
    //BIOMETRIC_WEAK= Para consultar solamente sensor de huella, de iris, o facial.
    //BIOMETRIC_SUCCESS=Cte de la clase que nos dice
    fun showPrompt(
        title: String = "",
        subtitle: String = "",
        description: String = "",
        cancelButton: String = "",
        activity: AppCompatActivity,
        callback: BiometricAuthCallback
    ){
        val promptInfo=
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setAllowedAuthenticators(BIOMETRIC_WEAK)
                .setNegativeButtonText(cancelButton)
                .build()
            //.setAllowedAuthenticators(BIOMETRIC_WEAK) lo que hace es que si no tenemos huella, pruebe la autentificación  con otro de ls métodos WEAK
           val prompt:BiometricPrompt = initPrompt(activity, callback)
            prompt.authenticate(promptInfo)




    }
    private fun initPrompt(activity: AppCompatActivity, callback: BiometricAuthCallback):BiometricPrompt{
        //Hacemos el executor de nuestro callback:
        val executor = ContextCompat.getMainExecutor(activity)
        val authCallback= object: BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess()//Llamamos a nuestro callback
            }
            override fun onAuthenticationError(
                @AuthenticationError errorCode: Int, errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                callback.onError()//Llamamos a nuestro callback
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onNotRecognized()//Llamamos a nuestro callback
            }
        }
        return BiometricPrompt(activity, executor, authCallback)
    }
    //-------- Devuelve si nuestro dispositivo tiene capacidades hardware biométricas:
    fun isDeviceReady(context: Context):Boolean= getDeviceCapabilityforBiometrics(
        context,
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    ) == BIOMETRIC_SUCCESS
    //Asignación directa del método
    private fun getDeviceCapabilityforBiometrics(context: Context, biometricType: Int):Int=BiometricManager.from(
        context
    ).canAuthenticate(biometricType)



}
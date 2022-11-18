package com.openwebinars.workshop_sleep_api

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.openwebinars.workshop_sleep_api.databinding.ActivityMainBinding

class SleepTrackerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SleepTrackerActivity"
         const val SLEEP_ACTION = "com.openwebinars.workshop_sleep_api.SLEEP_EVENT"
         const val SLEEP_DATA= "SLEEP_DATA"
         const val SLEEP_TEST = "com.openwebinars.workshop_sleep_api.SLEEP_EVENT_TEST"
    }

    private val sleepDataManager= SleepDataManager(this)
    private lateinit var sleepEventsBroadcast:BroadcastReceiver //lateinit: se va a inicializar más tarde
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        //Lo que hace el ActivityMainBinding es crear una clase java con los atributos del layout (autogenerada)
        setContentView(binding.root)
        checkPermissions()
    }
    //region Permissions
    private fun checkPermissions() {
        if (isPermissionGranted()) {
            requestSleepTracking()
        } else {
            requestPermission()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACTIVITY_RECOGNITION).not() &&
                grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showSettingsDialog(this)
        } else if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION &&
                permissions.contains(Manifest.permission.ACTIVITY_RECOGNITION) &&
                grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Permissions granted")
            requestSleepTracking()
        }
    }
    //endregion
    private fun requestSleepTracking() {
        Log.d(TAG, "Request Sleep Tracking")
        //Inicializamos
        registerSleepUpdateEvents()
        //LLamamos al sleepDataManager y le decimos k se suscriba:
        sleepDataManager.subscribe()
    }
    //Método a la escucha de events:
    private fun registerSleepUpdateEvents(){
        sleepEventsBroadcast=object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
               intent?.let {
                   if(intent.action== SLEEP_ACTION){ //Nos cercioramos que es un intent que manda el SleepUiData
                       //Sacamos los datos del intent:
                      val event= intent.getParcelableExtra<SleepUiData>(SLEEP_ACTION)
                       //y actualizamos la interfaz:
                       updateUi(event)
                   }
                   if(intent.action== SLEEP_TEST){ //Nos cercioramos que es un intent que manda el SleepUiData
                       //Sacamos los datos del intent:
                       val event= intent.getStringExtra(SLEEP_TEST)
                       Log.d(TAG,"Intent test recibido")
                       Log.d(TAG,"JSON=>"+event.toString())
                       //y actualizamos la interfaz:
                        val data= Gson().fromJson(event,SleepUiData::class.java)
                       Log.d(TAG,"Confidence ${data.confidence}  Light ${data.light}  Motion ${data.motion}")
                       updateUi(data)
                   }
               }
            }
        }
        //Los broadcast hay que registrarlos:
        this.registerReceiver(sleepEventsBroadcast, IntentFilter(SLEEP_ACTION))
        this.registerReceiver(sleepEventsBroadcast, IntentFilter(SLEEP_TEST))
    }
    private fun updateUi(event:SleepUiData?){
        event?.let{
            if(event.isUserSleeping()){
                binding.tvState.text="SLEEP"
            }else{
                binding.tvState.text="AWAKE"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sleepDataManager.unsuscribe()
    }

}
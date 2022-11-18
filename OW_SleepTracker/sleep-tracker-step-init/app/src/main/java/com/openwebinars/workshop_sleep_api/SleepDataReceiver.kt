package com.openwebinars.workshop_sleep_api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.openwebinars.workshop_sleep_api.SleepTrackerActivity.Companion.SLEEP_ACTION
import com.openwebinars.workshop_sleep_api.SleepTrackerActivity.Companion.SLEEP_DATA

class SleepDataReceiver:BroadcastReceiver() {
    companion object{
        val tag:String="SleepDataReceiver"
    }
    //Mecanismo de android para detectar ciertos eventos del dispositivo, en este caso del sleep API
    override fun onReceive(context: Context?, intent: Intent?) {
      if(SleepClassifyEvent.hasEvents(intent)){ //Comprobamos si son eventos Sleep API (Classify)
          intent?.let{ //Nos aseguramos que no es nulo
              val events= SleepClassifyEvent.extractEvents(intent)
              for (event in events){
                  Log.d(tag,"Confidence ${event.confidence}  Light ${event.light}  Motion ${event.motion}")
                  context?.let{
                      sendEventToUi(event, context)
                  }
              }
          }
          val events= SleepClassifyEvent.extractEvents(intent)
          for (event in events){
              Log.d(tag,"Confidence ${event.confidence}  Light ${event.light}  Motion ${event.motion}")
              context?.let{
                  sendEventToUi(event, context)
              }

          }
      }
    }
    //Manda los eventos a la interfaz a través del broadcast:
    private fun sendEventToUi(event:SleepClassifyEvent, context: Context){
        //Creamos el intent que comunica el Broadcast con la UI:
        val intent= Intent(SLEEP_ACTION).apply {
            putExtra(SLEEP_DATA, event.toUiEvent())
        }
        //Lo enviamos a través del broadcast:
        context.sendBroadcast(intent)
    }

    //Hacemos una función de extensión que nos convierta una clase SleepClassifyEvent() (la que usa el broadcast) a SleepUiData.
    private fun SleepClassifyEvent.toUiEvent()=
        SleepUiData(
            confidence=this.confidence,
            light=this.light,
            motion=this.motion
        )


}
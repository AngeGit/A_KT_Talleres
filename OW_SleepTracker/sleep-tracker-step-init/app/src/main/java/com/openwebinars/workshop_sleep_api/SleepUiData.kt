package com.openwebinars.workshop_sleep_api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


//Esta clase hay que meterla en un intent, con lo que hay que Serializarla o Parcelizarla
//Para parcelizarla se pone el plugin en el gradle, y en la clase, antes de la definición se pone la anotación @Parcelize y se he implementar la interfaz Parcelable
@Parcelize
data class SleepUiData(
    val confidence:Int,
    val light:Int,
    val motion:Int
): Parcelable {
    companion object{
        //Valores que a mi me de la gana para que el user esté durmiendo XD
        private const val CONFIDENCE_THRESHOLD = 50
        private const val LIGHT_THRESHOLD = 3
        private const val MOTION_THRESHOLD = 1
    }
    //Método que nos diga si el usuario está durmiendo
    fun isUserSleeping(): Boolean =
        confidence < CONFIDENCE_THRESHOLD && light < LIGHT_THRESHOLD && motion <=MOTION_THRESHOLD
}

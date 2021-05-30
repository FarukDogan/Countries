package com.example.countries.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class CustomSharedPrefences {
    companion object{
        private val PREFERENCES_TIME ="preferences_time"
        private var sharedPrefences :SharedPreferences? = null
        @Volatile private var instance : CustomSharedPrefences?=null
        private  val lock =Any()
        operator fun invoke(context: Context): CustomSharedPrefences = instance ?: synchronized(lock){
            instance ?: makeCustomSharedPreferences(context).also {
                instance=it
            }

        }
        private fun makeCustomSharedPreferences(context: Context) :CustomSharedPrefences{
            sharedPrefences=PreferenceManager.getDefaultSharedPreferences(context)
            return CustomSharedPrefences()
        }
    }
    fun saveTime(time:Long){
        sharedPrefences?.edit(commit = true){
            putLong(PREFERENCES_TIME,time)
        }
    }
    fun getTime ()= sharedPrefences?.getLong(PREFERENCES_TIME,0)
}
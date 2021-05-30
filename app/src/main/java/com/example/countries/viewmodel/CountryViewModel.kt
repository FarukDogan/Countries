package com.example.countries.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.countries.model.Country
import com.example.countries.service.CountryDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class CountryViewModel(application: Application) :BaseViewModel(application){
    val countryLiveData = MutableLiveData<Country>()

    @InternalCoroutinesApi
    fun getDataFromRoom(uuid :Int){

launch {
    val dao =CountryDatabase(getApplication()).countryDao()
   val country= dao.getCountry(uuid)
    countryLiveData.value =country
}
    }

}
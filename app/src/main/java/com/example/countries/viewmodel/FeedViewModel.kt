package com.example.countries.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.countries.model.Country
import com.example.countries.service.CountryAPIService
import com.example.countries.service.CountryDatabase
import com.example.countries.util.CustomSharedPrefences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class FeedViewModel(application: Application):BaseViewModel(application) {
    private val countryApiService =CountryAPIService()
    private val dispoable =CompositeDisposable()
    private var customPreferences = CustomSharedPrefences(getApplication())
    private var refreshTime = 5*60*1000*1000*1000L

    val countries = MutableLiveData<List<Country>>()
    val countryError =MutableLiveData<Boolean>()
    val countryLoading =MutableLiveData<Boolean>()

    @InternalCoroutinesApi
    fun refreshData(){

        val updateTime =customPreferences.getTime()
        if (updateTime!=null&&updateTime!= 0L &&System.nanoTime()-updateTime<refreshTime){
           getDataFromSQLite()
        }else {
            getDataFromAPI()
        }

    }
    @InternalCoroutinesApi
    fun refreshfromAPI(){
        getDataFromAPI()
    }
    @InternalCoroutinesApi
    private fun getDataFromSQLite(){
        countryLoading.value = true
        launch {
            val countries =CountryDatabase(getApplication()).countryDao().getAllCountries()
            showCountries(countries)
            Toast.makeText(getApplication(),"SQL den veriler geldi", Toast.LENGTH_LONG).show()
        }
    }
    @InternalCoroutinesApi
    private fun getDataFromAPI(){
        countryLoading.value=true
        dispoable.add(
            countryApiService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<List<Country>>(){
                    override fun onSuccess(t: List<Country>) {
                        storeInSQLite(t)
                        Toast.makeText(getApplication(),"internetden veriler geldi", Toast.LENGTH_LONG).show()


                    }

                    override fun onError(e: Throwable) {
                        countryLoading.value=false
                        countryError.value=true
                        e.printStackTrace()

                    }

                })
        )
    }
    private fun showCountries(countryList :List<Country>){
        countries.value=countryList
        countryError.value=false
        countryLoading.value=false
    }
    @InternalCoroutinesApi
    private fun storeInSQLite(list :List<Country>){
launch {
    val dao =CountryDatabase(getApplication()).countryDao()
    dao.deleteAllCountries()
   val listlong= dao.insertAll(*list.toTypedArray())
    var i = 0
    while (i<list.size){
        list[i].uuid=listlong[i].toInt()
        i=i+1
    }
    showCountries(list)
}

        customPreferences.saveTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        dispoable.clear()
    }
}
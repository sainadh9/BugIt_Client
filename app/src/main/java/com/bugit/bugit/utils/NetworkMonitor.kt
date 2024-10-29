package com.bugit.bugit.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkMonitor(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var _isNetworkAvailable = MutableLiveData<Boolean>()
    var isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _isNetworkAvailable.postValue(false)

                //Network is Lost
            }
        })
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
    }

}
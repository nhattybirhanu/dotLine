package com.dotline.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

abstract class NetworkListener : BroadcastReceiver {
    private var context: Context? = null
    private var connectivityManager: ConnectivityManager? = null
    open fun networkConnection(context: Context?, connected: Boolean, mobile: Boolean) {}
    override fun onReceive(context: Context, intent: Intent) {
        val status = getConnectivityStatusString(context)
        if ("android.net.conn.CONNECTIVITY_CHANGE" == intent.action) {
            networkConnection(
                context,
                status != NETWORK_STATUS_NOT_CONNECTED,
                getConnectivityStatus(context) == TYPE_MOBILE
            )
        }
    }

    constructor() {}

    fun dismiss() {
        if (connectivityManager != null) connectivityManager!!.unregisterNetworkCallback(
            networkCallback
        )
    }

    var networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            // network available
            networkConnection(
                context,
                true,
                getConnectivityStatusString(context) == NETWORK_STATUS_MOBILE
            )
        }

        override fun onLost(network: Network) {
            // network unavailable
            networkConnection(context, false, false)
        }
    }

    constructor(context: Context) {
        this.context = context
        networkConnection(
            context,
            getConnectivityStatusString(context) != NETWORK_STATUS_NOT_CONNECTED,
            getConnectivityStatusString(context) == NETWORK_STATUS_MOBILE
        )
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager!!.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager!!.registerNetworkCallback(request, networkCallback)
        }
    }

    fun getConnectivityStatus(context: Context?): Int {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context?): Int {
        val conn = getConnectivityStatus(context)
        var status = 0
        if (conn == TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED
        }
        return status
    }

    companion object {
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 0
        const val NETWORK_STATUS_NOT_CONNECTED = 0
        const val NETWORK_STATUS_WIFI = 1
        const val NETWORK_STATUS_MOBILE = 2
    }
}
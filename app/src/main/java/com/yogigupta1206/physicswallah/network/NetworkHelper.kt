package com.yogigupta1206.physicswallah.network

import android.content.Context
import android.net.*
import android.os.Build
import com.google.gson.GsonBuilder
import retrofit2.HttpException
import retrofit2.Response


/**
 * Created by Abhin.
 */

sealed class NetworkResult<T>(
    val responseData: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : NetworkResult<T>(data)
    class Error<T>(
        message: String? = null,
        data: T? = null
    ) : NetworkResult<T>(
        data,
        message
    )
}

abstract class BaseApiResponse<T>(val context: Context) {
    inline fun <reified T> safeApiCall(apiCall: () -> Response<T>): NetworkResult<T> {
        try {
            return if (isInternetAvailable(context)) {
                val response = apiCall()
                when {
                    response.isSuccessful -> {
                        val body = response.body()
                        NetworkResult.Success(body)
                    }
                    else -> {
                        val jsonString = response.errorBody()?.string()
                        val body = GsonBuilder().create().fromJson(jsonString, T::class.java)
                        NetworkResult.Error("Api call failed ${response.code()} ${response.message()}", body)
                    }
                }
            } else {
                error("No Internet available")
            }

        } catch (e: Exception) {
            return error(e.message.toString())
        }
    }

    fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")
}

fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
    } else {
        val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
        result = activeNetwork?.isConnectedOrConnecting == true
    }
    return result
}

val networkRequest: NetworkRequest = NetworkRequest.Builder()
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()


fun registerNetworkObserverCallback(context: Context, networkCallback: ConnectivityManager.NetworkCallback) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

}

fun unregisterNetworkObserver(context: Context, networkCallback: ConnectivityManager.NetworkCallback){
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    connectivityManager.unregisterNetworkCallback(networkCallback)
}



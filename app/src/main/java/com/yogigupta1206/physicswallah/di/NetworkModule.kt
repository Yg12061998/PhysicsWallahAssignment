package com.yogigupta1206.physicswallah.di

import androidx.databinding.ktx.BuildConfig
import com.yogigupta1206.physicswallah.network.ApiService
import com.yogigupta1206.physicswallah.utils.BASE_URL
import com.yogigupta1206.physicswallah.utils.NETWORK_UNKNOWN_ERROR
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Yogesh.
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("headerIntercepter") headerInterceptor: Interceptor
    ): OkHttpClient {
        val okHttpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        okHttpClient.readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInterceptor)

        if (BuildConfig.DEBUG) {
            loggingInterceptor?.let { okHttpClient.addInterceptor(it) }
        }

        return okHttpClient.build()
    }

    @Singleton
    @Provides
    fun provideConvertorFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @Named("headerIntercepter")
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request: Request.Builder = chain.request()
                .newBuilder()
            try {
                chain.proceed(request = request.build())
            } catch (e: IOException) {
                getNetworkErrorResponse(chain, e)
            }
        }
    }

    private fun getNetworkErrorResponse(chain: Interceptor.Chain, e: Exception) = Response.Builder()
        .code(NETWORK_UNKNOWN_ERROR)
        .request(chain.request())
        .body(getErrorResponseBody(chain.request().url.encodedPath))
        .message(e.message ?: e.toString())
        .protocol(Protocol.HTTP_1_1)
        .build()

    private fun getErrorResponseBody(url: String) =
        url.toResponseBody("text/plain; charset=utf-8".toMediaTypeOrNull())
}
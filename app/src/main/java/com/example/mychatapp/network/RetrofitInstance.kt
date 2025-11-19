package com.example.mychatapp.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.1.39:5047/"
    private const val TAG = "OkHttp"   // Tag LOG thống nhất

    /** Custom logging interceptor */
    private class LoggingInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val request: Request = chain.request()

            // Log Request
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "--> ${request.method} ${request.url}")
                request.headers.forEach { header ->
                    Log.d(TAG, "${header.first}: ${header.second}")
                }
            }

            val startTime = System.currentTimeMillis()
            val response: Response = chain.proceed(request)
            val endTime = System.currentTimeMillis()

            // Log Response
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "<-- ${response.code} ${request.url} (${endTime - startTime}ms)")
                response.headers.forEach { header ->
                    Log.d(TAG, "${header.first}: ${header.second}")
                }
            }

            return response
        }
    }

    /** Khởi tạo OkHttpClient với timeout + logging */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor())
            .build()
    }

    /** Khởi tạo Retrofit */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** API Service */
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

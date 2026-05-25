package com.example.profitly

import android.util.Log
import com.example.profitly.data.api.ApiConfig
import com.example.profitly.data.api.ProfitlyApiService
import com.example.profitly.data.repository.ProfitlyRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val httpLogger = HttpLoggingInterceptor.Logger { message ->
        Log.d("ProfitlyHttp", message)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor(httpLogger).apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            },
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(ProfitlyApiService::class.java)
    val repository = ProfitlyRepository(apiService)
}

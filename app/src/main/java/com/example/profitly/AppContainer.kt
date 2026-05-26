package com.example.profitly

import android.content.Context
import android.util.Log
import com.example.profitly.data.api.ApiConfig
import com.example.profitly.data.api.AuthInterceptor
import com.example.profitly.data.api.AuthTokenProvider
import com.example.profitly.data.api.ProfitlyApiService
import com.example.profitly.data.repository.ProfitlyRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val httpLogger = HttpLoggingInterceptor.Logger { message ->
        Log.d("ProfitlyHttp", message)
    }

    private val authTokenProvider = AuthTokenProvider(context)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(authTokenProvider))
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
    val tokenProvider: AuthTokenProvider = authTokenProvider
}

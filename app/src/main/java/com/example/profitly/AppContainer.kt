package com.example.profitly

import com.example.profitly.data.api.ApiConfig
import com.example.profitly.data.api.ProfitlyApiService
import com.example.profitly.data.repository.ProfitlyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService = retrofit.create(ProfitlyApiService::class.java)
    val repository = ProfitlyRepository(apiService)
}

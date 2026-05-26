package com.example.profitly.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: AuthTokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        tokenProvider.token?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        if (request.method == "POST" || request.method == "PUT") {
            builder.header("Content-Type", "application/json")
        }

        builder.header("Accept", "application/json")

        return chain.proceed(builder.build())
    }
}

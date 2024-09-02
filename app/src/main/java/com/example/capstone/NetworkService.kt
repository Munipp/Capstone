package com.example.capstone

import com.example.capstone.Constants.BASE_URL
import com.example.capstone.data.DictionaryApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val dictionaryApi : DictionaryApi = getInstance().create(DictionaryApi::class.java)
}
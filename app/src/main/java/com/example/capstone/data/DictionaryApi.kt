package com.example.capstone.data

import com.example.capstone.WordResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("en/{word}")
    suspend fun getDescription(@Path("word") word : String) : Response<List<WordResult>>
}
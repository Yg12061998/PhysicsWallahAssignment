package com.yogigupta1206.physicswallah.network

import com.yogigupta1206.physicswallah.network.model.profile.Profile
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("easygautam/data/users")
    suspend fun getProfiles(): Response<ArrayList<Profile>>

}

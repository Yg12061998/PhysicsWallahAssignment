package com.yogigupta1206.physicswallah.repository

import android.content.Context
import com.yogigupta1206.physicswallah.network.ApiService
import com.yogigupta1206.physicswallah.network.BaseApiResponse
import com.yogigupta1206.physicswallah.network.NetworkResult
import com.yogigupta1206.physicswallah.network.model.profile.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProfilesRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext context: Context
) : BaseApiResponse<Any?>(context){

    suspend fun getProfilesData(): NetworkResult<ArrayList<Profile>> =
       safeApiCall { apiService.getProfiles() }

}
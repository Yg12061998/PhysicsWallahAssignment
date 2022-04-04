package com.yogigupta1206.physicswallah.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogigupta1206.physicswallah.network.NetworkResult
import com.yogigupta1206.physicswallah.network.model.profile.Profile
import com.yogigupta1206.physicswallah.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(private val repository: ProfilesRepository) :
    ViewModel() {

    sealed class NavigateState {
        object NavigateBack : NavigateState()
        data class NavigateToProfile(
            val name:String
        ): NavigateState()
    }

    sealed class UiState{
        object Loading: UiState()
        object LoadingFinish: UiState()

        data class ErrorState(
            val message:String
        ): UiState()
    }

    val navigateState: LiveData<NavigateState> get() = _navigateState
    val uiState: LiveData<UiState> get() = _uiState
    val profiles: LiveData<ArrayList<Profile>> get() = _profiles

    private var _navigateState = MutableLiveData<NavigateState>()
    private var _uiState = MutableLiveData<UiState>()
    private var _profiles = MutableLiveData<ArrayList<Profile>>()

    private var profilesData = arrayListOf<Profile>()

    fun init(){
        _uiState.value = UiState.Loading
        if(profilesData.isNullOrEmpty()){
            viewModelScope.launch {
                val data = withContext(Dispatchers.IO){ repository.getProfilesData() }
                if(data is NetworkResult.Success){
                    _uiState.value = UiState.LoadingFinish
                    data.responseData?.let{
                        profilesData.clear()
                        profilesData.addAll(it)
                        _profiles.value = it
                    }
                }else{
                    _uiState.value = UiState.LoadingFinish
                    _uiState.postValue(UiState.ErrorState(data.message.toString()))
                }
            }

        }else{
            _uiState.postValue(UiState.LoadingFinish)
            _profiles.value = profilesData
        }
    }

    fun backPressed(){
        _navigateState.value = NavigateState.NavigateBack
    }

    fun itemClicked(index : Int){
        if(index < profilesData.size){
            _navigateState.value = NavigateState.NavigateToProfile(profilesData[index].name.toString())
        }
    }
}
package com.example.intermediate.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.intermediate.data.datastore.DataStoreInstance
import com.example.intermediate.data.datastore.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(application))
    }

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    fun getUserNameAndEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                userPreference.username.collect { username ->
                    this@SettingsViewModel._username.postValue(username)
                }
            }
            launch {
                userPreference.userEmail.collect { userEmail ->
                    this@SettingsViewModel._userEmail.postValue(userEmail)
                }
            }
        }
    }
}
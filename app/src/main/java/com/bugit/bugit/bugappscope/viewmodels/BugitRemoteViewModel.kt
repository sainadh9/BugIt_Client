package com.bugit.bugit.bugappscope.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bugit.bugit.bugappscope.data.BugItRemoteRepository
import com.bugit.bugit.bugappscope.domain.entities.CreateRowResponse
import com.bugit.bugit.bugappscope.domain.entities.CreateSheetResponse
import com.bugit.bugit.bugappscope.domain.entities.GetAllSheetsResponse
import com.bugit.bugit.bugappscope.domain.entities.PostCreateSheetModel
import com.bugit.bugit.bugappscope.domain.entities.PostRowToSheet
import com.bugit.bugit.bugappscope.domain.usecases.CreateNewSheetUseCase
import com.bugit.bugit.local.BugPref
import com.bugit.bugit.utils.UniversalManager.throwError
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BugitRemoteViewModel @Inject constructor(
    private val repository: BugItRemoteRepository,
    private val bugPref: BugPref

) :
    ViewModel() {
    val createNewSheetUseCase = CreateNewSheetUseCase(repository)


    private var _showProgressBar = MutableLiveData<Boolean?>()
    val showProgressBar: LiveData<Boolean?> = _showProgressBar


    private var _getAllSheetsResponse = MutableLiveData<GetAllSheetsResponse?>()
    val getAllSheetsResponse: LiveData<GetAllSheetsResponse?> = _getAllSheetsResponse

    private var _createSheetResponse = MutableLiveData<CreateSheetResponse?>()
    val createSheetResponse: LiveData<CreateSheetResponse?> = _createSheetResponse

    private var _createRowResponse = MutableLiveData<CreateRowResponse?>()
    val createRowResponse: LiveData<CreateRowResponse?> = _createRowResponse

    private var _uploadedUrl = MutableLiveData<String?>()
    val uploadedUrl: LiveData<String?> = _uploadedUrl

    private var _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    private var _latestTab = MutableStateFlow("")
    val latestTab: StateFlow<String?> = _latestTab


    fun saveNewTabLocally(title: String) {
        viewModelScope.launch {
            bugPref.saveLatestTab(title)
        }
    }

    fun getNewTab() {
        viewModelScope.launch {
            bugPref.getLatestTab().collectLatest { value ->
                _latestTab.value = value
            }

        }
    }


    fun getAllSheetData(key: String) {
        viewModelScope.launch {
            try {
                _showProgressBar.value = true
                delay(2000)
                val response = repository.getAllSheetData(key)
                if (response.isSuccessful) {
                    _getAllSheetsResponse.value = response.body()
                    _showProgressBar.value = false

                } else {
                    _showProgressBar.value = false
                    _errorMessage.value = response.errorBody()?.string()

                }


            } catch (e: Exception) {
                _showProgressBar.value = false
                _errorMessage.value = throwError(e)
            }

        }

    }


    fun createSheet(createSheetModel: PostCreateSheetModel, key: String) {
        viewModelScope.launch {

            try {
                _showProgressBar.value = true
                delay(2000)
                val response = createNewSheetUseCase.postCreateSheet(createSheetModel, key)
                if (response.isSuccessful) {
                    _createSheetResponse.value = response.body()
                    _showProgressBar.value = false

                } else {
                    _showProgressBar.value = false
                    _errorMessage.value = response.errorBody()?.string()

                }


            } catch (e: Exception) {
                _showProgressBar.value = false
                _errorMessage.value = throwError(e)
            }

        }

    }

    fun postCreateRow(
        range: String,
        insertDataOption: String,
        valueInputOption: String,
        key: String,
        postRowToSheet: PostRowToSheet,
    ) {
        viewModelScope.launch {
            try {
                _showProgressBar.value = true
                delay(2000)
                val response =
                    repository.postCreateRow(
                        range,
                        insertDataOption,
                        valueInputOption,
                        key,
                        postRowToSheet
                    )

                if (response.isSuccessful) {
                    _showProgressBar.value = false
                    _createRowResponse.value = response.body()
                } else {
                    _showProgressBar.value = false
                    _errorMessage.value = response.errorBody()?.string()

                }


            } catch (e: Exception) {
                _showProgressBar.value = false
                _errorMessage.value = throwError(e)
            }
        }

    }


    fun uploadImageToStorage(selectedImageUri: Uri, errorDownloadingFailed: String) {
        try {
        _showProgressBar.value = true

        val storageRef: StorageReference by lazy {
            Firebase.storage.reference
        }
        val imgName = "Img${System.currentTimeMillis()}"
        val uploadimage =
            storageRef.child("images/").child("${imgName}.jpg").putFile(selectedImageUri)
        uploadimage.addOnSuccessListener {
            storageRef.child("images/").child("${imgName}.jpg").downloadUrl.addOnSuccessListener {
                _uploadedUrl.value = it.toString()
                _showProgressBar.value = false
            }.addOnFailureListener {
                _errorMessage.value = errorDownloadingFailed
                _showProgressBar.value = false
            }

        }.addOnFailureListener {
            _errorMessage.value = errorDownloadingFailed
            _showProgressBar.value = false


        }
        } catch (e: Exception) {
            _showProgressBar.value = false
            _errorMessage.value = throwError(e)
        }


    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}



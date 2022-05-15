package com.example.testapp.businesslogic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.model.Notes
import com.example.testapp.repository.NotesRepository
import com.example.testapp.utils.NetworkResponse
import com.example.testapp.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel: ViewModel(){
    private val notesRepository= NotesRepository()
    private val createNoteLiveData: MutableLiveData<NetworkResponse<Boolean>> = MutableLiveData<NetworkResponse<Boolean>>()
    val createPublicNoteLiveData: MutableLiveData<NetworkResponse<Boolean>> = createNoteLiveData
    fun createNote(note: Notes){

        viewModelScope.launch(Dispatchers.IO){
            notesRepository.createNote(note){
                createNoteLiveData.postValue(NetworkResponse.loading())
                when(it){
                    is Results.Success ->{
                        createNoteLiveData.postValue(NetworkResponse.success(true, it.data))
                    }
                    is Results.Error ->{
                        createNoteLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }
    fun getNoteId(): String {
        return notesRepository.getNoteId()
    }
}
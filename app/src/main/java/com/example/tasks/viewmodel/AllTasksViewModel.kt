package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.TaskRepositry

class AllTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val mTaskRepositry = TaskRepositry(application)

    private val mValidation = MutableLiveData<ValidationListener>()
    val validation : LiveData<ValidationListener> = mValidation
    private var mTaskFilter = 0

    private val mList = MutableLiveData<List<TaskModel>>()
    val tasks : LiveData<List<TaskModel>> = mList

    fun list(taskFilter: Int){
        mTaskFilter = taskFilter

        val listener = object : APIListener<List<TaskModel>>{

            override fun onSuccess(model: List<TaskModel>) {
                mList.value = model
            }

            override fun onFailure(str: String) {
                mList.value = arrayListOf()
                mValidation.value = ValidationListener(str)
            }

        }

        when (mTaskFilter) {
            TaskConstants.FILTER.ALL -> mTaskRepositry.all(listener)
            TaskConstants.FILTER.NEXT -> mTaskRepositry.nextWeek(listener)
            TaskConstants.FILTER.EXPIRED -> mTaskRepositry.overDue(listener)
        }

    }

    fun delete(id: Int) {
        mTaskRepositry.delete(id, object : APIListener<Boolean>{
            override fun onSuccess(model: Boolean) {
                list(mTaskFilter)
                mValidation.value = ValidationListener()
            }

            override fun onFailure(str: String) {
                mValidation.value = ValidationListener(str)
            }

        })
    }


    fun complete(id: Int){
        updateStatus(id, true)
    }

    fun undo(id: Int){
        updateStatus(id, false)
    }

    private fun updateStatus(id: Int, complete: Boolean){
        mTaskRepositry.updateStatus(id, complete, object : APIListener<Boolean>{
            override fun onSuccess(Model: Boolean) {
                list(mTaskFilter)
            }

            override fun onFailure(str: String) {

            }
        })
    }
}
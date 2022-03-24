package com.example.tasks.service.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.local.TaskDatabase
import com.example.tasks.service.repository.remote.PriorityService
import com.example.tasks.service.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(val context: Context) : BaseRepository(context) {

    //Inicio meu a instancia do retrofit
    private val mRemote = RetrofitClient.createService(PriorityService::class.java)
    //Instancio o database
    private val mPriorityDatabase = TaskDatabase.getDatabase(context).priorityDao()
    fun all(){

        if (!isConnectionAvailable(context)) {
            return
        }

        val call: Call<List<PriorityModel>> = mRemote.list()
        call.enqueue(object : Callback<List<PriorityModel>>{
            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                if (response.code() == TaskConstants.HTTP.SUCCESS) {
                    mPriorityDatabase.clear() //Limpo os dados do banco antes de salvar novamente
                    response.body()?.let { mPriorityDatabase.save(it) }
                }
            }

            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {

            }

        })
    }

    fun localList() = mPriorityDatabase.list()

    fun getDescription(id: Int) = mPriorityDatabase.getDescription(id)

    /**
     * Verifica se existe conexão com internet
     */


}
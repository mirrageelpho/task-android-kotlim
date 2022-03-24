package com.example.tasks.service.listener

import com.example.tasks.service.model.HeaderModel

interface APIListener<T> {
    fun onSuccess(Model: T)
    fun onFailure(str: String)
}
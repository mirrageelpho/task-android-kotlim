package com.example.tasks.service.listener

class ValidationListener(msg: String = "") {
    private var mStatus: Boolean = true
    private var mMessage: String = ""

    init {
        if(msg != ""){
            mStatus = false
            mMessage = msg
        }
    }

    fun success() = mStatus
    fun failure() = mMessage
}
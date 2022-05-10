package com.example.testapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.testapp.model.User

class SessionManager(context:Context) {
    private var sharedPreferences:SharedPreferences
    private var editor:SharedPreferences.Editor
    init {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun storeUserInfo(user: User){
        editor.putString(Constants.UID, user.userId)
        editor.putString(Constants.FIRST_NAME, user.firstName)
        editor.putString(Constants.LAST_NAME, user.lastName)
        editor.putString(Constants.EMAIL_ADDRESS, user.email)
        editor.putBoolean(Constants.IS_LOGGED_IN, true)
        editor.apply()
    }
    fun getUserInfo():User{
        val user = User()
        user.userId = sharedPreferences.getString(Constants.UID, "")
        user.firstName = sharedPreferences.getString(Constants.FIRST_NAME, "")
        user.lastName = sharedPreferences.getString(Constants.LAST_NAME, "")
        user.email = sharedPreferences.getString(Constants.EMAIL_ADDRESS, "")
        return user
    }

    fun isLoggedIn():Boolean{
        return sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)
    }
    fun clear(){
        editor.clear()
        editor.apply()
    }
}
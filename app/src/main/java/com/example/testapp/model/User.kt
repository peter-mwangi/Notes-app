package com.example.testapp.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var userId:String? = null,
    var firstName:String? = null,
    var lastName:String? = null,
    var email:String? = null
):Parcelable
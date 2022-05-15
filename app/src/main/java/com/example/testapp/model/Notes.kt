package com.example.testapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notes(
    var noteId: String? =null,
    var userId: String? =null,
    var title:String? = null,
    var timestamp: String? = null,
    var body: String? = null,
    var reminder: String? = null,
    var imagePath:String? = null
):Parcelable
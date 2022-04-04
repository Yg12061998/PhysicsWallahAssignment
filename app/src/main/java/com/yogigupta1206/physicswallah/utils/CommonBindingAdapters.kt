package com.yogigupta1206.physicswallah.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.yogigupta1206.physicswallah.R
import com.yogigupta1206.physicswallah.network.model.profile.Profile

@BindingAdapter("subjectQualification")
fun AppCompatTextView.subjectQualification(profile: Profile?) {

    var data = ""

    profile?.subjects?.forEachIndexed { index, subject ->
        data += subject
        if (profile.subjects.size != 1) {
            data += if (profile.subjects.size > index + 1)
                ", "
            else{
                "  \u2022  "
            }
        }else if(profile.subjects.size == 1){
            data += "  \u2022  "
        }
    }

    profile?.qualification?.forEachIndexed { index, it ->
        data += it
        if (profile.qualification.size != 1) {
            if (profile.qualification.size > index + 1)
                data += ", "
        }
    }

    this.text = data
}

@BindingAdapter("setProfileUrl")
fun AppCompatImageView.setProfileUrl(url:String?){
    if(!(url.isNullOrEmpty() || url.isNullOrBlank())){
        Glide.with(this.context).load(url).placeholder(R.drawable.empty_profile).into(this)
    }
}
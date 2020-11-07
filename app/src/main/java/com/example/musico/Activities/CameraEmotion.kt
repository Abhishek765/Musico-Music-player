package com.example.musico.Activities

import android.app.ProgressDialog
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musico.R
import org.json.JSONObject

class CameraEmotion : AppCompatActivity() {
    val jsonobject: JSONObject ?= null
    val jsonobject2: JSONObject ?= null

    val mBitmap: Bitmap ?= null
    var takePicture = false
    val detectionProgress: ProgressDialog ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_emotion)
    }
}
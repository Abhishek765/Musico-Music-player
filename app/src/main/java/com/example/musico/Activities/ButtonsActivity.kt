package com.example.musico.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.musico.Activities.ButtonsActivity.Statified.songList
import com.example.musico.Fragments.MainScreenFragment
import com.example.musico.Fragments.SongPlayingFragment
import com.example.musico.MyApplication
import com.example.musico.R
import com.example.musico.Songs
import kotlinx.android.synthetic.main.activity_buttons.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ButtonsActivity : AppCompatActivity() {
    var mActivity: Activity? = null
    private var isSongPlaying:Boolean ?= null

    object Statified{
        var songList: ArrayList<Songs> ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buttons)
//        songList = getSongsFromPhone()
        this.title = "Home"
        songList = MainScreenFragment.Statified.getSongsList
        Log.e("Inside Button Activity", "onCreate: List:  $songList" )
//
        bt_play_random.setOnClickListener {
            playRandomSong()
//            Toast.makeText(this, "Random is playing", Toast.LENGTH_SHORT).show()
        }
    }

    //    For Emotion Detection
    fun openEmotionActivity(view: View) {
        isSongPlaying = SongPlayingFragment.Statified.mediaplayer?.isPlaying

        if(isSongPlaying == true){
            //Stop the song and remove the notification
            MainActivity.Statified.notificationManager?.cancel(1888)

            SongPlayingFragment.Statified.mediaplayer?.stop()
            SongPlayingFragment.Statified.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        }

        val intent = Intent(this, DemoActivity::class.java)
        startActivity(intent)

    }

    //    Open All songs list
    fun openSongList(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // TODO: 06-11-2020  Play some random song
    fun playRandomSong() {

            var MainIntent = Intent(this, MainActivity::class.java)
            MainIntent.putExtra("RandomSong", "playRandom")
        Toast.makeText(this, "Starting Random Song", Toast.LENGTH_SHORT).show()
            startActivity(MainIntent)
    }

    fun getSongsFromPhone(): ArrayList<Songs>{
        var arrayList = ArrayList<Songs>()
        var contentResolver = mActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTile = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentId, currentTile, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
    }


}
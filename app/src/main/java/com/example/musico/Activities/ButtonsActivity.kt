package com.example.musico.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.example.musico.Fragments.SongPlayingFragment
import com.example.musico.MyApplication
import com.example.musico.R
import com.example.musico.Songs
import kotlinx.android.synthetic.main.activity_buttons.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ButtonsActivity : AppCompatActivity() {
    var mActivity: Activity? = null
    var tempList: ArrayList<String> ?= null
    private var isSongPlaying:Boolean ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buttons)
//        var app = MyApplication()
//        tempList = app.globalSongList
//        bt_play_random.setOnClickListener {
//            playRandomSong(tempList)
//            Log.e("TAG", "onCreate: $tempList" )
//        }

    }

    //    For Emotion Detection
    fun openEmotionActivity(view: View) {
        isSongPlaying = SongPlayingFragment.Statified.mediaplayer?.isPlaying

        if(isSongPlaying == true){
            //Stop the song and remove the notification
            MainActivity.Statified.notificationManager?.cancel(1888)

            SongPlayingFragment.Statified.mediaplayer?.pause()
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
    fun playRandomSong(view: View) {
//    Get the song list from MainsScreenFragment using getSongsFromPhone method

//        MainScreenFragment.listofSongs?.let { songList?.addAll(it) }
            val songList = SongPlayingFragment.Statified.fetchSongs
//        var mainScreenFragment = MainScreenFragment()
//        songList = mainScreenFragment.getSongsFromPhone()
//        Log.e("ButtonsActivity", "SongList Before: $songList" )
//        getSongsFromPhone()
        Log.e("ButtonsActivity", "SongList After: $songList")

        val randomPosition = (0..songList!!.size).random()
/* getting random song */
        val songObject = songList[randomPosition]

        val args = Bundle()
        val songPlayingFragment = SongPlayingFragment()
        args.putString("songArtist", songObject.artist)
        args.putString("path", songObject.songData)
        args.putString("songTitle", songObject.songTitle)
        args.putInt("songId", songObject.songID.toInt() as Int)
        args.putInt("songPosition", randomPosition)
        args.putParcelableArrayList("songData", songList)
        songPlayingFragment.arguments = args

        try {
            if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaplayer?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        (this as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()

    }

    fun rand(start: Int, end: Int): Int {
        require(start <= end){}
        return Random(System.nanoTime()).nextInt(start, end + 1)
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
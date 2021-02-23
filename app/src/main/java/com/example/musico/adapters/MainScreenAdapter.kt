package com.example.musico.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musico.CurrentSongHelper
import com.example.musico.Fragments.HappyFragment
import com.example.musico.Fragments.MainScreenFragment
import com.example.musico.Fragments.SongPlayingFragment
import com.example.musico.R
import com.example.musico.Songs
import com.example.musico.databases.EchoDatabase


class MainScreenAdapter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null


    init {
        this.songDetails = _songDetails
        this.mContext = _context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)

        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var songObject = songDetails?.get(position)
        if (songObject?.songTitle?.contains("AUD")!!) {
            songObject = null
            return
        }
        holder.trackTitle?.text = songObject.songTitle
        holder.trackArtist?.text = songObject.artist

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        try {
//            path = bundleObejct?.getString("path")
            path = songObject.songData
            _songTitle = songObject.songTitle
            _songArtist = songObject.artist
            songId = songObject.songID


        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.emotionMenu?.setOnClickListener {

            //creating a popup menu
            val popup = PopupMenu(mContext, holder.emotionMenu)

            //inflating menu from xml resource
            popup.inflate(R.menu.options_menu)

            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.it_happy -> {

                        //add particular item to happy list
                        if (MainScreenFragment.Statified.happyContent?.checkifIdExistsHappy(songId.toInt()) as Boolean) {
                            Toast.makeText(mContext, "This song is already present song id: ${songId.toInt()}", Toast.LENGTH_SHORT).show()
                            Log.e("Main Screen Adapter", "song path in MainScreen: $path")
                        } else {
                            MainScreenFragment.Statified.happyContent?.storeAsHappy(songId.toInt(), _songArtist, _songTitle, path)
                            Toast.makeText(mContext, "Added successfully Song title: ${_songTitle}", Toast.LENGTH_SHORT).show()
                            Log.e("Main Screen Adapter", "song path in Mainscrenn: $path")

                        }
//                        Log.e("MainScreenAdapter", "onBindViewHolder: SongID: " + songObject?.songID?.toInt())

                    }
                    R.id.it_neutral -> {
                        Toast.makeText(mContext, "Neutral is clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.it_sad -> {
                        //add particular item to happy list
                        if (MainScreenFragment.Statified.sadContent?.checkifIdExistsSad(songId.toInt()) as Boolean) {
                            Toast.makeText(mContext, "This song is already present song id: ${songId.toInt()}", Toast.LENGTH_SHORT).show()
                            Log.e("Main Screen Adapter", "song path in MainScreen: $path")
                        } else {
                            MainScreenFragment.Statified.sadContent?.storeAsSad(songId.toInt(), _songArtist, _songTitle, path)
                            Toast.makeText(mContext, "Added successfully Song title: ${_songTitle}", Toast.LENGTH_SHORT).show()
                            Log.e("Main Screen Adapter", "song path in Mainscrenn: $path")

                        }
                    }
                }
                false
            }
            //displaying the popup
            popup.show()
        }

        holder.contentHolder?.setOnClickListener {

            var args = Bundle()
            val songPlayingFragment = SongPlayingFragment()
            args.putString("songArtist", songObject.artist)
            args.putString("path", songObject.songData)
            args.putString("songTitle", songObject.songTitle)
            args.putInt("songId", songObject.songID.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragment.arguments = args

            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragment")
                    .commit()
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null
        var emotionMenu: TextView? = null

        init {
            trackTitle = view.findViewById<TextView>(R.id.trackTitle)
            trackArtist = view.findViewById<TextView>(R.id.trackArtist)
            contentHolder = view.findViewById<RelativeLayout>(R.id.contentRow)
            emotionMenu = view.findViewById(R.id.tv_emotion_options)
        }

    }

}
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musico.Fragments.HappyFragment
import com.example.musico.Fragments.MainScreenFragment
import com.example.musico.Fragments.SongPlayingFragment
import com.example.musico.R
import com.example.musico.Songs

class HappyAdapter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<HappyAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HappyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }




    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(holder: HappyAdapter.MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist

        var songId: Long = 0

        try {
            if (songObject != null) {
                songId = songObject.songID
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
//        Create drop down to delete the song
        holder.emotionMenu?.setOnClickListener {

            //creating a popup menu
            val popup = PopupMenu(mContext, holder.emotionMenu)

            //inflating menu from xml resource
            popup.inflate(R.menu.option_menu_delete)

            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.it_delete -> {

                        //add particular item to happy list

//                        Log.e("MainScreenAdapter", "onBindViewHolder: SongID: " + songObject?.songID?.toInt())
                        if(HappyFragment.Statified.happyContent?.checkifIdExistsHappy(songId.toInt()) as Boolean){
                            HappyFragment.Statified.happyContent?.deleteHappy(songId.toInt())

                            songDetails?.remove(songObject)
                            notifyItemRemoved(position)
                            Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show()


                        }else{

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
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songId", songObject?.songID?.toInt() as Int)
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
                    .addToBackStack("SongPlayingFragmentHappy")
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
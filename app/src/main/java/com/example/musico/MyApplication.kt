package com.example.musico

import android.app.Application
import android.content.Context
import com.example.musico.Fragments.MainScreenFragment
import com.example.musico.Fragments.SongPlayingFragment

class MyApplication : Application() {
//    var mainScreenFragment = MainScreenFragment()
//var globalSongList = SongPlayingFragment.Statified.fetchSongs

     var globalSongList:ArrayList<String> ?= null

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
        globalSongList?.add("jhasjdj")
        globalSongList?.add("asdlma")
        globalSongList?.add("lasdjkasd")
        globalSongList?.add("kalsdlas")
        globalSongList?.add("kinas")
    }

}

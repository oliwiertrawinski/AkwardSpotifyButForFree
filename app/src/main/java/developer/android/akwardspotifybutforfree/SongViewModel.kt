package developer.android.akwardspotifybutforfree

import androidx.lifecycle.ViewModel

class SongViewModel:ViewModel() {
    var currentSong = 0
    var songs = mutableListOf(
        R.raw.miodowe_lata,
        R.raw.pan_nie_jest_moim_pasterzem,
        R.raw.plebania_remix,
        R.raw.polska_a_b_c,
        R.raw.sorry_polsko,
        R.raw.nie_raj
    )
    fun getCurrent(): Int{
        return songs[currentSong]
    }

    fun getNext(): Int{
        currentSong = if(currentSong + 1 > songs.size -1) 0 else currentSong + 1
        return songs[currentSong]
    }

    fun getPrevious(): Int{
        currentSong = if(currentSong - 1 < 0) songs.size - 1 else currentSong - 1
        return songs[currentSong]
    }

    fun setCurrent(id: Int){
        if(id < 0) currentSong = 0
        else if(id > songs.size) currentSong = songs.size
        else currentSong = id
    }
}
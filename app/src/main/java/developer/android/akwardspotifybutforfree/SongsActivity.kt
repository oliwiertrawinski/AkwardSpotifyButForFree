package developer.android.akwardspotifybutforfree
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import developer.android.akwardspotifybutforfree.databinding.ActivitySongListBinding
import kotlinx.android.synthetic.main.activity_song_list.*


private lateinit var binding: ActivitySongListBinding

class SongList : AppCompatActivity(), SongClickListener {

    private val songs: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SongAdapter(songs, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }
    }

    override fun onClick(id: Int) {
        Log.e("Recycler", id.toString())
        val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("id", id)
        }
        startActivity(intent)
    }
}
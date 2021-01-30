package developer.android.akwardspotifybutforfree



import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import developer.android.akwardspotifybutforfree.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mp: MediaPlayer? = null
    private val songs: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var startId = intent.getIntExtra("id", 1)
        songs.setCurrent(startId)
        controlSong(songs.getCurrent())
        mp?.start()

        binding.apply {
            nextBtn.setOnClickListener {
                if(mp != null){
                    cleanUp()
                    controlSong(songs.getNext())
                    mp?.start()
                }
            }
            previousBtn.setOnClickListener {
                if(mp != null){
                    cleanUp()
                    controlSong(songs.getPrevious())
                    mp?.start()
                }
            }
        }
    }
   

    private fun controlSong(id: Int){
        mp = MediaPlayer.create(applicationContext, id)

        val meta = MediaMetadataRetriever()
        val fileDescription = resources.openRawResourceFd(id)
        meta.setDataSource(
            fileDescription.fileDescriptor,
            fileDescription.startOffset,
            fileDescription.length
        );
        initializeSeekBar()

        binding.apply {

            songTitle.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            songAuthor.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

            if(meta.embeddedPicture != null){
                val image = BitmapFactory.decodeByteArray(meta.embeddedPicture,0, (meta.embeddedPicture)!!.size)
                songCover.load(image){transformations(RoundedCornersTransformation(48f))}
            }
            else songCover.load(R.drawable.no_cover)

            playBtn.setOnClickListener {
                if (mp == null) {
                    mp = MediaPlayer.create(applicationContext, id)
                    initializeSeekBar()
                }
                if (mp?.isPlaying!!) {
                    mp?.pause()
                    playBtn.setBackgroundResource(R.drawable.play)
                } else {
                    mp?.start()
                    playBtn.setBackgroundResource(R.drawable.stop)
                }
            }

            positionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mp?.seekTo(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mp != null){
            cleanUp()
        }
    }

    override fun onStop() {
        super.onStop()
        if(mp != null){
            cleanUp()
        }
    }
    
    private fun initializeSeekBar(){
        binding.apply {
            positionBar.max = mp!!.duration
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        positionBar.progress = mp!!.currentPosition
                        elapsedTimeLabel.text = getInTimeFormat(mp!!.currentPosition)
                        remainingTimeLabel.text = getInTimeFormat(mp!!.duration - mp!!.currentPosition)
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        positionBar.progress = 0
                    }
                }
            }, 0)
        }
    }

    private fun getInTimeFormat(duration: Int): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        );
    }

    private fun cleanUp(){
        if(mp != null){
            binding.apply {
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
                remainingTimeLabel.text = getInTimeFormat(0)
                elapsedTimeLabel.text = getInTimeFormat(0)
                positionBar.progress = 0
                songAuthor.text = ""
                songTitle.text = ""
                positionBar.setOnSeekBarChangeListener(null)
            }
        }
    }
}
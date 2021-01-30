package developer.android.akwardspotifybutforfree

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.util.concurrent.TimeUnit


class SongAdapter(
    private val songs: SongViewModel,
    private val onSongClick: SongClickListener
) : RecyclerView.Adapter<SongAdapter.ListViewHolder>() {

    inner class ListViewHolder(characterView: View) : RecyclerView.ViewHolder(characterView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val id = songs.songs[position]
        val cover = holder.itemView.findViewById<ImageView>(R.id.listItemSongCover)
        val title = holder.itemView.findViewById<TextView>(R.id.listItemTitle)
        val author = holder.itemView.findViewById<TextView>(R.id.listItemAuthor)
        val time = holder.itemView.findViewById<TextView>(R.id.listItemDuration)

        val meta = MediaMetadataRetriever()
        val file = holder.itemView.resources.openRawResourceFd(id)
        meta.setDataSource(
            file.fileDescriptor,
            file.startOffset,
            file.length
        );

        if (meta.embeddedPicture != null) {
            val image = BitmapFactory.decodeByteArray(
                meta.embeddedPicture,
                0,
                (meta.embeddedPicture)!!.size
            )
            cover.load(image)
        } else cover.load(R.drawable.no_cover)

        title.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        author.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        time.text =
            toTime(meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt())

        holder.itemView.setOnClickListener {
            onSongClick.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return songs.songs.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun toTime(duration: Int): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    duration.toLong()
                )
            )
        );
    }
}
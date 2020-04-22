package com.app.chotuve.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.chotuve.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_video_list_item.view.*

class VideoFeedRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<ModelVideo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VideoOnFeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_video_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is VideoOnFeedViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(videoList: List<ModelVideo>){
        items = videoList
    }

    class VideoOnFeedViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val videoThumbnail  = itemView.thumbnail
        val videoTitle = itemView.video_title
        val videoUsername = itemView.video_username
        val videoDate = itemView.video_date

        fun bind(videoPost: ModelVideo){
            videoTitle.setText(videoPost.title)
            videoUsername.setText(videoPost.username)
            videoDate.setText(videoPost.date)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.video_no_thumbnail)
                .error(R.drawable.video_no_thumbnail)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(videoPost.image)
                .into(videoThumbnail)
        }
    }

}
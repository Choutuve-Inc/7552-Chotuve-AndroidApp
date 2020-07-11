package com.app.chotuve.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.chotuve.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_video_list_item.view.*

class VideoFeedRecyclerAdapter(listener: VideoFeedRecyclerAdapter.OnVideoListener) : RecyclerView.Adapter<VideoFeedRecyclerAdapter.VideoOnFeedViewHolder>(){

    private var videoItems: ArrayList<ModelVideo> = ArrayList()
    private var onVideoListener: OnVideoListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):VideoOnFeedViewHolder {
        return VideoOnFeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_video_list_item, parent, false), onVideoListener
        )
    }

    override fun getItemCount(): Int {
        return videoItems.size
    }

    override fun onBindViewHolder(holder: VideoOnFeedViewHolder, position: Int) {
        when(holder){

            is VideoOnFeedViewHolder -> {
                holder.bind(videoItems[position], onVideoListener)
            }
        }
    }

    fun submitList(videoList: ArrayList<ModelVideo>){
        videoItems = videoList
    }

    fun addItem(video: ModelVideo){
        videoItems.add(video)
    }

    class VideoOnFeedViewHolder constructor(itemView: View, onVideoListener: OnVideoListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val videoThumbnail  = itemView.thumbnail
        private val videoTitle = itemView.video_title
        private val videoUsername = itemView.video_username
        private val videoDate = itemView.video_date
        private var listener: OnVideoListener = onVideoListener


        fun bind(videoPost: ModelVideo, onVideoListener: OnVideoListener){
            itemView.setOnClickListener(this)
            videoTitle.text = videoPost.title
            videoUsername.text = videoPost.username
            videoDate.text = videoPost.date
            listener = onVideoListener

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.video_no_thumbnail)
                .error(R.drawable.video_no_thumbnail)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(videoPost.image)
                .into(videoThumbnail)
        }

        override fun onClick(v: View?) {
            listener.onVideoClick(adapterPosition)
        }
    }

    interface OnVideoListener {
        fun onVideoClick(position: Int)
    }

}
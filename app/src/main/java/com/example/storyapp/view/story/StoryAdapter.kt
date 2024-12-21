package com.example.storyapp.view.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.response.ListStoryItem
import com.example.storyapp.view.detail.DetailStoryActivity

class StoryAdapter(private var listStory: List<ListStoryItem>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    // Fungsi untuk mengupdate list
    @SuppressLint("NotifyDataSetChanged")
    fun setStoryList(newList: List<ListStoryItem>) {
        listStory = newList
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.iv_photo)

        //        val iconUser: ImageView = itemView.findViewById(R.id.iconUser)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_description)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        val story = listStory[position]
        holder.tvName.text = story.name
        holder.tvDesc.text = story.description
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.ivPhoto)


        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.ivPhoto, "imageTransition"),
                Pair(holder.tvName, "nameTransition"),
                Pair(holder.tvDesc, "descriptionTransition")
            )

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intent.putExtra("EXTRA_STORY_ID", story.id)
            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount(): Int = listStory.size


}
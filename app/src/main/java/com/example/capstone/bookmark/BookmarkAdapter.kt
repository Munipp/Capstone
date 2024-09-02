package com.example.capstone.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R

class BookmarkAdapter(
    private var bookmarks: List<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {
    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.tvWord)
        val deleteImageView: ImageView = itemView.findViewById(R.id.ivDelete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }
    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val word = bookmarks[position]
        holder.wordTextView.text = word
        holder.deleteImageView.setOnClickListener {
            onDeleteClick(word)
        }
    }
    override fun getItemCount() = bookmarks.size
    fun updateData(newBookmarks: List<String>) {
        bookmarks = newBookmarks
        notifyDataSetChanged()
    }
}
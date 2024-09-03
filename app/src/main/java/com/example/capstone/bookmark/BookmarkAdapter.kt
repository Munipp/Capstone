package com.example.capstone.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.databinding.ItemBookmarkBinding

class BookmarkAdapter(
    private var bookmarks: List<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val word = bookmarks[position]
        holder.binding.tvWord.text = word
        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick(word)
        }
    }

    override fun getItemCount() = bookmarks.size

    fun updateData(newBookmarks: List<String>) {
        bookmarks = newBookmarks
        notifyDataSetChanged()
    }
}

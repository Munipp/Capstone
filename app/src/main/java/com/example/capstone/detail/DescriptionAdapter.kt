package com.example.capstone.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Constants
import com.example.capstone.Meaning
import com.example.capstone.databinding.DescriptionListBinding

class DescriptionAdapter(private var descriptionList: List<Meaning>) : RecyclerView.Adapter<DescriptionAdapter.DescriptionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val binding = DescriptionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DescriptionViewHolder(binding)
    }
    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        holder.bind(descriptionList[position])
    }
    override fun getItemCount(): Int = descriptionList.size
    fun updateData(newMeanings: List<Meaning>) {
        descriptionList = newMeanings
        notifyDataSetChanged()
    }
    inner class DescriptionViewHolder(private val binding: DescriptionListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meaning: Meaning) {
            binding.apply {
                tvSpeech.text = meaning.partOfSpeech
                tvDefinitions.text = meaning.definitions.joinToString("\n\n") { definition ->
                    "${meaning.definitions.indexOf(definition) + 1}. ${definition.definition}"
                }
                toggleVisibility(tvSynonymsHeader, tvSynonyms, meaning.synonyms,
                    Constants.SYNONYMS_HEADER
                )
                toggleVisibility(tvAntonymsHeader, tvAntonyms, meaning.antonyms,
                    Constants.ANTONYMS_HEADER
                )
            }
        }
        private fun toggleVisibility(headerTextView: View, contentTextView: View, items: List<String>, headerText: String) {
            if (items.isEmpty()) {
                headerTextView.visibility = View.GONE
                contentTextView.visibility = View.GONE
            } else {
                headerTextView.visibility = View.VISIBLE
                contentTextView.visibility = View.VISIBLE

                if (headerTextView is TextView) {
                    headerTextView.text = headerText
                }

                if (contentTextView is TextView) {
                    contentTextView.text = items.joinToString(", ")
                }
            }
        }
    }
}
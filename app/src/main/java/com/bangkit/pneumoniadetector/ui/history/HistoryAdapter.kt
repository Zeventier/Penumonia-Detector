package com.bangkit.pneumoniadetector.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.databinding.ItemRowHistoryBinding
import com.bumptech.glide.Glide

class HistoryAdapter :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listHistory: MutableList<History> = mutableListOf()

    fun addItem(history: List<History>) {
        listHistory.addAll(history)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowHistoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(listHistory[position].photoUrl)
            .into(holder.binding.imageViewItemImage)
        holder.binding.apply {
            textViewAccuracy.text = listHistory[position].probability
            textViewName.text = listHistory[position].name
            textViewDatecreated.text = listHistory[position].createdAt
            textViewPneumoniatype.text = listHistory[position].prediction
        }

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listHistory[position])
        }
    }

    override fun getItemCount(): Int = listHistory.size

    class ViewHolder(val binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(historyDetail: History)
    }
}
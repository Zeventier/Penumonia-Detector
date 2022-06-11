package com.bangkit.pneumoniadetector.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.pneumoniadetector.data.remote.response.ResultItem
import com.bangkit.pneumoniadetector.databinding.ItemRowHistoryBinding

class ResultListAdapter:
    PagingDataAdapter<ResultItem, ResultListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        var data: ResultItem?
        fun onItemClicked()
    }

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    class MyViewHolder(private val binding: ItemRowHistoryBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(data: ResultItem){
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .into(binding.imageViewItemImage)
                binding.textViewAccuracy.text = data.accuracy
                binding.textViewName.text = data.name
                binding.textViewDatecreated.text = data.createdAt
                binding.textViewPneumoniatype.text = data.pneumoniaType
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null)
            holder.bind(data)

        holder.itemView.setOnClickListener {
            val item: ResultItem? = getItem(position)
            onItemClickCallback.data = item
            onItemClickCallback.onItemClicked()
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResultItem>() {
            override fun areItemsTheSame(oldItem: ResultItem, newItem: ResultItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ResultItem, newItem: ResultItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
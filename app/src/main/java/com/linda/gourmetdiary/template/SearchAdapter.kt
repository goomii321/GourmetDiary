package com.linda.gourmetdiary.template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.ItemSearchBinding

class SearchAdapter(private val onClickListener: OnClickListener): ListAdapter<Diary,SearchAdapter.SearchListViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (temDiary: Diary) -> Unit) {
        fun onClick(temDiary: Diary) = clickListener(temDiary)
    }

    class SearchListViewHolder(private var binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list: Diary, onClickListener: OnClickListener) {
            binding.data = list
            binding.root.setOnClickListener {
                onClickListener.onClick(list)
            }

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAdapter.SearchListViewHolder {
        return SearchListViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,onClickListener)
    }
}

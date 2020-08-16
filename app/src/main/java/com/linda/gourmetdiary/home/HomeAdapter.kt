package com.linda.gourmetdiary.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.databinding.ItemHomeListBinding
import com.linda.gourmetdiary.util.BREAKFAST
import com.linda.gourmetdiary.util.DINNER
import com.linda.gourmetdiary.util.LUNCH

class HomeAdapter(private val onClickListener: OnClickListener): ListAdapter<Diary, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (diary: Diary) -> Unit) {
        fun onClick(diary: Diary) = clickListener(diary)
    }

    class HomeViewHolder(private var binding: ItemHomeListBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary, onClickListener: OnClickListener) {

            binding.diary = diary
            binding.root.setOnClickListener { onClickListener.onClick(diary) }
            when (diary.type) {
                BREAKFAST -> binding.backgroundColor.setBackgroundResource(R.drawable.corners_breakfast)
                LUNCH -> binding.backgroundColor.setBackgroundResource(R.drawable.corner_lunch)
                DINNER -> binding.backgroundColor.setBackgroundResource(R.drawable.corner_dinner)
                else -> binding.backgroundColor.setBackgroundResource(R.drawable.corners_dessert)
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

        private const val ITEM_VIEW_TYPE_HOME = 0x00
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HOME -> HomeViewHolder(ItemHomeListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HomeViewHolder -> {
                holder.bind((getItem(position) as Diary),onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_HOME
    }
}
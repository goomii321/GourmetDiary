package com.linda.gourmetdiary.diarys

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diarys4Day
import com.linda.gourmetdiary.databinding.ItemDiaryCheckDayBinding
import com.linda.gourmetdiary.databinding.ItemDiarylistBinding

class DiarysAdapter( val viewModel: DiarysViewModel, private val onClickListener: OnClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (data: String) -> Unit) {
        fun onClick(data: String) = clickListener(data)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_HEADER = 0
        private const val ITEM_VIEW_DIARY_LIST = 1
    }

    class TitleViewHolder(private var binding: ItemDiaryCheckDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, onClickListener: OnClickListener) {
            binding.title = title
            binding.root.setOnClickListener { onClickListener.onClick(title) }

            binding.executePendingBindings()
        }
    }

    class DiaryViewHolder(private var binding: ItemDiarylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diarys4Day: Diarys4Day, viewModel: DiarysViewModel) {
            binding.diarys = diarys4Day
            binding.viewModel = viewModel
            binding.recyclerDiary.adapter = DailyItemAdapter(DailyItemAdapter.OnClickListener{
                viewModel.navigateToDetail(it)
            })

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_HEADER -> TitleViewHolder(
                ItemDiaryCheckDayBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false))

            ITEM_VIEW_DIARY_LIST -> DiaryViewHolder(
                ItemDiarylistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is TitleViewHolder -> {
                holder.bind((getItem(position) as DataItem.Title).title,onClickListener)
            }
            is DiaryViewHolder -> {
                holder.bind((getItem(position) as DataItem.Diarys).diarys4Day, viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.Title -> ITEM_VIEW_HEADER
            is DataItem.Diarys -> ITEM_VIEW_DIARY_LIST
        }
    }
}

sealed class DataItem {

    abstract val id: Long

    data class Title(val title: String): DataItem() {
        override val id: Long = -1
    }
    data class Diarys(val diarys4Day: Diarys4Day): DataItem() {
        override val id: Long = -2
    }
}



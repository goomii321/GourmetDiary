package com.linda.gourmetdiary.diarys

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Diary
import com.linda.gourmetdiary.data.Diarys4Day
import com.linda.gourmetdiary.data.source.DiaryRepository
import com.linda.gourmetdiary.databinding.ItemDiaryOnedayBinding
import com.linda.gourmetdiary.databinding.ItemDiarylistBinding
import com.linda.gourmetdiary.ext.getVmFactory

class DiarysAdapter( val viewModel: DiarysViewModel) :
    ListAdapter<Diarys4Day, RecyclerView.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Diarys4Day>() {
        override fun areItemsTheSame(oldItem: Diarys4Day, newItem: Diarys4Day): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Diarys4Day, newItem: Diarys4Day): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_LV1 = 0
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
            ITEM_VIEW_TYPE_LV1 -> DiaryViewHolder(
                ItemDiarylistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is DiaryViewHolder -> {
                holder.bind((getItem(position) as Diarys4Day), viewModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_LV1
    }

}



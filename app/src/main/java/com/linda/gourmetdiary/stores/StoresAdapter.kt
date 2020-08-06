package com.linda.gourmetdiary.stores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linda.gourmetdiary.data.Store
import com.linda.gourmetdiary.databinding.ItemStoresListBinding
import com.linda.gourmetdiary.util.NONE

class StoresAdapter(private val onClickListener: OnClickListener): ListAdapter<Store, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (store: Store) -> Unit) {
        fun onClick(store: Store) = clickListener(store)
    }

    class StoresViewHolder(private var binding: ItemStoresListBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Store, onClickListener: OnClickListener) {

            binding.store = store
            binding.root.setOnClickListener { onClickListener.onClick(store) }
            if (store.storeBranch == NONE){
                binding.storeItemBranch.visibility = View.GONE
            }

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Store>() {
        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_STORES = 0x00
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_STORES -> StoresViewHolder(ItemStoresListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is StoresViewHolder -> {
                holder.bind((getItem(position) as Store),onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_STORES
    }
}
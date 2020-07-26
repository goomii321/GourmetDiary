package com.linda.gourmetdiary.stores

import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.linda.gourmetdiary.DiaryApplication
import com.linda.gourmetdiary.R
import com.linda.gourmetdiary.data.Stores
import com.linda.gourmetdiary.databinding.ItemStoresListBinding

class StoresAdapter(private val onClickListener: OnClickListener): ListAdapter<Stores, RecyclerView.ViewHolder>(DiffCallback) {

    class OnClickListener(val clickListener: (store: Stores) -> Unit) {
        fun onClick(store: Stores) = clickListener(store)
    }

    class StoresViewHolder(private var binding: ItemStoresListBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(store: Stores, onClickListener: OnClickListener) {

            binding.store = store
            binding.root.setOnClickListener { onClickListener.onClick(store) }
            if (store.storeBranch == "無"){
                binding.storeItemBranch.visibility = View.GONE
            }

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Stores>() {
        override fun areItemsTheSame(oldItem: Stores, newItem: Stores): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Stores, newItem: Stores): Boolean {
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
                holder.bind((getItem(position) as Stores),onClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_STORES
    }
}
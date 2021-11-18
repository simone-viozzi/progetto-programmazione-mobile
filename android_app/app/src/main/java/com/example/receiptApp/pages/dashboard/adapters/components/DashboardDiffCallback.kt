package com.example.receiptApp.pages.dashboard.adapters.components

import androidx.recyclerview.widget.DiffUtil
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import timber.log.Timber


class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardDataModel>()
{
    override fun areItemsTheSame(oldItem: DashboardDataModel, newItem: DashboardDataModel): Boolean
    {
        Timber.d("oldItem -> $oldItem")
        Timber.d("newItem -> $newItem")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DashboardDataModel, newItem: DashboardDataModel): Boolean
    {
        return oldItem == newItem
    }
}
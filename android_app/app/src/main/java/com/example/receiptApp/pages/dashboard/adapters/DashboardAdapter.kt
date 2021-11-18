package com.example.receiptApp.pages.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.receiptApp.R
import com.example.receiptApp.databinding.DashboardElementLabelBinding
import com.example.receiptApp.databinding.DashboardElementTestBigBinding
import com.example.receiptApp.databinding.DashboardElementTestSquareBinding
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import com.example.receiptApp.pages.dashboard.adapters.components.DashboardDiffCallback
import com.example.receiptApp.pages.dashboard.adapters.components.DashboardViewHolder


class DashboardAdapter: ListAdapter<DashboardDataModel, DashboardViewHolder>(DashboardDiffCallback())
{
    var onLongClickListener: (() -> Unit)? = null
    var onClickListener: ((DashboardDataModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder
    {
        return when (viewType)
        {
            R.layout.dashboard_element_label -> DashboardViewHolder.LabelViewHolder(
                DashboardElementLabelBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onLongClickListener,
                onClickListener
            )
            R.layout.dashboard_element_test_big -> DashboardViewHolder.TestBigViewHolder(
                DashboardElementTestBigBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onLongClickListener,
                onClickListener
            )
            R.layout.dashboard_element_test_square -> DashboardViewHolder.TestSquareViewHolder(
                DashboardElementTestSquareBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onLongClickListener,
                onClickListener
            )
            else -> throw IllegalStateException("the view type in the RecyclerView is wrongggg! ")
        }
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int)
    {
        when (holder)
        {
            //is DashboardViewHolder.TestViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Test)
            is DashboardViewHolder.TestBigViewHolder -> holder.bind(getItem(position) as DashboardDataModel.TestBig)
            is DashboardViewHolder.TestSquareViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Square)
            is DashboardViewHolder.LabelViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Label)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position))
        {
            //is DashboardDataModel.Test -> R.layout.dashboard_element_label
            is DashboardDataModel.TestBig -> R.layout.dashboard_element_test_big
            is DashboardDataModel.Square -> R.layout.dashboard_element_test_square
            is DashboardDataModel.Label -> R.layout.dashboard_element_label
        }
    }

}
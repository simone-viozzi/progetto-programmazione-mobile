package com.example.receiptApp.pages.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.receiptApp.R
import com.example.receiptApp.databinding.*
import com.example.receiptApp.pages.dashboard.DashboardDataModel
import com.example.receiptApp.pages.dashboard.adapters.components.DashboardDiffCallback
import com.example.receiptApp.pages.dashboard.adapters.components.DashboardViewHolder
import com.example.receiptApp.pages.graphs.GraphAdapter


class DashboardAdapter: ListAdapter<DashboardDataModel, DashboardViewHolder>(DashboardDiffCallback())
{
    // the callbacks are common for every element and every type
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
            R.layout.graphs_cake_card -> DashboardViewHolder.CakeCardViewHolder(
                GraphsCakeCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onLongClickListener,
                onClickListener
            )
            R.layout.graphs_histogram_card -> DashboardViewHolder.HistogramViewHolder(
                GraphsHistogramCardBinding.inflate(
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
            is DashboardViewHolder.LabelViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Label)
            is DashboardViewHolder.CakeCardViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Pie)
            is DashboardViewHolder.HistogramViewHolder -> holder.bind(getItem(position) as DashboardDataModel.Histogram)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (getItem(position))
        {
            is DashboardDataModel.Label -> R.layout.dashboard_element_label
            is DashboardDataModel.Pie -> R.layout.graphs_cake_card
            is DashboardDataModel.Histogram -> R.layout.graphs_histogram_card
        }
    }

}
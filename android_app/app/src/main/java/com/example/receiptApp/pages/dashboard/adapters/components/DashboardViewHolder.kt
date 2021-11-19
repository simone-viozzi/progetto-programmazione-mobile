package com.example.receiptApp.pages.dashboard.adapters.components

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.receiptApp.databinding.*
import com.example.receiptApp.pages.dashboard.DashboardDataModel


sealed class DashboardViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root)
{
    class LabelViewHolder(
        private val binding: DashboardElementLabelBinding,
        private val onLongClickListener: (() -> Unit)?,
        private val onClickListener: ((DashboardDataModel) -> Unit)?
    ) : DashboardViewHolder(binding)
    {

        fun bind(holder: DashboardDataModel.Label)
        {
            (binding.cardView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = binding.cardView.isBig

            binding.textViewTag.text = holder.name
            binding.textViewExpense.text = holder.value.toString()

            binding.cardView.setOnLongClickListener {
                onLongClickListener?.invoke()
                true
            }

            binding.cardView.setOnClickListener {
                onClickListener?.invoke(holder)
            }
        }
    }

    //class TestBigViewHolder(
    //    private val binding: DashboardElementTestBigBinding,
    //    private val onLongClickListener: (() -> Unit)?,
    //    private val onClickListener: ((DashboardDataModel) -> Unit)?
    //) : DashboardViewHolder(binding)
    //{
    //    fun bind(holder: DashboardDataModel.TestBig)
    //    {
    //        (binding.cardView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = binding.cardView.isBig
//
    //        binding.textView1.text = holder.id.toString()
//
    //        binding.cardView.setOnLongClickListener {
    //            onLongClickListener?.invoke()
    //            true
    //        }
//
    //        binding.cardView.setOnClickListener {
    //            onClickListener?.invoke(holder)
    //        }
    //    }
    //}

    //class TestSquareViewHolder(
    //    private val binding: DashboardElementTestSquareBinding,
    //    private val onLongClickListener: (() -> Unit)?,
    //    private val onClickListener: ((DashboardDataModel) -> Unit)?
    //) : DashboardViewHolder(binding)
    //{
//
    //    fun bind(holder: DashboardDataModel.Square)
    //    {
    //        (binding.cardView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = binding.cardView.isBig
//
    //        binding.textView1.text = holder.id.toString()
//
    //        binding.cardView.setOnLongClickListener {
    //            onLongClickListener?.invoke()
    //            true
    //        }
//
    //        binding.cardView.setOnClickListener {
    //            onClickListener?.invoke(holder)
    //        }
    //    }
    //}

    class CakeCardViewHolder(
        private val binding: GraphsCakeCardBinding,
        private val onLongClickListener: (() -> Unit)?,
        private val onClickListener: ((DashboardDataModel) -> Unit)?
    ) : DashboardViewHolder(binding)
    {
        init
        {
            //binding.cardView.widthRatio = 1F
            binding.cardView.isBig = true
        }


        fun bind(holder: DashboardDataModel.Pie)
        {
            with(binding)
            {
                (cardView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = cardView.isBig

                textView.text = holder.name
                aaChartView.aa_drawChartWithChartModel(holder.aaChartModel)

                cardView.setOnLongClickListener {
                    onLongClickListener?.invoke()
                    true
                }

                cardView.setOnClickListener {
                    onClickListener?.invoke(holder)
                }
            }
        }
    }

    class HistogramViewHolder(
        private val binding: GraphsHistogramCardBinding,
        private val onLongClickListener: (() -> Unit)?,
        private val onClickListener: ((DashboardDataModel) -> Unit)?
    ) : DashboardViewHolder(binding)
    {
        init
        {
            //binding.cardView.widthRatio = 1F
            binding.cardView.isBig = true
        }


        fun bind(holder: DashboardDataModel.Histogram)
        {
            with(binding)
            {
                (cardView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = cardView.isBig

                textView.text = holder.name
                aaChartView.aa_drawChartWithChartModel(holder.aaChartModel)

                cardView.setOnLongClickListener {
                    onLongClickListener?.invoke()
                    true
                }

                cardView.setOnClickListener {
                    onClickListener?.invoke(holder)
                }
            }
        }
    }
}
package com.example.receiptApp.pages.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView


class DragManageAdapter(
    var viewModel: HomeViewModel,
) : ItemTouchHelper.Callback()
{
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int
    {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags,0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean
    {
        val from = viewHolder.bindingAdapterPosition
        val to = target.bindingAdapterPosition

        (viewHolder.itemView as? MaterialCardView)?.isDragged = true

        viewModel.swapItems(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    {
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    )
    {
        (viewHolder.itemView as? MaterialCardView)?.isDragged = false
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }
}
package com.example.receiptApp.pages.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

/**
 * Drag manage adapter
 *  will set and menage frag and drop and swipe
 *
 * @property viewModel
 * @constructor Create empty Drag manage adapter
 */
class DragManager(
    var viewModel: DashboardViewModel,
) : ItemTouchHelper.Callback()
{
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int
    {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.START or  ItemTouchHelper.END

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean
    {
        // this doesn't seems to work
        (viewHolder.itemView as? MaterialCardView)?.isDragged = true

        // when the user overlap an element with another, we swap the positions of the two element
        val from = viewHolder.bindingAdapterPosition
        val to = target.bindingAdapterPosition

        viewModel.swapItems(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    {
        // when the user swipe an element, we delete the element
        viewModel.removeItemFromDashBoard(viewHolder.absoluteAdapterPosition)
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
        // this doesn't seems to work
        (viewHolder.itemView as? MaterialCardView)?.isDragged = false
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }
}
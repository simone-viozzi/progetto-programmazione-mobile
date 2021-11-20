package com.example.receiptApp.layouts


import android.content.Context
import android.util.AttributeSet
import com.example.receiptApp.R
import com.example.receiptApp.px
import com.google.android.material.card.MaterialCardView

/**
 * override of a cardView to make it use the aspect ratio we impose from the XML
 * it has two custom attributes:
 *  -> widthRatio -> this is used to determine the height from the width: height = width / widthRatio
 *  -> isBig -> this is used in the dashboard to make the cardView occupy all the available rows
 */
class FixedAspectCardView(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs)
{
    var widthRatio: Float = 0F
    var isBig: Boolean = false

    init
    {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FixedAspectCardView,
            0, 0
        ).apply {

            try
            {
                widthRatio = getFloat(R.styleable.FixedAspectCardView_aspectRatio, 0F)
                isBig = getBoolean(R.styleable.FixedAspectCardView_isBig, false)
            } finally
            {
                recycle()
            }
        }
    }

    /**
     * with this override i can set the height respecting the aspect ratio
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        // if the aspect ratio was not set, revert to default behavior
        if (widthRatio == 0F)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // if the card is a square i need to compensate the padding of the smaller views to make it of
        // the same height
        var heightValue = width / widthRatio
        if (widthRatio == 1.0F && !isBig)
        {
            heightValue += 16.px
        }

        val height = MeasureSpec.makeMeasureSpec(heightValue.toInt(), MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, height)
    }
}
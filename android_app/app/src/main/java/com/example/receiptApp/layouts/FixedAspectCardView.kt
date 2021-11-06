package com.example.receiptApp.layouts


import android.content.Context
import android.util.AttributeSet
import com.example.receiptApp.R
import com.example.receiptApp.px
import com.google.android.material.card.MaterialCardView


class FixedAspectCardView(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs)
{
    private var widthRatio: Float = 2.0F
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
                widthRatio = getFloat(R.styleable.FixedAspectCardView_aspectRatio, 2.0F)
                isBig = getBoolean(R.styleable.FixedAspectCardView_isBig, false)
            } finally
            {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        var heightValue = width / widthRatio
        if (widthRatio == 1.0F && !isBig)
        {
            heightValue += 16.px
        }

        val height = MeasureSpec.makeMeasureSpec(heightValue.toInt(), MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, height)
    }
}
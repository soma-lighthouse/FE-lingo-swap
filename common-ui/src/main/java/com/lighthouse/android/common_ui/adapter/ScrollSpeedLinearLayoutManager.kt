package com.lighthouse.android.common_ui.adapter

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class ScrollSpeedLinearLayoutManager(context: Context?, private val factor: Float) :
    LinearLayoutManager(context) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int,
    ) {
        var linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView!!.context) {
                override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                    return this@ScrollSpeedLinearLayoutManager.computeScrollVectorForPosition(
                        targetPosition
                    )
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return (displayMetrics?.densityDpi
                        ?: DisplayMetrics.DENSITY_DEFAULT).let { densityDpi ->
                        factor / densityDpi
                    }
                }

            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }


}
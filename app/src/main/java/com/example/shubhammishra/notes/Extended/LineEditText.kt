package com.example.shubhammishra.notes.Extended

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.EditText

class LineEditText(context: Context,attributeSet: AttributeSet): EditText(context,attributeSet) {
    lateinit var rect: Rect
    lateinit var paint: Paint
    init {
        rect= Rect()
        paint= Paint()
        paint.style=Paint.Style.FILL_AND_STROKE
        paint.color= Color.BLACK
    }

    override fun onDraw(canvas: Canvas?) {
        val height=height
        val lheight=lineHeight
        var count=height/lheight
        if(lineCount>count)
        {
            count=lineCount
        }
        val r=rect
        val mpaint=paint
        var baseline=getLineBounds(0,r)
        for(i in 0..count-1)
        {
            canvas!!.drawLine(r.left.toFloat(), (baseline+1).toFloat(), r.right.toFloat(), (baseline+1).toFloat(),mpaint)
            baseline+=lineHeight
        }
        super.onDraw(canvas)
    }
}
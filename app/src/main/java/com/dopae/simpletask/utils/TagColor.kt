package com.dopae.simpletask.utils

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.dopae.simpletask.R

enum class TagColor( val color: Int) {
    RED(R.color.tagColorRED),
    GREEN(R.color.tagColorGREEN),
    YELLOW(R.color.tagColorYELLOW),
    PURPLE(R.color.tagColorPURPLE),
    BLUE(R.color.tagColorBLUE),
    CIAN(R.color.tagColorCIAN),
    PINK(R.color.tagColorPINK),
    ORANGE(R.color.tagColorORANGE),
    ROSE(R.color.tagColorROSE),
    LILAS(R.color.tagColorLILAS);


    fun getColor(context: Context):Int{
        return ContextCompat.getColor(context, this.color)
    }

    fun getColorStateList(context: Context):ColorStateList{
        return ColorStateList.valueOf(getColor(context))
    }

}

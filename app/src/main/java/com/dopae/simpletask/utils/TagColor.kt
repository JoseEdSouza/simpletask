package com.dopae.simpletask.utils

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat

enum class TagColor(val color: Int) {
    RED(0xfc6f6a),
    GREEN(0x9bfc90),
    YELLOW(0xfcd890),
    PURPLE(0xcaaffa),
    BLUE(0xa5bafa),
    CIAN(0x83fcde),
    PINK(0xffabf5),
    ORANGE(0xfca16d),
    ROSE(0xf29d85),
    LILAS(0xc48fd9);

    fun getColorStateList(): ColorStateList {
        return ColorStateList.valueOf(this.color)
    }
}

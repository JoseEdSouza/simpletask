package com.dopae.simpletask.component

import android.content.Context
import android.content.res.ColorStateList
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.ColorTagBinding
import com.dopae.simpletask.utils.TagColor

class ColorTagComponent(
    private val context: Context,
    binding: ColorTagBinding,
) {
    private var selectedPos: Int? = null
    private val root = binding.root

    private val colorMap: Map<Int, Pair<View, TextView>> = mapOf(
        R.color.tagColorRED to Pair(binding.itemTagColorOption1, binding.txtViewColorName1),
        R.color.tagColorGREEN to Pair(binding.itemTagColorOption2, binding.txtViewColorName2),
        R.color.tagColorYELLOW to Pair(binding.itemTagColorOption3, binding.txtViewColorName3),
        R.color.tagColorPURPLE to Pair(binding.itemTagColorOption4, binding.txtViewColorName4),
        R.color.tagColorBLUE to Pair(binding.itemTagColorOption5, binding.txtViewColorName5),
        R.color.tagColorCIAN to Pair(binding.itemTagColorOption6, binding.txtViewColorName6),
        R.color.tagColorPINK to Pair(binding.itemTagColorOption7, binding.txtViewColorName7),
        R.color.tagColorORANGE to Pair(binding.itemTagColorOption8, binding.txtViewColorName8),
        R.color.tagColorROSE to Pair(binding.itemTagColorOption9, binding.txtViewColorName9),
        R.color.tagColorLILAS to Pair(binding.itemTagColorOption10, binding.txtViewColorName10) // Add this line if you have a 10th color option
    )

    val info: TagColor?
        get() = selectedPos?.let { TagColor.values()[it] }

    fun setInfo(tagColor: TagColor) {
        val pos = tagColor.ordinal
        selectedPos = pos
        colorMap[tagColor.color]?.let { (option, name) ->
            name.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.black)
                )
            )
        }
    }

    fun init() {
        colorMap.values.forEachIndexed { i, (color, _) ->
            color.setOnClickListener { select(i) }
        }
    }

    private fun select(pos: Int) {
        TransitionManager.beginDelayedTransition(root, AutoTransition())
        selectedPos?.let {
            if (pos != it) {
                flipColorPosition(it, pos)
                selectedPos = pos
            }
        } ?: run {
            colorMap.values.elementAt(pos).second.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.black)
                )
            )
            selectedPos = pos
        }
    }

    private fun flipColorPosition(old: Int, new: Int) {
        TransitionManager.beginDelayedTransition(root, AutoTransition())
        colorMap.values.elementAt(new).second.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.black)
            )
        )
        colorMap.values.elementAt(old).second.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.gray_1)
            )
        )
    }

}
package com.dopae.simpletask.controller

import android.content.Context
import android.content.res.ColorStateList
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.R
import com.dopae.simpletask.databinding.ColorTagBinding
import com.dopae.simpletask.utils.TagColor

class ColorTagController(
    private val context: Context,
    binding: ColorTagBinding,
) {
    private var selectedPos: Int? = null
    private val root = binding.root
    private val colors = listOf(
        binding.itemTagColorOption1,
        binding.itemTagColorOption2,
        binding.itemTagColorOption3,
        binding.itemTagColorOption4,
        binding.itemTagColorOption5,
        binding.itemTagColorOption6,
        binding.itemTagColorOption7,
        binding.itemTagColorOption8,
        binding.itemTagColorOption9,
    )
    private val colorsName = listOf(
        binding.txtViewColorName1,
        binding.txtViewColorName2,
        binding.txtViewColorName3,
        binding.txtViewColorName4,
        binding.txtViewColorName5,
        binding.txtViewColorName6,
        binding.txtViewColorName7,
        binding.txtViewColorName8,
        binding.txtViewColorName9,
    )

    val info: TagColor?
        get() = selectedPos?.let { TagColor.values()[it] }


    fun init() {
        colors.forEachIndexed { i, color ->
            color.setOnClickListener { select(i) }
        }
    }

    private fun select(pos: Int) {
        TransitionManager.beginDelayedTransition(root, AutoTransition())
        selectedPos?.let {
            if (pos != it) {
                flipColorPosition(it,pos)
                selectedPos = pos
            }
        } ?: run {
            colorsName[pos].setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.black)
                )
            )
            selectedPos = pos
        }

    }

    private fun flipColorPosition(old: Int, new: Int) {
        TransitionManager.beginDelayedTransition(root, AutoTransition())
        colorsName[new].setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.black)
            )
        )
        colorsName[old].setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.gray_1)
            )
        )
    }
}
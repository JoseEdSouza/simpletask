package com.dopae.simpletask.controller

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import com.dopae.simpletask.R
import com.dopae.simpletask.dao.TagDAOImp
import com.dopae.simpletask.databinding.TagCardTaskBinding
import com.dopae.simpletask.model.Task
import com.dopae.simpletask.utils.TagColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CardTagController(
    private val context: Context,
    private val binding: TagCardTaskBinding
) {
    private val selectedTags = mutableSetOf<Int>()
    private val card = binding.root
    private val addBtn = binding.imgBtnAddTag
    private val cardAdd = binding.cardViewAddTag
    private val tagView = binding.imgViewTag
    private val tagLayout = binding.linearLayoutTags
    private val hint = binding.txtViewAddTagHint
    private var task: Task? = null
    private var readOnly = false
    private val dao = TagDAOImp.getInstance()


    fun init() {
        if (readOnly) {
            addBtn.visibility = View.GONE
            task?.also {
                if (it.numTags == 0) {
                    hint.visibility = View.VISIBLE
                    tagLayout.removeAllViews()
                }
                else {
                    hint.visibility = View.GONE
                    selectedTags.addAll(it.tags)
                    changeState()
                }

            }
        } else {
            cardAdd.setOnClickListener { openTagPicker() }
        }

    }

    val info: Set<Int>
        get() = selectedTags

    fun setInfo(tags: Collection<Int>) {
        selectedTags.clear()
        selectedTags.addAll(tags)
        changeState()
    }

    fun setReadOnly(task: Task): CardTagController {
        this.task = task
        readOnly = true
        return this
    }

    val isEmpty: Boolean
        get() = selectedTags.isEmpty()

    fun setWriteRead(): CardTagController {
        this.task = null
        readOnly = false
        return this
    }

    private fun openTagPicker() {
        val tagNames = dao.getAll().map { it.name }.toTypedArray()
        val checkedItems = dao.getAll().map { it.id in selectedTags }.toBooleanArray()
        val picker = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setTitle(R.string.addTag)
        when (dao.size()) {
            0 -> picker.setMessage(R.string.noTagsAvaliable)
            else -> picker.setMultiChoiceItems(tagNames, checkedItems) { _, index, checked ->
                if (checked)
                    selectedTags.add(dao.getByPosition(index).id)
                else if (index in selectedTags)
                    selectedTags.remove(dao.getByPosition(index).id)
            }
                .setPositiveButton("OK") { _, _ -> changeState() }
        }
        picker.show()
    }

    private fun changeState() {
        TransitionManager.beginDelayedTransition(card, AutoTransition())
        tagLayout.removeAllViews()
        if (selectedTags.size > 0)
            hint.visibility = View.GONE
        else
            hint.visibility = View.VISIBLE
        selectedTags.forEach {
            val tagColor = dao.get(it)?.color
            tagColor?.let {tg ->
                val view = ImageView(context)
                with(tagView) {
                    view.layoutParams = layoutParams
                    view.scaleType = scaleType
                    view.adjustViewBounds = adjustViewBounds
                }
                view.setImageResource(R.drawable.full_circle)
                view.imageTintList = tg.getColorStateList(context)
                view.visibility = View.VISIBLE
                tagLayout.addView(view)
            }

        }

    }
}
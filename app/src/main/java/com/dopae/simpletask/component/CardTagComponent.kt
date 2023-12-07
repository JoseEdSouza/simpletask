package com.dopae.simpletask.component

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import com.dopae.simpletask.R
import com.dopae.simpletask.dao.TagDAOFirebase
import com.dopae.simpletask.databinding.TagCardTaskBinding
import com.dopae.simpletask.model.Tag
import com.dopae.simpletask.model.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CardTagComponent(
    private val context: Context,
    private val binding: TagCardTaskBinding
) {
    private var dao = TagDAOFirebase.getInstance()
    private var tagList: List<Tag> = emptyList()
    private val selectedTags = mutableSetOf<String>()
    private val card = binding.root
    private val addBtn = binding.imgBtnAddTag
    private val cardAdd = binding.cardAddTag
    private val tagView = binding.imgViewTag
    private val tagLayout = binding.linearLayoutTags
    private val hint = binding.txtViewAddTagHint
    private var task: Task? = null
    private var readOnly = false
    private val handler = CoroutineExceptionHandler { context, _ ->
        context.cancel()
    }
    private val scope = CoroutineScope(Dispatchers.IO + handler)
    private val fetchTagList = scope.launch(start = CoroutineStart.LAZY) { tagList = dao.getAll() }

    init {
        fetchTagList.start()
    }

    fun init() {
        if (readOnly) {
            addBtn.visibility = View.GONE
            task?.let {
                if (it.numTags == 0) {
                    hint.visibility = View.VISIBLE
                    tagLayout.removeAllViews()
                } else {
                    hint.visibility = View.GONE
                    selectedTags.clear()
                    selectedTags.addAll(it.tags)
                    changeState()
                }
            }
        } else {
            cardAdd.setOnClickListener { openTagPicker() }
            addBtn.setOnClickListener { openTagPicker() }
        }

    }

    val info: Set<String>
        get() = selectedTags

    fun setInfo(tags: Collection<String>) {
        selectedTags.clear()
        selectedTags.addAll(tags)
        changeState()
    }

    fun setReadOnly(task: Task): CardTagComponent {
        this.task = task
        readOnly = true
        return this
    }

    val isEmpty: Boolean
        get() = selectedTags.isEmpty()

    fun setWriteRead(): CardTagComponent {
        this.task = null
        readOnly = false
        return this
    }

    private fun openTagPicker() {
        val tagNames = tagList.map { it.name }.toTypedArray()
        val checkedItems = tagList.map { it.id in selectedTags }.toBooleanArray()
        val picker = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setTitle(R.string.addTag)
        if (tagList.isEmpty())
            picker.setMessage(R.string.noTagsAvaliable)
        else
            picker.setMultiChoiceItems(tagNames, checkedItems) { _, index, checked ->
                if (checked)
                    selectedTags.add(tagList[index].id)
                else if (tagList[index].id in selectedTags)
                    selectedTags.remove(tagList[index].id)
            }.setPositiveButton("OK") { _, _ -> changeState() }
        picker.show()
    }

    private fun changeState() {
        TransitionManager.beginDelayedTransition(card, AutoTransition())
        tagLayout.removeAllViews()
        if (selectedTags.size > 0)
            hint.visibility = View.GONE
        else{
            hint.visibility = View.VISIBLE
            return
        }
        val selectedTagsList = tagList.filter { it.id in selectedTags }
        selectedTagsList.forEach {
            val tagColor = it.color
            tagColor.let { tg ->
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
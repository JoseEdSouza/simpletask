package com.dopae.simpletask.controller

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View.OnClickListener
import androidx.fragment.app.FragmentManager
import com.dopae.simpletask.databinding.CardsLayoutTaskBinding

class CardTaskController(
    private val binding: CardsLayoutTaskBinding,
    supportFragmentManager: FragmentManager
) {
    val cardTime = CardTimeTaskController(binding.cardTimeAddTask, supportFragmentManager)
    val cardLocal =
        CardLocalTaskController(binding.cardLocalAddTask, supportFragmentManager)

    private var lastClickedCard: CardController? = null

    fun init() {
        cardTime.init()
        cardLocal.init()
        cardTime.setOnClickListener { cardClicked(cardTime) }
        cardLocal.setOnClickListener { cardClicked(cardLocal) }
    }


    private fun cardClicked(clickedCard: CardController) {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        lastClickedCard?.let {
            if (it == clickedCard) {
                clickedCard.changeState()
            } else {
                clickedCard.changeState()
                if (it.isActivated)
                    it.changeState()
                lastClickedCard = clickedCard
            }

        } ?: run {
            clickedCard.changeState()
            lastClickedCard = clickedCard
        }
    }
}



package com.pkapps.ui.domain.dict

enum class QuestionDifficulty(private val value: Int) {
    TRIVIAL(1),
    EASY(2),
    MEDIUM(3),
    HARD(4);

    companion object {
        fun QuestionDifficulty.next() = QuestionDifficulty.values().find { it.value == value + 1 }
    }
}
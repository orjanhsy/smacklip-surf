package com.example.myapplication.model.conditions

import com.example.myapplication.R

enum class ConditionStatus(val description: String, val surfBoard: Int, val value: Int) {
    GREAT("Utmerket", R.drawable.greenboard, 0),
    DECENT("Greit", R.drawable.yellowboard, 1),
    POOR("Dårlig", R.drawable.redboard, 2),
    BLANK("Uvisst", R.drawable.blankboard, 3)
}
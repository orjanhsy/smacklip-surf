package com.example.myapplication.model.conditions

import com.example.myapplication.R

enum class ConditionStatus(val description: String, val surfBoard: Int) {
    GREAT("Utmerket", R.drawable.greenboard),
    DECENT("Greit", R.drawable.yellowboard),
    POOR("Dårlig", R.drawable.redboard),
    BLANK("", R.drawable.spm)
}
package no.uio.ifi.in2000.team8.model.conditions

import no.uio.ifi.in2000.team8.R

enum class ConditionStatus(val description: String, val surfBoard: Int, val value: Int) {
    GREAT("Utmerket", R.drawable.greenboard, 0),
    DECENT("Greit", R.drawable.yellowboard, 1),
    POOR("Dårlig", R.drawable.redboard, 2),
    BLANK("", R.drawable.blankboard, 3)
}
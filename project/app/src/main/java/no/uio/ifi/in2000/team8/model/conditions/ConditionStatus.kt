package no.uio.ifi.in2000.team8.model.conditions

import no.uio.ifi.in2000.team8.R

enum class ConditionStatus(val description: String, val surfBoard: Int, val value: Int) {
    GREAT("Utmerket", R.drawable.gront_brett, 0),
    DECENT("Greit", R.drawable.gult_brett, 1),
    POOR("Dårlig", R.drawable.rodt_brett, 2),
    BLANK("", R.drawable.gjennomsiktig_brett, 3)
}
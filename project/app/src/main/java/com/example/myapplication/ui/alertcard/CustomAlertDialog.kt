package com.example.myapplication.ui.AlertCard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun <T> CustomAlertDialog(
    time: String,
    title: String,
    message: String,
    actionText: String,
    warningIcon: Int,
    data: T?,
    showAlert: MutableState<Boolean>,
    action: (T) -> Unit
) {
    Dialog(
        onDismissRequest = { showAlert.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CustomAlert(
            time = time,
            title = title,
            message = message,
            actionText = actionText,
            warningIcon = warningIcon,
            data = data,
            showAlert = showAlert,
            //actionWithValue = action, action = null
        )
    }
}

@Composable
fun CustomAlertDialog(
    time: String,
    title: String,
    message: String,
    actionText: String,
    warningIcon: Int,
    showAlert: MutableState<Boolean>,
    action: () -> Unit
) {
    Dialog(
        onDismissRequest = { showAlert.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CustomAlert(
            time = time,
            title = title,
            message = message,
            actionText = actionText,
            warningIcon = warningIcon,
            data = null,
            showAlert = showAlert,
           // actionWithValue = null, action = action
        )
    }
}
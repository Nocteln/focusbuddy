package com.nocteln.app.service

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun openOverlaySettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        "package:${context.packageName}".toUri()
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(intent)
}
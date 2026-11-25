package com.nocteln.app.ui.settings

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nocteln.app.R
import com.nocteln.app.service.hasUsageAccess
import com.nocteln.app.service.openUsageAccessSettings

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    var hasUsageAccess by remember { mutableStateOf(hasUsageAccess(context = context)) }

    LifecycleResumeEffect(Unit) {
        hasUsageAccess = hasUsageAccess(context)

        onPauseOrDispose {  }
    }

    Text(text = stringResource(R.string.settings_title))
    Button(
        onClick = { openUsageAccessSettings(context) }
    ) {
        if (hasUsageAccess) {
            Text("Already got the perms")
        }
        else
        {
            Text("Give access")
        }
    }
}
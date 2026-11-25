package com.nocteln.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nocteln.app.R
import com.nocteln.app.service.hasOverlayPermission
import com.nocteln.app.service.hasUsageAccess
import com.nocteln.app.service.openOverlaySettings
import com.nocteln.app.service.openUsageAccessSettings
import com.nocteln.app.ui.home.DEFAULT_STARTING_MINUTES
import com.nocteln.app.ui.theme.PlantFocusTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    distractingAppsCount: Int = 3,                    // TODO: real value from DistractingAppsRepository (step 4c)
    defaultSessionMinutes: Int = DEFAULT_STARTING_MINUTES // TODO: real value once it is saved
) {
    val context = LocalContext.current

    var usageGranted by remember { mutableStateOf(hasUsageAccess(context)) }
    var overlayGranted by remember { mutableStateOf(hasOverlayPermission(context)) }

    // TODO: these toggles are not saved yet (they reset when you leave the screen)
    var notifySessionEnd by remember { mutableStateOf(true) }
    var vibrateOnDanger by remember { mutableStateOf(true) }
    var dailyReminder by remember { mutableStateOf(false) }

    // Re-check the permissions each time we come back from the system settings
    LifecycleResumeEffect(Unit) {
        usageGranted = hasUsageAccess(context)
        overlayGranted = hasOverlayPermission(context)
        onPauseOrDispose { }
    }

    SettingsContent(
        distractingAppsCount = distractingAppsCount,
        defaultSessionMinutes = defaultSessionMinutes,
        notifySessionEnd = notifySessionEnd,
        vibrateOnDanger = vibrateOnDanger,
        dailyReminder = dailyReminder,
        usageGranted = usageGranted,
        overlayGranted = overlayGranted,
        onBack = onBack,
        onOpenDistractingApps = { /* TODO: screen at step 4c */ },
        onOpenDefaultDuration = { /* TODO */ },
        onNotifySessionEndChange = { notifySessionEnd = it },
        onVibrateOnDangerChange = { vibrateOnDanger = it },
        onDailyReminderChange = { dailyReminder = it },
        onEnableUsageAccess = { openUsageAccessSettings(context) },
        onEnableOverlay = { openOverlaySettings(context) },
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    distractingAppsCount: Int,
    defaultSessionMinutes: Int,
    notifySessionEnd: Boolean,
    vibrateOnDanger: Boolean,
    dailyReminder: Boolean,
    usageGranted: Boolean,
    overlayGranted: Boolean,
    onBack: () -> Unit,
    onOpenDistractingApps: () -> Unit,
    onOpenDefaultDuration: () -> Unit,
    onNotifySessionEndChange: (Boolean) -> Unit,
    onVibrateOnDangerChange: (Boolean) -> Unit,
    onDailyReminderChange: (Boolean) -> Unit,
    onEnableUsageAccess: () -> Unit,
    onEnableOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        SettingsTopBar(onBack = onBack)

        SectionTitle(stringResource(R.string.settings_section_sessions))
        SettingsCard {
            NavigationRow(
                title = stringResource(R.string.settings_distracting_apps),
                value = pluralStringResource(
                    R.plurals.settings_apps_active, distractingAppsCount, distractingAppsCount
                ),
                onClick = onOpenDistractingApps
            )
            RowDivider()
            NavigationRow(
                title = stringResource(R.string.settings_default_duration),
                value = stringResource(R.string.home_minutes, defaultSessionMinutes),
                onClick = onOpenDefaultDuration
            )
        }

        SectionTitle(stringResource(R.string.settings_section_notifications))
        SettingsCard {
            SwitchRow(
                title = stringResource(R.string.settings_notify_session_end),
                checked = notifySessionEnd,
                onCheckedChange = onNotifySessionEndChange
            )
            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_vibrate_on_danger),
                checked = vibrateOnDanger,
                onCheckedChange = onVibrateOnDangerChange
            )
            RowDivider()
            SwitchRow(
                title = stringResource(R.string.settings_daily_reminder),
                checked = dailyReminder,
                onCheckedChange = onDailyReminderChange
            )
        }

        SectionTitle(stringResource(R.string.settings_section_permissions))
        SettingsCard {
            PermissionRow(
                title = stringResource(R.string.settings_usage_access),
                description = stringResource(R.string.settings_usage_access_description),
                granted = usageGranted,
                onEnable = onEnableUsageAccess
            )
            RowDivider()
            PermissionRow(
                title = stringResource(R.string.settings_overlay),
                description = stringResource(R.string.settings_overlay_description),
                granted = overlayGranted,
                onEnable = onEnableOverlay
            )
        }

        Text(
            text = stringResource(R.string.settings_privacy_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )
    }
}

/** Back arrow + screen title. */
@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.settings_back),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

/** Small label above each card ("Sessions", "Notifications"...). */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp)
    )
}

/** White rounded card that groups rows together. */
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

/** Thin line between two rows of the same card. */
@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/** Row that opens another screen: title, current value, chevron. */
@Composable
private fun NavigationRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Row with a switch. The whole row is tappable, not just the switch. */
@Composable
private fun SwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Switch)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        // onCheckedChange = null: the row already handles the click
        Switch(checked = checked, onCheckedChange = null)
    }
}

/** Permission row: "Enabled" pill when granted, "Enable" button otherwise. */
@Composable
private fun PermissionRow(
    title: String,
    description: String,
    granted: Boolean,
    onEnable: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 62.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            // The explanation only matters while the permission is missing
            if (!granted) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        if (granted) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = stringResource(R.string.settings_permission_enabled),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        } else {
            Button(
                onClick = onEnable,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(stringResource(R.string.settings_permission_enable))
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun SettingsMissingPermissionPreview() {
    PlantFocusTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsContent(
                distractingAppsCount = 4,
                defaultSessionMinutes = 45,
                notifySessionEnd = true,
                vibrateOnDanger = true,
                dailyReminder = false,
                usageGranted = true,
                overlayGranted = false,
                onBack = {},
                onOpenDistractingApps = {},
                onOpenDefaultDuration = {},
                onNotifySessionEndChange = {},
                onVibrateOnDangerChange = {},
                onDailyReminderChange = {},
                onEnableUsageAccess = {},
                onEnableOverlay = {}
            )
        }
    }
}
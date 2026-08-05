/*
 * Copyright (c) 2026 Cyber Defense Africa.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.updater

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

/**
 * Numero de build interne de CETTE APK. A incrementer a chaque release publiee,
 * en phase avec le champ "versionCode" de kdodo-version.json (si le JSON annonce
 * un versionCode strictement superieur, l'utilisateur se voit proposer la MAJ).
 */
private const val CURRENT_APK_VERSION = 3

private const val VERSION_URL = "https://link.cda.tg/kdodo-version.json"
private const val TIMEOUT_MS = 6000

private data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val notes: String,
)

/**
 * Verification silencieuse de mise a jour (distribution hors store, sur link.cda.tg).
 * Echoue sans bruit si le serveur est injoignable (hors reseau, lab down, etc.).
 */
@Composable
fun AppUpdateChecker() {
    val uriHandler = LocalUriHandler.current
    var update by remember { mutableStateOf<UpdateInfo?>(null) }
    LaunchedEffect(Unit) {
        val info = fetchUpdateInfo()
        if (info != null && info.versionCode > CURRENT_APK_VERSION && info.apkUrl.startsWith("https://")) {
            update = info
        }
    }
    update?.let { info ->
        ConfirmationDialog(
            title = "Mise a jour disponible",
            content = buildString {
                append("La version ")
                append(info.versionName.ifBlank { "la plus recente" })
                append(" de Link est disponible.")
                if (info.notes.isNotBlank()) {
                    append("\n\n")
                    append(info.notes)
                }
            },
            submitText = "Telecharger",
            cancelText = "Plus tard",
            onSubmitClick = {
                update = null
                runCatching { uriHandler.openUri(info.apkUrl) }
            },
            onDismiss = { update = null },
        )
    }
}

private suspend fun fetchUpdateInfo(): UpdateInfo? = withContext(Dispatchers.IO) {
    try {
        val connection = (URL(VERSION_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            requestMethod = "GET"
        }
        try {
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            UpdateInfo(
                versionCode = json.getInt("versionCode"),
                versionName = json.optString("versionName", ""),
                apkUrl = json.getString("apkUrl"),
                notes = json.optString("notes", ""),
            )
        } finally {
            connection.disconnect()
        }
    } catch (throwable: Throwable) {
        Timber.tag("AppUpdateChecker").d(throwable, "Update check failed")
        null
    }
}

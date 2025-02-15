package io.github.saidooubella.sash.run

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MainScreenState(
    val text: TextFieldValue = TextFieldValue(),
    val diagnostics: ImmutableList<String> = persistentListOf(),
    val output: String = "",
)

package io.github.saidooubella.sash.run

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saidooubella.sash.compiler.diagnostics.DiagnosticsReporter
import saidooubella.sash.compiler.diagnostics.formattedMessage
import saidooubella.sash.evaluator.Environment
import saidooubella.sash.evaluator.Evaluator
import saidooubella.sash.evaluator.createNativeFunction
import saidooubella.sash.evaluator.values.UnitValue
import saidooubella.sash.compiler.input.MutableIntInput
import saidooubella.sash.compiler.input.map
import saidooubella.sash.compiler.input.observer
import saidooubella.sash.compiler.input.provider.StringProvider
import saidooubella.sash.compiler.parser.Parse
import saidooubella.sash.compiler.parser.context.ParserContext
import saidooubella.sash.compiler.refiner.Refine
import saidooubella.sash.compiler.refiner.context.RefinerContext
import saidooubella.sash.compiler.refiner.nodes.Program
import saidooubella.sash.compiler.refiner.symbols.PrintLn
import saidooubella.sash.compiler.span.MutablePositionBuilder
import saidooubella.sash.compiler.tokens.RawTokensProvider
import saidooubella.sash.compiler.tokens.TokenizerContext
import saidooubella.sash.compiler.tokens.TokensProvider
import javax.inject.Inject

@HiltViewModel
internal class MainScreenModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private var runJob: Job? = null

    private val environment = Environment().apply {
        createNativeFunction(PrintLn) { args ->
            _state.update { it.copy(output = it.output + args[0].toString() + "\n") }
            UnitValue
        }
    }

    fun setText(newText: TextFieldValue) {
        _state.update { it.copy(text = newText) }
    }

    fun runSnippet() {
        runJob?.cancel()
        runJob = viewModelScope.launch {
            val (diagnostics, program) = compile(state.value.text.text)
            _state.update { it.copy(diagnostics = diagnostics, output = "") }
            if (diagnostics.isEmpty()) eval(program)
        }
    }

    private fun eval(program: Program) {
        try {
            Evaluator(program, environment)
        } catch (e: Exception) {
            _state.update { it.copy(output = "Runtime Error: ${e.message ?: "Unknown error"}") }
        }
    }
}

private suspend fun compile(source: String) = withContext(Dispatchers.IO) {

    val positionBuilder = MutablePositionBuilder()
    val diagnostics = DiagnosticsReporter("<playground>")

    val program = MutableIntInput(StringProvider(source))
        .observer { old, new -> positionBuilder.advance(old, new) }
        .map { input -> RawTokensProvider(input, TokenizerContext(positionBuilder, diagnostics)) }
        .map { input -> TokensProvider(input) }
        .use { input -> Parse(input, ParserContext(diagnostics)) }
        .let { program -> Refine(program, RefinerContext(diagnostics)) }

    diagnostics.build().map { it.formattedMessage }.toImmutableList() to program
}

package io.github.saidooubella.sash.run

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.saidooubella.sash.run.ui.theme.SashRunTheme
import kotlinx.collections.immutable.ImmutableList

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      enableEdgeToEdge()
      super.onCreate(savedInstanceState)

      setContent {
         SashRunTheme {
            HomeScreen()
         }
      }
   }
}

private enum class Visibility { Diagnostics, Output, None }

@Composable
private fun HomeScreen(model: MainScreenModel = viewModel()) {
   val state by model.state.collectAsStateWithLifecycle()

   Scaffold(
      modifier = Modifier
         .fillMaxSize()
         .imePadding(),
      topBar = { ShellUIToolbar(model::runSnippet) },
   ) { innerPadding ->
      Column(modifier = Modifier.padding(innerPadding)) {
         EditorInputText(
            text = state.text,
            onTextChange = model::setText,
            modifier = Modifier.weight(1f),
         )
         AnimatedContent(
            label = "bottom-section-visibility",
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            targetState = when {
               state.output.isNotEmpty() -> Visibility.Output
               state.diagnostics.isNotEmpty() -> Visibility.Diagnostics
               else -> Visibility.None
            },
         ) { visibility ->
            if (visibility == Visibility.Diagnostics) {
               Diagnostics(state.diagnostics, Modifier.fillMaxHeight(.4f))
            } else if (visibility == Visibility.Output) {
               Output(state, Modifier.fillMaxHeight(.4f))
            }
         }
      }
   }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShellUIToolbar(onRun: () -> Unit) {
   TopAppBar(
      title = { Text(text = "Playground") },
      actions = {
         IconButton(onClick = onRun) {
            Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Run snippet")
         }
      },
   )
}

@Composable
private fun Output(state: MainScreenState, modifier: Modifier = Modifier) {
   HorizontalDivider()
   Text(
      text = state.output,
      modifier = modifier
         .fillMaxWidth()
         .verticalScroll(rememberScrollState())
   )
}

@Composable
private fun Diagnostics(diagnostics: ImmutableList<String>, modifier: Modifier = Modifier) {
   HorizontalDivider()
   LazyColumn(modifier = modifier.fillMaxWidth()) {
      items(diagnostics) { diagnostic ->
         Text(text = diagnostic)
      }
   }
}

@Composable
private fun EditorInputText(
   text: TextFieldValue,
   onTextChange: (TextFieldValue) -> Unit,
   modifier: Modifier = Modifier,
) {
   BasicTextField(
      modifier = modifier
         .fillMaxSize()
         .padding(16.dp),
      value = text,
      onValueChange = onTextChange,
      textStyle = TextStyle(
         color = MaterialTheme.colorScheme.onBackground,
         fontFamily = FontFamily.Monospace,
         fontSize = 16.sp,
      ),
      cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
   )
}

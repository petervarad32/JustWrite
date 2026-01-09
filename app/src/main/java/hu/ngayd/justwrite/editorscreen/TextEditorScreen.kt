package hu.ngayd.justwrite.editorscreen

import android.util.Log
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.ngayd.justwrite.R
import hu.ngayd.justwrite.rememberImeState
import hu.ngayd.justwrite.ui.theme.OrchidBranch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TextEditorScreen(
	private val pr: TextEditorPresenter,
	private val onSave: () -> Unit,
	private val onSaveAs: () -> Unit,
	private val onOpen: () -> Unit,
	private val onOpenSettings: () -> Unit,
) {

	@Composable
	fun Screen() {
		val coroutineScope = rememberCoroutineScope()
		val imeState = rememberImeState()
		val scrollState = rememberScrollState()
		val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
		val text = pr.textFlow.collectAsState()

		//scrolling to cursor on keyboard opening
		LaunchedEffect(imeState.value) {
			val cursorPosition = textLayoutResult.value?.getCursorRect(text.value.selection.start)?.top?.toInt()
			if (imeState.value && cursorPosition != null) {
				Log.d("oks", "max " + scrollState.maxValue.toString())
				Log.d("oks", "scroll ${cursorPosition - 1000}")
				val scrollPosition = cursorPosition - 1000
				coroutineScope.launch {
					delay(100)
					if (scrollPosition < scrollState.maxValue) scrollState.animateScrollTo(scrollPosition, SpringSpec())
					else scrollState.animateScrollTo(scrollState.maxValue)
				}
			}
		}
		//scrolling to cursor while printing
		LaunchedEffect(text.value.selection.end) {
			val cursorPosition = textLayoutResult.value?.getCursorRect(text.value.selection.end)?.top?.toInt()
			if (cursorPosition != null) {
				val scrollPosition = cursorPosition - 1000
				coroutineScope.launch {
					if (scrollPosition < scrollState.maxValue) scrollState.animateScrollTo(scrollPosition, SpringSpec())
					else scrollState.animateScrollTo(scrollState.maxValue)
				}
			}
		}

		val interactionSource = remember { MutableInteractionSource() }
		val isFocused = interactionSource.collectIsFocusedAsState()

		//setting placeholder or no placeholder if text is empty
		LaunchedEffect(isFocused.value) {
			if (isFocused.value && text.value.text == pr.placeholder.text) pr.onTextChange(TextFieldValue(""))
			if (!isFocused.value && text.value.text == "") pr.onTextChange(pr.placeholder)
		}

		val keyboardHeight = with(LocalDensity.current) {
			WindowInsets.ime.getBottom(LocalDensity.current).toDp()
		}

		Scaffold(
			modifier = Modifier
				.fillMaxSize(),
			topBar = {
				TopBar(
					modifier = Modifier,
					appBarText = pr.appBarText,
				)
			}
		) { innerPadding ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(
						top = innerPadding.calculateTopPadding(),
						start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
						end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
						bottom = if (imeState.value) keyboardHeight else innerPadding.calculateBottomPadding()
					)
					.verticalScroll(scrollState)
			) {
				BasicTextField(
					modifier = Modifier
						.fillMaxWidth()
						.fillMaxHeight()
						.padding(horizontal = 16.dp),
					value = text.value,
					textStyle = TextStyle(
						fontSize = 18.sp
					),
					onValueChange = {
						if (it.text != text.value.text) {
							pr.restartEraseTimer()
						}
						pr.onTextChange(it)
					},
					onTextLayout = { layoutResult ->
						textLayoutResult.value = layoutResult
					},
					interactionSource = interactionSource
				)
			}
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	private fun TopBar(
		modifier: Modifier,
		appBarText: MutableState<String>,
	) {
		TopAppBar(
			modifier = modifier,
			title = {
				Text(
					appBarText.value,
					color = Color.White
				)
			},
			actions = {
				IconButton(onClick = {
					onOpen()
				}) {
					Image(
						painter = painterResource(id = R.drawable.open_from_cataloque),
						contentDescription = "Open From Folder"
					)
				}
				IconButton(onClick = {
					onSaveAs()
				}) {
					Image(
						painter = painterResource(id = R.drawable.download),
						contentDescription = "Save File As"
					)
				}
				IconButton(onClick = {
					onSave()
				}) {
					Image(
						painter = painterResource(id = R.drawable.save),
						contentDescription = "Save File"
					)
				}
				IconButton(onClick = {
					onOpenSettings()
				}) {
					Image(
						painter = painterResource(id = R.drawable.settings),
						contentDescription = "Settings"
					)
				}
			},
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = OrchidBranch,
			)
		)
	}
}
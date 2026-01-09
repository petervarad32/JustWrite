package hu.ngayd.justwrite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.lifecycleScope
import hu.ngayd.justwrite.editorscreen.TextEditorPresenter
import hu.ngayd.justwrite.editorscreen.TextEditorScreen
import hu.ngayd.justwrite.repository.SettingsRepository
import hu.ngayd.justwrite.repository.TextRepository
import hu.ngayd.justwrite.ui.theme.JustWriteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

	private val createTextFileLauncher =
		registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
			SessionState.setDialogClosed()
			uri ?: return@registerForActivityResult

			lifecycleScope.launch(Dispatchers.IO) {
				writeToFile(uri, TextRepository.text.value.text, contentResolver)
			}
			TextRepository.uri = uri
			Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
		}

	private val openTextFileLauncher =
		registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
			if (uri != null) {
				lifecycleScope.launch(Dispatchers.IO) {
					val text = readFromFile(uri, contentResolver)
					TextRepository.text.value = TextFieldValue(text)
				}
				TextRepository.uri = uri
			} else {
				// resetting timer only if no file opened
				// otherwise letting user to read the file
				SessionState.setDialogClosed()
			}
		}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		SettingsRepository.init(this)

		setContent {
			val isSettingsMode = remember { mutableStateOf(false) }
			val presenter = remember { TextEditorPresenter() }

			JustWriteTheme {
				TextEditorScreen(
					pr = presenter,
					onSaveAs = {
						SessionState.setDialogOpened()
						createTextFileLauncher.launch("${TextRepository.text.value.text.take(15)}.txt")
					},
					onOpen = {
						SessionState.setDialogOpened()
						openTextFileLauncher.launch(arrayOf("text/plain"))
					},
					onSave = {
						SessionState.setDialogOpened()
						val uri = TextRepository.uri
						if (uri != null) {
							writeToFile(uri, TextRepository.text.value.text, contentResolver)
							Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
						} else createTextFileLauncher.launch("${TextRepository.text.value.text.take(15)}.txt")
					},
					onOpenSettings = {
						SessionState.setDialogOpened()
						isSettingsMode.value = true
					},
				).Screen()
				if (isSettingsMode.value)
					SettingsScreen(
						onBack = {
							SessionState.setDialogClosed()
							isSettingsMode.value = false
						},
					).Screen()
			}
		}
	}
}
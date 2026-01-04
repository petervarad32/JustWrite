package hu.ngayd.justwrite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.input.TextFieldValue
import hu.ngayd.justwrite.editorscreen.TextEditorPresenter
import hu.ngayd.justwrite.editorscreen.TextEditorScreen
import hu.ngayd.justwrite.ui.theme.JustWriteTheme


class MainActivity : ComponentActivity() {

	private val createTextFileLauncher =
		registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
			uri ?: return@registerForActivityResult

			writeToFile(uri, TextRepository.text.value.text, contentResolver)
			TextRepository.uri = uri
			Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
		}

	private val openTextFileLauncher =
		registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
			uri ?: return@registerForActivityResult

			/*contentResolver.takePersistableUriPermission(
				uri,
				Intent.FLAG_GRANT_READ_URI_PERMISSION
			)*/

			val text = readFromFile(uri, contentResolver)
			TextRepository.text.value = TextFieldValue(text)
			TextRepository.uri = uri
		}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			JustWriteTheme {
				TextEditorScreen(
					presenter = TextEditorPresenter(),
					onSaveAs = { createTextFileLauncher.launch("${TextRepository.text.value.text.take(15)}.txt") },
					onOpen = { openTextFileLauncher.launch(arrayOf("text/plain")) },
					onSave = {
						val uri = TextRepository.uri
						if (uri != null) {
							writeToFile(uri, TextRepository.text.value.text, contentResolver)
							Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
						} else createTextFileLauncher.launch("${TextRepository.text.value.text.take(15)}.txt")
					}
				).Screen()
			}
		}
	}
}
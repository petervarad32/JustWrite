package hu.ngayd.justwrite.editorscreen

import androidx.compose.ui.text.input.TextFieldValue
import hu.ngayd.justwrite.repository.TextRepository

class TextEditorPresenter {

	fun onTextChange(newValue: TextFieldValue) {
		TextRepository.text.value = newValue
	}

	fun getValue() = TextRepository.text
	fun getText() = TextRepository.text.value.text
	fun getSelection() = TextRepository.text.value.selection
	fun getUri() = TextRepository.uri
}
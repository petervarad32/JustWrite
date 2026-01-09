package hu.ngayd.justwrite.repository

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow

object TextRepository {
	val text = MutableStateFlow(TextFieldValue("Start your story..."))
	var uri: Uri? = null
}
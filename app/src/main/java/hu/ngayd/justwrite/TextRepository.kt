package hu.ngayd.justwrite

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue


object TextRepository {
	var text = mutableStateOf(TextFieldValue("Start your story..."))
	var uri: Uri? = null

}
package hu.ngayd.justwrite

import android.content.ContentResolver
import android.net.Uri

fun writeToFile(uri: Uri, text: String, contentResolver: ContentResolver) {
	contentResolver.openOutputStream(uri)?.use { out ->
		out.write(text.toByteArray(Charsets.UTF_8))
	}
}

fun readFromFile(uri: Uri, contentResolver: ContentResolver): String {
	return contentResolver.openInputStream(uri)?.use { input ->
		input.bufferedReader(Charsets.UTF_8).readText()
	}.orEmpty()
}
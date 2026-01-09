package hu.ngayd.justwrite

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionState {
	private val _isSystemDialogOpen = MutableStateFlow(false)
	val isSystemDialogOpen: StateFlow<Boolean> = _isSystemDialogOpen

	fun setDialogOpened() {
		_isSystemDialogOpen.value = true
	}

	fun setDialogClosed() {
		_isSystemDialogOpen.value = false
	}
}
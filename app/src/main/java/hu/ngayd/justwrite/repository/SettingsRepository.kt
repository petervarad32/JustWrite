package hu.ngayd.justwrite.repository

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.content.edit

object SettingsRepository {

	private const val PREFS_NAME = "justwrite_settings"
	private const val KEY_ERASE_DELAY = "erase_delay_seconds"
	private const val DEFAULT_ERASE_DELAY = 45
	private const val DEFAULT_BEFORE_TIMER_DELAY = 30

	private lateinit var prefs: android.content.SharedPreferences

	var eraseDelaySeconds = mutableIntStateOf(DEFAULT_ERASE_DELAY)
		private set
	var beforeTimerSeconds = mutableIntStateOf(DEFAULT_BEFORE_TIMER_DELAY)
		private set
	var afterTimerSeconds = mutableIntStateOf(DEFAULT_BEFORE_TIMER_DELAY)
		private set

	fun init(context: Context) {
		prefs = context.applicationContext
			.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

		eraseDelaySeconds.intValue =
			prefs.getInt(KEY_ERASE_DELAY, DEFAULT_ERASE_DELAY)
		setBeforeAfterTimerTime()
	}

	fun setEraseDelay(value: Int) {
		eraseDelaySeconds.intValue = value
		setBeforeAfterTimerTime()
		prefs.edit {
			putInt(KEY_ERASE_DELAY, value)
		}
	}

	private fun setBeforeAfterTimerTime() {
		when (eraseDelaySeconds.intValue) {
			EraseDelay.SEC_30.seconds -> {
				beforeTimerSeconds.intValue = 15
				afterTimerSeconds.intValue = 15
			}

			EraseDelay.SEC_45.seconds -> {
				beforeTimerSeconds.intValue = 30
				afterTimerSeconds.intValue = 15
			}

			EraseDelay.SEC_60.seconds -> {
				beforeTimerSeconds.intValue = 30
				afterTimerSeconds.intValue = 30
			}
		}
	}
}

enum class EraseDelay(val seconds: Int) {
	SEC_30(30),
	SEC_45(45),
	SEC_60(60)
}
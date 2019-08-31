package creative.endless.growing.allergychecker

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    companion object {
        fun newInstance(
        ): SettingsFragment = SettingsFragment()

    }
}
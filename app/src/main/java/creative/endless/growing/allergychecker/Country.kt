package creative.endless.growing.allergychecker

import android.content.Context
import android.widget.ImageView
import androidx.preference.PreferenceManager
import java.util.Locale

class Country {
    companion object {
        fun setCorrectFlag(country: ImageView, context: Context) {
            when(getCountry(context)){
                en -> country.setImageDrawable(context.getDrawable(R.drawable.en))
                sv -> country.setImageDrawable(context.getDrawable(R.drawable.sv))
            }
        }

        var en = 0
        var sv = 1
        var country = "Country"
        fun getLocale(context: Context): Locale {
            return when(getCountry(context)){
                sv -> Locale("sv","SE")
                else -> Locale.ENGLISH
            }
        }

        private fun getCountry(context: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(this.country, 0)
        }
    }

}

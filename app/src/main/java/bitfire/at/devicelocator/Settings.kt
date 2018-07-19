/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package bitfire.at.devicelocator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

class Settings(val context: Context) {

    private val settings = PreferenceManager.getDefaultSharedPreferences(context)!!

    var password: String
        get() {
            return settings.getString(context.getString(R.string.settings_key_password), null) ?: {
                // first call, generate password
                val pwd = UUID.randomUUID().toString().substring(0, 5)
                password = pwd
                pwd
            }()
        }
        set(value) = settings.edit()
            .putString(context.getString(R.string.settings_key_password), value)
            .apply()


    fun registerOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            settings.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterOnChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            settings.unregisterOnSharedPreferenceChangeListener(listener)

}
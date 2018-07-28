/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.bitfire.devicelocator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log

object Util {

    const val TAG = "DeviceLocator"

    private val smsManager = SmsManager.getDefault()

    fun sendSms(recipient: String, text: String) {
        Log.i(Util.TAG, "Sending SMS to $recipient: $text")
        smsManager.sendTextMessage(recipient, null, text, null, null)
    }

    fun viewHomepage(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gitlab.com/bitfireAT/device-locator"))
        if (intent.resolveActivity(context.packageManager) != null)
            context.startActivity(intent)
    }

}
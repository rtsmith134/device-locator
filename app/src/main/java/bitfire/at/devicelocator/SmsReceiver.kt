/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package bitfire.at.devicelocator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import bitfire.at.devicelocator.Util.sendSms

class SmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            return

        val message = Telephony.Sms.Intents.getMessagesFromIntent(intent).first()

        val settings = Settings(context)
        val password = settings.password
        if (!message.messageBody.startsWith("$password ")) {
            Log.d(Util.TAG, "Received SMS that doesn't start with the password")
            return
        }

        val command = message.messageBody.substring(password.length + 1).trim()
        Log.i(Util.TAG, "Received SMS with password. Command: $command")

        when (command.toLowerCase()) {
             "send location" -> {
                 val serviceIntent = Intent(context, LocatorService::class.java)
                 val extras = Bundle(2)
                 serviceIntent.putExtra(LocatorService.EXTRA_COMMAND, LocatorService.SEND_LOCATION)
                 serviceIntent.putExtra(LocatorService.SEND_LOCATION_RECIPIENT, message.originatingAddress)
                 context.startService(serviceIntent)
             }
            else ->
                sendSms(message.originatingAddress, "Unknown command \"$command\". Known commands: Send location")
        }
    }

}
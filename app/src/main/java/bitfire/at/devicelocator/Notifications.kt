/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package bitfire.at.devicelocator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class Notifications {

    companion object {

        const val CHANNEL_SERVICE = "service"
        const val ID_SERVICE_RUNNING = 1

        const val CHANNEL_ACTIONS = "actions"
        const val ID_LOCATION_SENT = 2

        fun prepare(context: Context) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel = NotificationChannel(CHANNEL_SERVICE,
                        context.getString(R.string.notifications_service_status_name), NotificationManager.IMPORTANCE_NONE)
                serviceChannel.description = context.getString(R.string.notifications_service_status_description)
                nm.createNotificationChannel(serviceChannel)

                val actionsChannel = NotificationChannel(CHANNEL_ACTIONS,
                        context.getString(R.string.notifications_service_actions_name), NotificationManager.IMPORTANCE_HIGH)
                actionsChannel.description = context.getString(R.string.notifications_service_actions_description)
                actionsChannel.shouldShowLights()
                actionsChannel.shouldVibrate()
                nm.createNotificationChannel(actionsChannel)
            }
        }

    }

}
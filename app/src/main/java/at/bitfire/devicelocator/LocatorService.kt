/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.bitfire.devicelocator

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import at.bitfire.devicelocator.Util.sendSms

class LocatorService: Service() {

    companion object {
        val permissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
        )

        const val EXTRA_COMMAND = "command"

        const val SEND_LOCATION = "send_location"
        const val SEND_LOCATION_RECIPIENT = "recipient"

        const val SEND_SMS_LOCATION = "send_sms_location"
        const val SEND_SMS_RECIPIENT = "recipient"

        val keepInForeground = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        fun Location.toText() =
                "geo:$latitude,$longitude,$altitude;u=$accuracy ($provider)"

    }

    private val receiver = SmsReceiver()


    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        // check for permissions
        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED })
            return

        Notifications.prepare(this)

        if (keepInForeground) {
            registerReceiver(receiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))

            val notification = NotificationCompat.Builder(this, Notifications.CHANNEL_SERVICE)
                    .setSmallIcon(R.drawable.notify_location)
                    .setContentTitle(getString(R.string.notification_service_running_title))
                    .setContentText(getString(R.string.notification_service_running_message))
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setShowWhen(false)
                    .setContentIntent(PendingIntent.getActivity(applicationContext, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
                    .build()
            startForeground(Notifications.ID_SERVICE_RUNNING, notification)
        }
    }

    override fun onDestroy() {
        if (keepInForeground)
            unregisterReceiver(receiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.getStringExtra(EXTRA_COMMAND)) {
            SEND_LOCATION -> {
                val recipient = intent.extras.getString(SEND_LOCATION_RECIPIENT)

                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val replyIntent = Intent(this, LocatorService::class.java)
                    replyIntent.putExtra(EXTRA_COMMAND, SEND_SMS_LOCATION)
                    replyIntent.putExtra(SEND_SMS_RECIPIENT, recipient)

                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?:
                            locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)?.let { lastLocation ->
                        sendSms(recipient, getString(R.string.service_last_known_coarse_location, lastLocation.toText()))
                        notifyLocationSent(recipient)
                    }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, object: LocationListener {
                        override fun onLocationChanged(location: Location) {
                            sendSms(recipient, location.toText())
                            notifyLocationSent(recipient)
                            locationManager.removeUpdates(this)
                        }
                        override fun onProviderEnabled(provider: String?) {
                        }
                        override fun onProviderDisabled(provider: String?) {
                            sendSms(recipient, getString(R.string.service_gps_location_provider_disabled))
                            locationManager.removeUpdates(this)
                        }
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                            if (status == LocationProvider.OUT_OF_SERVICE) {
                                sendSms(recipient, getString(R.string.service_gps_location_provider_out_of_service))
                                locationManager.removeUpdates(this)
                            }
                        }
                    })
                } else
                    sendSms(recipient, getString(R.string.service_no_location_permission))
            }

            SEND_SMS_LOCATION -> {
                val recipient = intent.extras.getString(SEND_SMS_RECIPIENT)
                (intent.extras[LocationManager.KEY_LOCATION_CHANGED] as? Location)?.let { location ->
                    sendSms(recipient, location.toText())
                }
            }
        }

        return if (keepInForeground)
            START_STICKY
        else
            START_NOT_STICKY
    }

    private fun notifyLocationSent(recipient: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_SENT_LOCATION, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nm = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, Notifications.CHANNEL_ACTIONS)
                .setSmallIcon(R.drawable.notify_location)
                .setContentTitle(getString(R.string.notification_location_sent_location_shared))
                .setContentText(getString(R.string.notification_location_sent_sent_location_to, recipient))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addPerson(Uri.fromParts("tel", recipient, null).toString())
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        nm.notify(Notifications.ID_LOCATION_SENT, notification)
    }

}
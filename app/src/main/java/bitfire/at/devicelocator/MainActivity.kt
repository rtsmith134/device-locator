/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package bitfire.at.devicelocator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        )

        const val EXTRA_SENT_LOCATION = "sentLocation"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null && intent.getBooleanExtra(EXTRA_SENT_LOCATION, false))
            supportFragmentManager.beginTransaction()
                    .add(SentLocationFragment(), null)
                    .commit()

        if (permissions.all { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED })
            startService()
        else
            ActivityCompat.requestPermissions(this, permissions, 0)
    }

    override fun onResume() {
        super.onResume()
        val currentPassword = Settings(this).password
        password.text = currentPassword
        info_send_sms.text = getString(R.string.main_activity_info_send_sms, currentPassword)
        info_location_services.text = getString(R.string.main_activity_info_location_services, getString(R.string.app_name))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            startService()
        else {
            Toast.makeText(this, "Requires additional permissions", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun startService() {
        if (LocatorService.keepInForeground)
            ActivityCompat.startForegroundService(this, Intent(this, LocatorService::class.java))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    fun openSettings(item: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun openAbout(item: MenuItem) {
        supportFragmentManager.beginTransaction()
                .add(AboutFragment(), null)
                .commit()
    }

}
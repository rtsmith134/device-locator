/*
 * Copyright © Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.bitfire.devicelocator

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class SentLocationFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.ic_phone_black)
                .setTitle(R.string.found_fragment_found_your_device)
                .setMessage(R.string.found_fragment_consider_donation)
                .setPositiveButton(R.string.found_fragment_donation_options) { _, _ ->
                    Util.viewHomepage(requireActivity())
                }
                .setNegativeButton(R.string.found_fragment_not_now) { _, _ -> }
                .create()!!
    }

}
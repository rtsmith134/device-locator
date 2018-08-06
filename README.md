
Device Locator
==============

Device Locator is licensed under the [GPLv3 License](LICENSE).

© Ricki Hirner ([bitfire web engineering](https://www.bitfire.at))


How does it work?
=================

Device Locator is a foreground service that should always run
in background. It intercepts incoming SMS messages. When a
message with the correct password and command is received,
Device Locator executes the requested action.

At the moment, only the "Send location" command is implemented.

**To receive the device location, send an SMS with body `YourPassword Send location`
to it and Device Locator will reply with its location.**


Donations
=========

If Device Locator was useful to find your (expensive) mobile phone/tablet,
please consider a donation.

**For donations, please use the channels listed on our [DAVdroid
donation page](https://www.davdroid.com/donate/) (just replace DAVdroid by Device Locator).**


FAQ
===

Why another device locating app?
--------------------------------

I could not find a device locating app that fulfills these requirements:

  * usable without Google Play
  * trustworthy (i.e. no spyware and ads) and ideally open-source
  * doesn't require Internet access (only SMS)
  * does actually work

So I have decided to make my own one.


What preconditions does it need to actually work?
-------------------------------------------------

Device Locator needs:

  * active location services (when Location is disabled in the notifcation drawer
    or Android settings, Device Locator will *not* work)
  * all requested permissions (location, send SMS, phone state)
  * to be active and running (i.e. if you force-stop the app in Android settings,
    it may not work anymore)


Why does it show a permanent notification?
------------------------------------------

On Android 8+, an app (which is not the default SMS app) which wants
to receive incoming SMS messages must run in the background.

When an app runs in the background, it will be killed by Android
(app standby/doze mode) after some time unless it shows a permanent
notification.

To hide the notification, hide the app's notification channel
"Service status".


Can I contribute to this app?
-----------------------------

Of course! Just fork the repository, implement your changes and [let me
know](mailto:play@bitfire.at?subject=Device%20Locator). **Please do not
send bug reports or feature requests.**

If you want to maintain or work on this project, let me also know – I'll
happily give you write access or hand it over to you.


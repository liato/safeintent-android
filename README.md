safeintent-android
==================

A PoC for sending Intents safely between Android applications.


##Instructions

####Install the real host
```
cd safeintent-android/host
./gradlew installRealRelease
```

The real host is the app that our client wants to send an Intent to. The public key of the real app is known to the client beforehand.

####Install and run the client
```
cd safeintent-android/client
./gradlew installDebug
adb shell am start -n eu.nullbyte.safeintent.client/eu.nullbyte.safeintent.client.MainActivity
```

Click the *Send intent* button.
The steps taken to verify that the installed host is our intended app are printed in the log window.
When verified our host app should open with the *username* and *password* fields prefilled with the values from our Intent.

####Install the rouge host
The rouge app has the same package name but is signed with a different key.

```
cd safeintent-android/host
adb uninstall eu.nullbyte.safeintent.host
./gradlew installRougeRelease
```
Run the client again
`adb shell am start -n eu.nullbyte.safeintent.client/eu.nullbyte.safeintent.client.MainActivity`

When you "Send intent" this time the signature verification should fail and nothing should be sent to the rouge app.





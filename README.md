# workaround for setWifiApEnabled which is not working in android 8

WifiManager access point methods are not working anymore in android 8, looks like it's functionality has been moved into ConnectionMananager, and again fuck using reflection.

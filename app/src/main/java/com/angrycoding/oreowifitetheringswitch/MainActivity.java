package com.angrycoding.oreowifitetheringswitch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.android.dx.stock.ProxyBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MainActivity extends Activity {

    ConnectivityManager connectivityManager;

    private boolean isTetheringStarted() {
        try {
            Method getTetheredIfaces = connectivityManager.getClass().getDeclaredMethod("getTetheredIfaces");
            String[] result = (String[])getTetheredIfaces.invoke(connectivityManager);
            for (String iface : result) {
                if (iface.startsWith("softap")) {
                    return true;
                }
            }
        } catch (Exception e) {}
        return false;
    }

    private void startTethering() {
        try {
            Class classOnStartTetheringCallback = Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
            Method startTethering = connectivityManager.getClass().getDeclaredMethod("startTethering", int.class, boolean.class, classOnStartTetheringCallback);
            Object proxy = ProxyBuilder.forClass(classOnStartTetheringCallback).handler(new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    return null;
                }
            }).build();
            startTethering.invoke(connectivityManager, 0, false, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTethering() {
        try {
            Method method = connectivityManager.getClass().getDeclaredMethod("stopTethering", int.class);
            method.invoke(connectivityManager, 0);
        } catch (Exception e) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Settings.System.canWrite(this)) {
            if (isTetheringStarted()) {
                stopTethering();
            } else {
                startTethering();
            }
        }
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, 0, null);
        }
    }

}

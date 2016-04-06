package tcc.cast5;

import android.app.Application;

import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;

import java.util.Locale;

import tcc.cast5.tools.Acesso;

/**
 * Created by admin on 31/12/2015.
 */
public class CastApplication extends Application {


    public static final double VOLUME_INCREMENT = 0.05;
    public static final int PRELOAD_TIME_S = 20;

    @Override
    public void onCreate() {
        super.onCreate();
        String applicationId = getString(R.string.app_id);


        CastConfiguration options = new CastConfiguration.Builder(applicationId)
                .enableAutoReconnect()
                .enableCaptionManagement()
                .enableDebug()
                .enableLockScreen()
                .enableNotification()
                .enableWifiReconnection()
                .setCastControllerImmersive(true)
                .setLaunchOptions(false, Locale.getDefault())
                .addNamespace("urn:x-cast:teste")
                .setNextPrevVisibilityPolicy(CastConfiguration.NEXT_PREV_VISIBILITY_POLICY_DISABLED)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_REWIND, false)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
                .setForwardStep(10)
                .build();

        VideoCastManager.initialize(this, options);
        Acesso.initialize();


    }


}

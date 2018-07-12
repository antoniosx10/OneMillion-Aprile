package unisa.it.pc1.provacirclemenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Build;
import android.util.Log;

import unisa.it.pc1.provacirclemenu.ListenerService;

/**
 * Created by PC1 on 08/03/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootRec","entrato");
            Intent serviceIntent = new Intent(context, ListenerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, ListenerService.class));
            } else {
                context.startService(new Intent(context, ListenerService.class));
            }
        }
    }
}

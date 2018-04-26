package unisa.it.pc1.provacirclemenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import unisa.it.pc1.provacirclemenu.ListenerService;

/**
 * Created by PC1 on 08/03/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ListenerService.class);
            context.startService(serviceIntent);
        }
    }
}

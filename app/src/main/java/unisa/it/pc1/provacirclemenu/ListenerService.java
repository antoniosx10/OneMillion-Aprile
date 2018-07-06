package unisa.it.pc1.provacirclemenu;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.Date;

import com.abangfadli.shotwatch.ScreenshotData;
import com.abangfadli.shotwatch.ShotWatch;

/**
 * Created by PC1 on 17/04/2018.
 */

public class ListenerService extends Service {

    private ShotWatch mShotWatch;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipText = clipboard.getPrimaryClip();
                ClipData.Item clipItem = clipText.getItemAt(0);
                final String text = clipItem.getText().toString();
                final Date data = new Date();

                Intent i = new Intent(getApplicationContext(),CircleActivity.class);

                i.putExtra("testoCopiato", text);
                i.putExtra("dataOdierna", data);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);

            }
        });

        ShotWatch.Listener listener = new ShotWatch.Listener() {
            @Override
            public void onScreenShotTaken(ScreenshotData screenshotData) {



                Intent i = new Intent(getApplicationContext(),CircleActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
            }
        };
        mShotWatch = new ShotWatch(getContentResolver(), listener);

        mShotWatch.register();

        Log.d("COMAND","BA");

        return super.onStartCommand(intent, flags, startId);
    }


}
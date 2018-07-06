package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import unisa.it.pc1.provacirclemenu.model.ChatMessage;
import unisa.it.pc1.provacirclemenu.model.User;

public class CircleActivity extends Activity {

    private DatabaseReference mMessagesDBRef;
    private DatabaseReference mUsersRef;

    private String testo;
    private View mChatHeadView;
    private WindowManager mWindowManager;
    private CircleMenu circleMenu;

    private Boolean firstTime = true;

    private Task task;

    private Handler handler;
    private Runnable runnable;

    private Boolean isDettagli = false;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private DatabaseReference mConvDatabase;

    private ArrayList<User> utenti;

    public WindowManager.LayoutParams params;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);


        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i = getIntent();

        testo = i.getStringExtra("testoCopiato");

        utenti = new ArrayList<User>();

        startTimerHead();
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setMainMenu(Color.parseColor("#d3d1d1"), R.drawable.ic_menu_black_24dp, R.drawable.ic_remove_circle_black_24dp);
        uploadChat();
        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                if (!isDettagli) {
                    startTimerHead();
                } else {
                    isDettagli = false;
                    circleMenu.setVisibility(View.INVISIBLE);

                }
            }
        });

        params = new WindowManager.LayoutParams();
        params = createHead();

        circleMenu.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialY;
            private int initialX;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopTimerHead();
                        //remember the initial position.
                        initialY = params.y;
                        initialX = params.x;

                        //get the touch location
                        initialTouchY = event.getRawY();
                        initialTouchX = event.getRawX();


                        lastAction = event.getAction();

                        return true;
                    case MotionEvent.ACTION_UP:
                        startTimerHead();
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            stopTimerHead();
                            break;
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        stopTimerHead();
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(circleMenu, params);
                        int differenzaY = 0;
                        if (params.y > initialY) {
                            differenzaY = params.y - initialY;
                        } else {
                            differenzaY = initialY - params.y;
                        }

                        int differenzaX = 0;
                        if (params.y > initialY) {
                            differenzaX = params.x - initialX;
                        } else {
                            differenzaX = initialX - params.x;
                        }

                        if (differenzaY > 0.3 || differenzaX > 0.3)
                            lastAction = event.getAction();

                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            circleMenu.setVisibility(View.VISIBLE);
            if(data != null) {
                task = (Task) data.getSerializableExtra("taskDettagli");
            }
    }

    private void uploadChat(){
        Query conversationQuery = mConvDatabase.orderByChild("timestamp").limitToFirst(5);

        final ArrayList<User> finalListaUtenti = new ArrayList<User>();

        conversationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot convData, String s) {

                mUsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userData) {
                        User user = new User();
                        user.setDisplayName(userData.child(convData.getKey()).child("displayName").getValue(String.class));
                        user.setThumb_image(userData.child(convData.getKey()).child("thumb_image").getValue(String.class));
                        user.setUserId(userData.child(convData.getKey()).getKey());

                        finalListaUtenti.add(user);

                        utenti = finalListaUtenti;
                        createCircleMenu();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        return;
    }

    public void createCircleMenu() {

        Bitmap[] imgs = new Bitmap[5];
        for(int i = 0; i<5; i++) {
            imgs[i] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_24dp);
        }
        try {
            imgs = new BitmapFromURLTask().execute(utenti).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        circleMenu
                .addSubMenu(Color.parseColor("#ff9d00"), imgs[0])
                .addSubMenu(Color.parseColor("#ff9d00"), imgs[1])
                .addSubMenu(Color.parseColor("#ff9d00"), imgs[2])
                .addSubMenu(Color.parseColor("#ff9d00"), imgs[3])
                .addSubMenu(Color.parseColor("#ff9d00"), imgs[4])
                .addSubMenu(Color.parseColor("#ff9d00"), R.drawable.ic_save_black_24dp)
                .addSubMenu(Color.parseColor("#ff9d00"), R.drawable.ic_add_black_24dp)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        switch (i) {
                            case 0:

                                //sendMessageToFirebase(testo, "HoqUAbqQpGNAqfbHon5dA2bDpMt1","E1f84RhKMhOA49X696T1Y0vNvBA3");

                            case 5:

                                Toast.makeText(getApplicationContext(), "Hai salvato: " + testo, Toast.LENGTH_SHORT).show();
                                break;

                            case 6:
                                isDettagli = true;
                                task = new Task(testo, new Date(), "3");
                                Intent dettagliIntent = new Intent(getApplicationContext(), DettagliActivity.class);
                                dettagliIntent.putExtra("task", task);
                                startActivityForResult(dettagliIntent, 15);
                                break;
                        }
                    }
                });
    }

    class BitmapFromURLTask extends AsyncTask<ArrayList<User>, Void, Bitmap[]> {

        private Exception exception;

        protected Bitmap[] doInBackground(ArrayList<User>... urls) {
            try {
                Bitmap[] imgs = new Bitmap[5];

                for (int i = 0; i < urls[0].size(); i++) {
                    if(utenti.get(i) != null) {
                        URL url = new URL(urls[0].get(i).getThumb_image());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);

                        imgs[i] = myBitmap;
                    } else {
                        imgs[i] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_24dp);
                    }
                }
                return imgs;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Bitmap feed) {

        }
    }

    private void startTimerHead(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (circleMenu != null) {
                    mWindowManager.removeView(circleMenu);
                    circleMenu = null;
                }

            }
        };
        handler.postDelayed(runnable,4000);
    }
    private void stopTimerHead(){
        handler.removeCallbacks(runnable);
    }
    private WindowManager.LayoutParams createHead() {

        circleMenu = findViewById(R.id.circle_menu);

        ViewGroup p = (ViewGroup)circleMenu.getParent();
        p.removeView(circleMenu);

        //Add the view to the window.
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(circleMenu,params);

        return params;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWindowManager.removeView(circleMenu);
        stopTimerHead();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}



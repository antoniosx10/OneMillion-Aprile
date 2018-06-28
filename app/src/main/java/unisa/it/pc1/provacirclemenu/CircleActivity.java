package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.util.ArrayList;
import java.util.Date;

import unisa.it.pc1.provacirclemenu.model.ChatMessage;

public class CircleActivity extends Activity {
    //PROVA
    private DatabaseReference mMessagesDBRef;
    private DatabaseReference mUsersRef;



    private String testo;
    private View mChatHeadView;
    private WindowManager mWindowManager;
    private CircleMenu circleMenu;

    private Task task;

    private Handler handler;
    private Runnable runnable;

    private Boolean isDettagli = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        //PROVA
        //init Firebase
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i = getIntent();

        testo = i.getStringExtra("testoCopiato");


        startTimerHead();

        final String arrayName[] = {"Contatto1",
                "Contatto2",
                "Contatto3",
                "Contatto4",
                "Contatto5",
                "Salva",
                "Aggiungi Dettagli"
        };

        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#CCCCCC"), R.drawable.ic_menu_black_24dp, R.drawable.ic_remove_circle_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_person_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_person_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_person_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_person_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_person_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_save_black_24dp)
                .addSubMenu(Color.parseColor("#1db58c"), R.drawable.ic_add_black_24dp)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        switch (i) {
                            case 0:
                                //PROVE
                                sendMessageToFirebase(testo, "HoqUAbqQpGNAqfbHon5dA2bDpMt1","E1f84RhKMhOA49X696T1Y0vNvBA3");

                            case 5:

                                Toast.makeText(getApplicationContext(), "Hai salvato: " + testo, Toast.LENGTH_SHORT).show();
                                break;

                            case 6:
                                isDettagli = true;
                                task = new Task(testo, new Date(), "3");
                                Intent dettagliIntent = new Intent(getApplicationContext(), DettagliActivity.class);
                                dettagliIntent.putExtra("task",task);
                                startActivityForResult(dettagliIntent,15);
                                break;
                        }
                    }
                });
        //---Fine CircleMenu


        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                if(!isDettagli) {
                    startTimerHead();
                } else {
                    isDettagli = false;
                    circleMenu.setVisibility(View.INVISIBLE);

                }
            }
        });

        final WindowManager.LayoutParams params =  createHead();

        mChatHeadView.setOnTouchListener(new View.OnTouchListener() {
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
                        mWindowManager.updateViewLayout(mChatHeadView, params);
                        int differenzaY = 0;
                        if(params.y > initialY) {
                            differenzaY = params.y - initialY;
                        } else {
                            differenzaY = initialY - params.y;
                        }

                        int differenzaX = 0;
                        if(params.y > initialY) {
                            differenzaX = params.x - initialX;
                        } else {
                            differenzaX = initialX - params.x;
                        }

                        if(differenzaY > 0.3 || differenzaX > 0.3)
                            lastAction = event.getAction();

                        return true;
                }
                return false;
            }
        });
    }

    private WindowManager.LayoutParams createHead() {

        mChatHeadView = findViewById(R.id.circle_menu);

        ViewGroup p = (ViewGroup )mChatHeadView.getParent();
        p.removeView(mChatHeadView);

        //Add the view to the window.
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView,params);

        return params;
    }

    private void startTimerHead(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mChatHeadView != null) {
                    mWindowManager.removeView(mChatHeadView);
                    mChatHeadView = null;
                }

            }
        };
        handler.postDelayed(runnable,4000);
    }

    private void stopTimerHead(){
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            circleMenu.setVisibility(View.VISIBLE);
            if(data != null) {
                task = (Task) data.getSerializableExtra("taskDettagli");
            }
    }


    //PROVA
    private void sendMessageToFirebase(String message, String senderId, String receiverId) {
        //mMessagesList.clear();

        ChatMessage newMsg = new ChatMessage(message, senderId, receiverId);
        mMessagesDBRef.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (!task.isSuccessful()) {
                    //error
                    // Toast.makeText(ChatMessagesActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(ChatMessagesActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                    //mMessageEditText.setText(null);
                    //hideSoftKeyboard();
                }
            }
        });
    }

}

package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import unisa.it.pc1.provacirclemenu.model.Chatter;
import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.Messages;
import unisa.it.pc1.provacirclemenu.model.Task;
import unisa.it.pc1.provacirclemenu.model.User;

public class CircleActivity extends Activity {

    private DatabaseReference mMessagesDBRef;
    private DatabaseReference mUsersRef;

    private Bitmap[] imgs;

    private String testo;
    private View mChatHeadView;
    private WindowManager mWindowManager;
    private CircleMenu circleMenu;

    private StorageReference mImageStorage;
    private Boolean firstTime = true;

    private unisa.it.pc1.provacirclemenu.model.Task task;

    private ArrayList<String> imgsList;

    private Handler handler;
    private Runnable runnable;

    private Boolean isDettagli = false;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private DatabaseReference mConvDatabase;

    private ArrayList<Chatter> utenti;

    public WindowManager.LayoutParams params;

    private DatabaseReference mRootRef;

    private String imagePath;

    private DatabaseReference mNotificationRef;

    private String nome;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i = getIntent();
        testo = i.getStringExtra("testoCopiato");
        imagePath = i.getStringExtra("pathImg");
        nome = i.getStringExtra("nome");

        utenti = new ArrayList<Chatter>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        imgsList = new ArrayList<String>();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        try {
            utenti = (ArrayList<Chatter>) ObjectSerializer
                    .deserialize(prefs.getString("lista_utenti", ObjectSerializer.serialize(new ArrayList<Chatter>())));

            /*imgsList = (ArrayList<String>) ObjectSerializer
                    .deserialize(prefs.getString("lista_img", ObjectSerializer.serialize(new ArrayList<String>())));*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        imgs = new Bitmap[5];
        int k = 0;
        for(String encoded :imgsList) {
            if(!encoded.equals("vuoto")) {
                byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                imgs[k] = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            }
            k++;
        }

        createCircleMenu(imgs);

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
                500,
               500,
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
        stopTimerHead();
        if(circleMenu != null )
            mWindowManager.removeView(circleMenu);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear();
        editor.commit();
    }


    private void sendImage(final String senderId, final String receiverId, String image, final String nome) {

        Uri imageUri = Uri.fromFile(new File(image));

        final String current_user_ref = "messages/" + senderId + "/" + receiverId;
        final String chat_user_ref = "messages/" + receiverId + "/" + senderId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(senderId).child(receiverId).push();

        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".png");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    String download_url = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", senderId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });

                    DatabaseReference task_message_push = mRootRef.child("Task")
                            .child(receiverId).push();

                    String push_id_task = task_message_push.getKey();

                    unisa.it.pc1.provacirclemenu.model.Task taskInivato = new unisa.it.pc1.provacirclemenu.model.Task("Immagine", new Date(),null, "", "normale",senderId,false,nome,download_url);

                    mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(taskInivato).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                            if (task.isSuccessful()) {

                            } else {

                            }
                        }
                    });
                }
            }
        });

    }
    private void sendMessage(String senderId, String receiverId, String message,String nome) {

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + senderId + "/" + receiverId;
            String chat_user_ref = "messages/" + receiverId + "/" + senderId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(senderId).child(receiverId).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", senderId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


            mRootRef.child("Chat").child(senderId).child(receiverId).child("seen").setValue(true);
            mRootRef.child("Chat").child(senderId).child(receiverId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(receiverId).child(senderId).child("seen").setValue(false);
            mRootRef.child("Chat").child(receiverId).child(senderId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });
            DatabaseReference task_message_push = mRootRef.child("Task")
                    .child(receiverId).push();

            String push_id_task = task_message_push.getKey();

            unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task(message, new Date(),null, "", "normale",senderId,false,nome,"");

            mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                    if (task.isSuccessful()) {

                    } else {

                    }
                }
            });


        }else{
            Toast.makeText(getApplicationContext(),"Inserisci il tuo messaggio",Toast.LENGTH_LONG).show();
        }
    }
    private void sendTask(final String senderId, final String receiverId, String messaggio, final String nome, String imagePath){

        if(messaggio.equalsIgnoreCase("Immagine")){

            Uri imageUri = Uri.fromFile(new File(imagePath));

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(senderId).child(receiverId).push();

            final String push_id = user_message_push.getKey();
            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".png");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        String download_url = task.getResult().getDownloadUrl().toString();

                        DatabaseReference task_message_push = mRootRef.child("Task")
                                .child(receiverId).push();

                        String push_id_task = task_message_push.getKey();

                        unisa.it.pc1.provacirclemenu.model.Task taskInivato = new unisa.it.pc1.provacirclemenu.model.Task("Immagine", new Date(),null, "", "normale",senderId,false,nome,download_url);

                        mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(taskInivato).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                if (task.isSuccessful()) {

                                } else {

                                }
                            }
                        });
                    }
                }
            });
        }else{
            DatabaseReference task_message_push = mRootRef.child("Task")
                    .child(receiverId).push();

            String push_id_task = task_message_push.getKey();

            unisa.it.pc1.provacirclemenu.model.Task taskInivato = new unisa.it.pc1.provacirclemenu.model.Task(messaggio, new Date(),null, "", "normale",senderId,false,nome,"");

            mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(taskInivato).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                    if (task.isSuccessful()) {

                    } else {

                    }
                }
            });
        }


    }

    private void createCircleMenu(Bitmap[] imgs) {
        startTimerHead();
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
        circleMenu.setMainMenu(Color.parseColor("#d3d1d1"), R.drawable.ic_menu_black_24dp, R.drawable.ic_remove_circle_black_24dp);

        ArrayList<Drawable> imgsDrawable = new ArrayList<>();
        ColorGenerator generator = ColorGenerator.MATERIAL;

        String tempNome;
        for(int j = 0; j < 5; j++){
            imgsDrawable.add(getResources().getDrawable(R.drawable.ic_remove_circle_black_24dp));
        }
        for(int i= 0; i < utenti.size(); i++){
                if(utenti.get(i) instanceof User) {
                    User user = (User) utenti.get(i);

                    tempNome = user.getDisplayName();

                } else {
                    Group group = (Group) utenti.get(i);
                    tempNome = group.getNome();
                }

                Log.d("NOME",""+tempNome);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(tempNome.substring(0,1), generator.getRandomColor());

                imgsDrawable.add(i,drawable);
        }

        circleMenu
                .addSubMenu(Color.parseColor("#ff9d00"), imgsDrawable.get(0))
                .addSubMenu(Color.parseColor("#ff9d00"), imgsDrawable.get(1))
                .addSubMenu(Color.parseColor("#ff9d00"), imgsDrawable.get(2))
                .addSubMenu(Color.parseColor("#ff9d00"), imgsDrawable.get(3))
                .addSubMenu(Color.parseColor("#ff9d00"), imgsDrawable.get(4))
                .addSubMenu(Color.parseColor("#ff9d00"), R.drawable.ic_save_black_24dp)
                .addSubMenu(Color.parseColor("#ff9d00"), R.drawable.ic_add_black_24dp)
                .addSubMenu(Color.parseColor("#ff9d00"), R.drawable.ic_contacts_black_24dp)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        switch (i) {
                            case 0:
                                if(testo != null) {
                                    if(utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendMessage(mAuth.getCurrentUser().getUid(), user.getUserId(), testo, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendMessageToGroup(group.getGroup_id(),testo,nome);
                                    }
                                } else {
                                    if (utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendImage(mAuth.getCurrentUser().getUid(), user.getUserId(), imagePath, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendImageToGroup(group.getGroup_id(), imagePath,nome);
                                    }
                                }
                                break;
                            case 1:
                                if(testo != null) {
                                    if(utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendMessage(mAuth.getCurrentUser().getUid(), user.getUserId(), testo, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendMessageToGroup(group.getGroup_id(),testo,nome);
                                    }
                                } else {
                                    if (utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendImage(mAuth.getCurrentUser().getUid(), user.getUserId(), imagePath, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendImageToGroup(group.getGroup_id(), imagePath,nome);
                                    }
                                }
                                break;
                            case 2:
                                if(testo != null) {
                                    if(utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendMessage(mAuth.getCurrentUser().getUid(), user.getUserId(), testo, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendMessageToGroup(group.getGroup_id(),testo,nome);
                                    }
                                } else {
                                    if (utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendImage(mAuth.getCurrentUser().getUid(), user.getUserId(), imagePath, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendImageToGroup(group.getGroup_id(), imagePath,nome);
                                    }
                                }
                                break;
                            case 3:
                                if(testo != null) {
                                    if(utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendMessage(mAuth.getCurrentUser().getUid(), user.getUserId(), testo, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendMessageToGroup(group.getGroup_id(),testo,nome);
                                    }
                                } else {
                                    if (utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendImage(mAuth.getCurrentUser().getUid(), user.getUserId(), imagePath, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendImageToGroup(group.getGroup_id(), imagePath,nome);
                                    }
                                }
                                break;
                            case 4:
                                if(testo != null) {
                                    if(utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendMessage(mAuth.getCurrentUser().getUid(), user.getUserId(), testo, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendMessageToGroup(group.getGroup_id(),testo,nome);
                                    }
                                } else {
                                    if (utenti.get(i) instanceof User) {
                                        User user = (User) utenti.get(i);
                                        sendImage(mAuth.getCurrentUser().getUid(), user.getUserId(), imagePath, nome);
                                    } else {
                                        Group group = (Group) utenti.get(i);
                                        sendImageToGroup(group.getGroup_id(), imagePath,nome);
                                    }
                                }
                                break;
                            case 5:
                                if(testo != null) {
                                    sendTask(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid(),testo,"Me stesso",null);
                                } else {
                                    sendTask(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid(),"Immagine","Me stesso",imagePath);
                                }
                                break;
                            case 6:
                                isDettagli = true;
                                Intent dettagliIntent = new Intent(getApplicationContext(), DettagliActivity.class);
                                if(testo != null) {
                                    dettagliIntent.putExtra("testo",testo);
                                } else {
                                    dettagliIntent.putExtra("imagePath",imagePath);
                                }
                                dettagliIntent.putExtra("nome",nome);
                                startActivity(dettagliIntent);
                                break;

                            case 7:
                                isDettagli = true;
                                Intent contatti = new Intent(getApplicationContext(),ContattiActivity.class);
                                if(testo != null) {
                                    contatti.putExtra("testo",testo);
                                } else {
                                    contatti.putExtra("imagePath",imagePath);
                                }
                                contatti.putExtra("flagDettagli","false");
                                contatti.putExtra("nome",nome);

                                startActivity(contatti);

                                break;
                        }
                    }
                });

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

    private void sendImageToGroup(final String groupId, final String image, final String nome) {

        Uri imageUri = Uri.fromFile(new File(image));

        final DatabaseReference group_message_push = mRootRef.child("Group")
                .child(groupId).child("messages").push();

        final String push_id = group_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".png");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    String download_url = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mAuth.getCurrentUser().getUid());

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(groupId + "/" + "messages" + "/" + push_id , messageMap);

                    mRootRef.child("Group").updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });

                    mRootRef.child("Chat").child(mAuth.getCurrentUser().getUid()).child(groupId).child("seen").setValue(true);
                    mRootRef.child("Chat").child(mAuth.getCurrentUser().getUid()).child(groupId).child("timestamp").setValue(ServerValue.TIMESTAMP);

                    mRootRef.child("Chat").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

                            for(DataSnapshot d : iter) {
                                Log.d("DERP"," " + d.getKey());
                                if(!d.getKey().equals(mAuth.getCurrentUser().getUid())) {

                                    mRootRef.child("Chat").child(d.getKey()).child(groupId).child("seen").setValue(true);
                                    mRootRef.child("Chat").child(d.getKey()).child(groupId).child("timestamp").setValue(ServerValue.TIMESTAMP);


                                    DatabaseReference task_message_push = mRootRef.child("Task")
                                            .child(dataSnapshot.getKey()).push();

                                    String push_id_task = task_message_push.getKey();

                                    unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task(image, new Date(), null, "", "normale", mAuth.getCurrentUser().getUid(), false, nome, "");



                                    mRootRef.child("Task").child(d.getKey()).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                            if (task.isSuccessful()) {

                                            } else {

                                            }
                                        }
                                    });
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    group_message_push.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }
                        }
                    });

                }
            }
        });

    }

    private void sendMessageToGroup(final String groupId, final String message, final String nome) {
        if(!TextUtils.isEmpty(message)) {

            DatabaseReference user_message_push = mRootRef.child("Group")
                    .child(groupId).child("messages").push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mAuth.getCurrentUser().getUid());

            mRootRef.child("Chat").child(mAuth.getCurrentUser().getUid()).child(groupId).child("seen").setValue(true);
            mRootRef.child("Chat").child(mAuth.getCurrentUser().getUid()).child(groupId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

                    for(DataSnapshot d : iter) {
                        Log.d("DERP"," " + d.getKey());
                        if(!d.getKey().equals(mAuth.getCurrentUser().getUid())) {

                            mRootRef.child("Chat").child(d.getKey()).child(groupId).child("seen").setValue(true);
                            mRootRef.child("Chat").child(d.getKey()).child(groupId).child("timestamp").setValue(ServerValue.TIMESTAMP);


                            DatabaseReference task_message_push = mRootRef.child("Task")
                                    .child(dataSnapshot.getKey()).push();

                            String push_id_task = task_message_push.getKey();

                            unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task(message, new Date(), null, "", "normale", mAuth.getCurrentUser().getUid(), false, nome, "");



                            mRootRef.child("Task").child(d.getKey()).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                    if (task.isSuccessful()) {

                                    } else {

                                    }
                                }
                            });
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            user_message_push.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });

        }
    }

}




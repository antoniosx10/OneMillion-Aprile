package unisa.it.pc1.provacirclemenu;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.abangfadli.shotwatch.ScreenshotData;
import com.abangfadli.shotwatch.ShotWatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import unisa.it.pc1.provacirclemenu.model.Chatter;
import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.User;

/**
 * Created by PC1 on 17/04/2018.
 */

public class ListenerService extends Service {

    private ShotWatch mShotWatch;

    private DatabaseReference mUsersRef;
    private DatabaseReference mGroupsRef;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private DatabaseReference mConvDatabase;

    private ArrayList<Chatter> utenti;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    private Intent i;

    private User utente;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupsRef = FirebaseDatabase.getInstance().getReference().child("Group");

        mUsersRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userData) {
                 utente = userData.getValue(User.class);
                 utente.setDisplayName(userData.child("displayName").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        utenti = new ArrayList<Chatter>();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Login
        if(mAuth.getCurrentUser() != null) {
            Log.d("Login", "Sei loggato");

            final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {

                    uploadChat();

                    ClipData clipText = clipboard.getPrimaryClip();
                    ClipData.Item clipItem = clipText.getItemAt(0);
                    final String text = clipItem.getText().toString();
                    final Date data = new Date();

                    i = new Intent(getApplicationContext(), CircleActivity.class);
                    i.putExtra("testoCopiato", text);
                    i.putExtra("dataOdierna", data);
                    i.putExtra("nome", utente.getDisplayName());
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);

                }
            });

            ShotWatch.Listener listener = new ShotWatch.Listener() {
                @Override
                public void onScreenShotTaken(ScreenshotData screenshotData) {

                    uploadChat();

                    i = new Intent(getApplicationContext(), CircleActivity.class);
                    i.putExtra("pathImg", screenshotData.getPath());
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            };
            mShotWatch = new ShotWatch(getContentResolver(), listener);

            mShotWatch.register();
        } else {
            Log.d("Login","Non sei loggato");
            startActivity(new Intent(getApplicationContext(),Autenticazione.class));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadChat(){

        utenti.clear();
        Query conversationQuery = mConvDatabase.orderByChild("timestamp").limitToFirst(5);

        final ArrayList<Chatter> finalListaUtenti = new ArrayList<Chatter>();

        conversationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot convData, String s) {

                if(convData.getKey().charAt(0) != '-') {
                    mUsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userData) {
                            User user = new User();
                            user.setDisplayName(userData.child(convData.getKey()).child("displayName").getValue(String.class));
                            user.setThumb_image(userData.child(convData.getKey()).child("thumb_image").getValue(String.class));
                            user.setUserId(userData.child(convData.getKey()).getKey());

                            finalListaUtenti.add(user);

                            utenti = finalListaUtenti;

                            try {
                                Bitmap[] imgs = new BitmapFromURLTask().execute(utenti).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.d("Diverso","clacla");
                    mGroupsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot groupData) {
                            Group group = new Group();
                            group.setNome(groupData.child(convData.getKey()).child("nome").getValue(String.class));
                            group.setThumb_image(groupData.child(convData.getKey()).child("thumb_image").getValue(String.class));
                            group.setGroup_id(groupData.child(convData.getKey()).getKey());

                            finalListaUtenti.add(group);

                            utenti = finalListaUtenti;

                            try {
                                Bitmap[] imgs = new BitmapFromURLTask().execute(utenti).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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

    public void sendUtenti(Bitmap[] imgs) throws IOException, ExecutionException, InterruptedException {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();

        editor.putString("lista_utenti", ObjectSerializer.serialize(utenti));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ArrayList<String> encodedList = new ArrayList<String>();

        for(int i=0; i<imgs.length; i++) {
            if(imgs[i] != null) {
                Log.d("Lung", imgs.length + "");
                imgs[i].compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                encodedList.add(encoded);
            } else {
                encodedList.add("vuoto");
            }
        }

        editor.putString("lista_img", ObjectSerializer.serialize(encodedList));

        editor.commit();



    }

    class BitmapFromURLTask extends AsyncTask<ArrayList<Chatter>, Void, Bitmap[]> {

        protected Bitmap[] doInBackground(ArrayList<Chatter>... urls) {
            Bitmap[] imgs = new Bitmap[5];
            for (int j = 0; j < urls[0].size(); j++) {
                if (utenti.get(j) != null) {
                    try {
                        URL url = null;
                        if(utenti.get(j) instanceof User) {
                            User userUrl = (User) urls[0].get(j);
                            url = new URL(userUrl.getThumb_image());
                        } else {
                            Group groupUrl = (Group) urls[0].get(j);
                            url = new URL(groupUrl.getThumb_image());
                        }
                        HttpURLConnection connection = null;
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = null;
                        input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        Log.d("Bit", "" + myBitmap);
                        imgs[j] = myBitmap;

                    } catch (Exception e) {

                    }
                } else {
                    imgs[j] = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_24dp);
                    Log.d("BitElse", "");
                }
            }
            return imgs;
        }

        protected void onPostExecute(Bitmap[] imgs) {
            try {
                Log.d("SSD", imgs[0] + "");
                sendUtenti(imgs);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
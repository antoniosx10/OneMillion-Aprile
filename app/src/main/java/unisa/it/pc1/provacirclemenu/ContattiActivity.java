package unisa.it.pc1.provacirclemenu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class ContattiActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private ArrayList<String> listaNumeri;
    private UtentiModel utentiModel;
    private RecyclerView recyclerView;
    private List<User> listContact;

    private Boolean firstTime = false;

    private FirebaseAuth userFirebase;
    private DatabaseReference mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

    private java.util.ArrayList<User> mUsersList = new ArrayList<>();

    private ProgressBar progressBar;

    private RecyclerViewAdapterContactSend recyclerViewAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatti);

        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();

        Uri data = i.getData();

        String testo = i.getStringExtra("testo");
        String imagePath = i.getStringExtra("imagePath");

        String descrizione = i.getStringExtra("descrizione");
        String categoria = i.getStringExtra("categoria");
        Date deadline = (Date) i.getSerializableExtra("deadline");

        String flagDettagli = i.getStringExtra("flagDettagli");

        String nome = i.getStringExtra("nome");

        // Figure out what to do based on the intent type
        if (i.getType().indexOf("image/") != -1) {
            Log.d("Entrato immagine","si");
            Uri imageUri = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);


            imagePath = getRealPathFromURI(getApplicationContext(),imageUri);


            //QUI AGGIUNGERE SALVATAGGIO A FIREBASESTORAGE

            flagDettagli = "false";

        } else if (i.getType().equals("text/plain")) {
            Log.d("Entrato link/testo","si");
            testo = i.getStringExtra(Intent.EXTRA_TEXT);
            flagDettagli = "false";
        }

        utentiModel = new UtentiModel();
        //Trovare modo per non far caricare sempre listaNumeri
        listaNumeri = utentiModel.getContattiTelefono(this);
        progressBar = findViewById(R.id.progressBar_chat);
        recyclerView = findViewById(R.id.contatti_list);

        progressBar.setVisibility(ProgressBar.VISIBLE);


        mUsersList = queryUsersAndAddthemToList();
        recyclerViewAdapter = new RecyclerViewAdapterContactSend(this,mUsersList,testo,imagePath,descrizione,categoria,deadline,flagDettagli,nome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    private ArrayList<User> queryUsersAndAddthemToList(){

        final ArrayList<User> lista = new ArrayList<User>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        user.setNumber(snap.child("number").getValue(String.class));
                        user.setUserId(snap.getKey());
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            if(!user.getUserId().equals(mAuth.getCurrentUser().getUid())){

                                /**

                                 for(String s : listaNumeri) {
                                 Log.d("Num",s);
                                 if(!s.substring(0,3).equals("+39")) {
                                 s = "+39" + s;
                                 }
                                 if(s.equals(user.getNumber())) {
                                 lista.add(user);
                                 notify();
                                 }
                                 }
                                 **/
                                lista.add(user);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return lista;
    }



    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

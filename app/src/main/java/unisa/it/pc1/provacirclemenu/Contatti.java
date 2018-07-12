package unisa.it.pc1.provacirclemenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class Contatti extends AppCompatActivity {
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

        String testo = i.getStringExtra("testo");
        String imagePath = i.getStringExtra("imagePath");


        utentiModel = new UtentiModel();

        //Trovare modo per non far caricare sempre listaNumeri
        listaNumeri = utentiModel.getContattiTelefono(this);


        progressBar = findViewById(R.id.progressBar_chat);
        recyclerView = findViewById(R.id.contatti_list);

        progressBar.setVisibility(ProgressBar.VISIBLE);


        mUsersList = queryUsersAndAddthemToList();
        recyclerViewAdapter = new RecyclerViewAdapterContactSend(this,mUsersList,testo,imagePath);
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


}
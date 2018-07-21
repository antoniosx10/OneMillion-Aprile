package unisa.it.pc1.provacirclemenu;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;

import unisa.it.pc1.provacirclemenu.RecyclerViewAdapterContact;
import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

/**
 * Created by Antonio on 24/03/2018.
 */

public class ChatsFragment extends Fragment {
    private UtentiModel utentiModel;
    private ArrayList<String> listaNumeri;

    View v;
    private RecyclerView recyclerView;
    private List<User> listContact;

    private Boolean firstTime = false;
    private FirebaseAuth userFirebase;
    private DatabaseReference mUsersDBRef;
    private ArrayList<User> mUsersList = new ArrayList<>();
    private ProgressBar progressBar;


    public ChatsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFirebase = FirebaseAuth.getInstance();
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        utentiModel = new UtentiModel();

        //Trovare modo per non far caricare sempre listaNumeri
        //listaNumeri = utentiModel.getContattiTelefono(getContext());

        mUsersList = queryUsersAndAddthemToList();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_chats,container,false);
        progressBar = v.findViewById(R.id.progressBar_chat);

        progressBar.setVisibility(ProgressBar.VISIBLE);



        recyclerView = v.findViewById(R.id.conv_list);

        RecyclerViewAdapterContact recyclerViewAdapter = new RecyclerViewAdapterContact(getContext(),mUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private ArrayList<User> queryUsersAndAddthemToList(){

        final ArrayList<User> lista = new ArrayList<User>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsersList.clear();
                Log.d("ONDATA","ppp");
                if(dataSnapshot.getChildrenCount() > 0) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        user.setNumber(snap.child("number").getValue(String.class));
                        user.setUserId(snap.getKey());
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            if(!user.getUserId().equals(userFirebase.getCurrentUser().getUid())){

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

                }
                if(!firstTime) {
                    getFragmentManager().beginTransaction().detach(ChatsFragment.this).attach(ChatsFragment.this).commit();
                    Log.d("First","libidine");
                    firstTime = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
        return lista;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("OnPause", "" + mUsersList.size());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("OnStop", "" + mUsersList.size());
    }

    public void onStart() {
        super.onStart();
        Log.d("OnStart", "" + mUsersList.size());
    }
}

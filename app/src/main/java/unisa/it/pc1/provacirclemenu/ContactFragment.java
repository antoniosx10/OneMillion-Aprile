package unisa.it.pc1.provacirclemenu;

import android.database.Cursor;
import android.provider.ContactsContract;
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

import unisa.it.pc1.provacirclemenu.RecyclerViewAdapter;
import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

/**
 * Created by Antonio on 24/03/2018.
 */

public class ContactFragment extends Fragment {
    private UtentiModel utentiModel;

    View v;
    private RecyclerView recyclerView;
    private List<User> listContact;

    private Boolean firstTime = false;


    private FirebaseAuth userFirebase;
    private DatabaseReference mUsersDBRef;



    private ArrayList<User> mUsersList = new ArrayList<>();



    public ContactFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFirebase = FirebaseAuth.getInstance();
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        utentiModel = new UtentiModel();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.contact_fragment,container,false);
        recyclerView = v.findViewById(R.id.contact_recyclerview);
        RecyclerViewAdapterContact recyclerViewAdapter = new RecyclerViewAdapterContact(getContext(),mUsersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUsersList = queryUsersAndAddthemToList();


    }

    private ArrayList<User> queryUsersAndAddthemToList(){

        final ArrayList<User> lista = new ArrayList<User>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            ArrayList<String> listaNumeri = new ArrayList<String>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        user.setNumber(snap.child("number").getValue(String.class));
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            if(!user.getUserId().equals(userFirebase.getCurrentUser().getUid())){

                                if(!firstTime) {
                                    listaNumeri = utentiModel.getContattiTelefono(getContext());
                                }

                                for(String s : listaNumeri) {
                                    Log.d("Num",s);
                                    if(s.equals(user.getNumber())) {
                                        lista.add(user);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                if(!firstTime) {
                    getFragmentManager().beginTransaction().detach(ContactFragment.this).attach(ContactFragment.this).commit();
                    firstTime = true;

                    Log.d("Entrato", "in firsttime");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return lista;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


}

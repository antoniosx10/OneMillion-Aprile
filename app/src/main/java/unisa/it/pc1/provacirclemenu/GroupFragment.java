package unisa.it.pc1.provacirclemenu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class GroupFragment extends android.support.v4.app.Fragment {
    View v;

    private RecyclerView recyclerView;
    private DatabaseReference mUsersDBRef;
    private DatabaseReference mGroupsDBRef;
    private FirebaseAuth userFirebase;
    private Boolean firstTime = false;

    private ArrayList<Group> mGroupList;
    private ArrayList<String> mUserKeyGroupList;

    private FloatingActionButton creaGruppo;


    public GroupFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFirebase = FirebaseAuth.getInstance();

        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userFirebase.getCurrentUser().getUid()).child("groups");


        mGroupsDBRef = FirebaseDatabase.getInstance().getReference().child("Group");
        mGroupList = new ArrayList<Group>();

        mUserKeyGroupList = queryUserKeyGroupAndAddThemeToList();

        mGroupList = queryGroupsAndAddThemToList();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.group_fragment,container,false);

        creaGruppo = v.findViewById(R.id.crea_gruppo);

        creaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),CreazioneGroupActivity.class);
                startActivity(i);
            }
        });
        recyclerView = v.findViewById(R.id.group_list_fragment);
        RecyclerViewGroup recyclerViewAdapter = new RecyclerViewGroup(getContext(),mGroupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onStart() {
        super.onStart();
    }

    private ArrayList<Group> queryGroupsAndAddThemToList() {

        final ArrayList<Group> lista = new ArrayList<Group>();
        mGroupsDBRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGroupList.clear();
                if(dataSnapshot.getChildrenCount() > 0) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()){

                        Group group = snap.getValue(Group.class);

                        for(String s: mUserKeyGroupList) {

                            if (s.equals(snap.child("group_id").getValue(String.class))) {
                                group.setGroup_id(snap.child("group_id").getValue(String.class));
                                group.setNome(snap.child("nome").getValue(String.class));
                                group.setImmagine(snap.child("immagine").getValue(String.class));
                                Log.d("Scrivo s", "" + s);
                                lista.add(group);
                                Log.d("Listone", "" + lista.get(0).getNome());
                            }
                        }

                    }
                }
                if(!firstTime) {
                    getFragmentManager().beginTransaction()
                            .detach(GroupFragment.this).attach(GroupFragment.this).commit();
                    Log.d("First","libidine");
                    firstTime = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return lista;
    }

    private ArrayList<String> queryUserKeyGroupAndAddThemeToList() {
        final ArrayList<String> lista = new ArrayList<String>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserKeyGroupList.clear();
                if(dataSnapshot.getChildrenCount() > 0) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()){

                        String groupKey = snap.child("group_id").getValue(String.class);
                        Log.d("Scrivo group", "" + groupKey);
                        lista.add(groupKey);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        return lista;
    }
}

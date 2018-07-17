package unisa.it.pc1.provacirclemenu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class GroupFragment extends android.support.v4.app.Fragment {
    View v;

    private RecyclerView recyclerView;
    private DatabaseReference mUsersDBRef;
    private FirebaseAuth userFirebase;

    private ArrayList<Group> groupList;

    public GroupFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFirebase = FirebaseAuth.getInstance();
        mUsersDBRef = FirebaseDatabase.getInstance().getReference();

        groupList = new ArrayList<Group>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.group_fragment,container,false);


        recyclerView = v.findViewById(R.id.group_list_fragment);

        RecyclerViewGroup recyclerViewAdapter = new RecyclerViewGroup(getContext(),groupList);
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
}

package unisa.it.pc1.provacirclemenu;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class TaskFragment extends Fragment {
    private UtentiModel utentiModel;
    private ArrayList<String> listaNumeri;

    View v;
    private RecyclerView recyclerView;
    private List<User> listContact;

    private Boolean firstTime = false;

    private FirebaseAuth userFirebase;
    private DatabaseReference mUsersDBRef;

    private ArrayList<Task> mMessagesList = new ArrayList<>();

    public TaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFirebase = FirebaseAuth.getInstance();
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Messages");

        utentiModel = new UtentiModel();
        //Trovare modo per non far caricare sempre listaNumeri
        listaNumeri = utentiModel.getContattiTelefono(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.task_fragment,container,false);
        recyclerView = v.findViewById(R.id.task_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),mMessagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMessagesList = queryMessagesAndAddthemToList();


    }

    private ArrayList<Task> queryMessagesAndAddthemToList(){

        final ArrayList<Task> lista = new ArrayList<Task>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        Task task = snap.getValue(Task.class);
                        task.setContenuto(snap.child("message").getValue(String.class));
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            lista.add(task);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(!firstTime) {
                    getFragmentManager().beginTransaction().detach(TaskFragment.this).attach(TaskFragment.this).commit();
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


}

package unisa.it.pc1.provacirclemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.Task;

public class ExportCSVActivity extends AppCompatActivity {

    View v;
    private RecyclerView recyclerView;
    private Boolean firstTime = false;
    private FirebaseAuth userFirebase;
    private DatabaseReference mMessagesDBRef;
    private ArrayList<Task> mMessagesList = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_csv);

        userFirebase = FirebaseAuth.getInstance();
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Task").child(userFirebase.getUid());

        recyclerView = findViewById(R.id.task_list);

        mMessagesList = queryMessagesAndAddthemToList();

        recyclerViewAdapter = new RecyclerViewAdapter(this,mMessagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private ArrayList<Task> queryMessagesAndAddthemToList(){

        final ArrayList<Task> lista = new ArrayList<Task>();
        mMessagesDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        Task task = snap.getValue(Task.class);
                        task.setTaskId(snap.getKey());
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            lista.add(task);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

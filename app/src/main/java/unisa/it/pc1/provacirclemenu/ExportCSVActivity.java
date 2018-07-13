package unisa.it.pc1.provacirclemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.Task;

public class ExportCSVActivity extends AppCompatActivity implements OnItemClick {

    View v;
    private RecyclerView recyclerView;
    private Boolean firstTime = false;
    private FirebaseAuth userFirebase;
    private DatabaseReference mMessagesDBRef;
    private ArrayList<Task> mMessagesList = new ArrayList<>();
    private RecyclerViewAdapterExport recyclerViewAdapter;

    private FileWriter mFileWriter;
    private ArrayList<Task> taskDaEsportare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_csv);

        userFirebase = FirebaseAuth.getInstance();
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Task").child(userFirebase.getUid());

        recyclerView = findViewById(R.id.task_list);

        mMessagesList = queryMessagesAndAddthemToList();

        recyclerViewAdapter = new RecyclerViewAdapterExport(this,mMessagesList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        taskDaEsportare = new ArrayList<Task>();
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

    @Override
    public void onClick(Task task) {
        if(task.isSelected()) {
            taskDaEsportare.add(task);
        } else {
            taskDaEsportare.remove(task);
        }
    }

    public void esportaTask(View v) {
        if(taskDaEsportare.size() <= 0) {
            Toast.makeText(getApplicationContext(),"Nessun Task Selezionato",Toast.LENGTH_SHORT).show();
        } else {
            try {
                export(taskDaEsportare);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void export(ArrayList<Task> tasks) throws IOException {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        String fileName = "ToDashTask" + System.currentTimeMillis() + ".csv";
        String filePath = baseDir + File.separator + fileName;
        Log.d("PERCORSO", " " + filePath);
        File f = new File(filePath);
        CSVWriter writer;
        // File exist
        if(f.exists() && !f.isDirectory()){
            mFileWriter = new FileWriter(filePath , true);
            writer = new CSVWriter(mFileWriter);
        }
        else {
            writer = new CSVWriter(new FileWriter(filePath));
        }

        for(Task task: tasks) {
            ArrayList<String> listData = new ArrayList<String>();
            listData.add(task.getContenuto());
            listData.add(task.getFrom());
            listData.add(task.getDescrizione());
            listData.add(task.getCategoria());

            if (task.getDeadline() != null) {
                listData.add(new SimpleDateFormat("yyyy-MM-dd").format(task.getDeadline()));
            }

            if (task.getData() != null) {
                listData.add(new SimpleDateFormat("yyyy-MM-dd").format(task.getData()));
            }

            String[] data = listData.toArray(new String[listData.size()]);

            writer.writeNext(data);
        }
        writer.close();
    }
}

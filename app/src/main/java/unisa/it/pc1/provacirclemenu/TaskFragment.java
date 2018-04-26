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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskFragment extends Fragment {

    private List<String> scelta = new ArrayList<>();

    View v;
    private RecyclerView recyclerView;
    private List<Task> listContact;



    public TaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        listContact = new ArrayList<>();

        DbManager databaseHelper = new DbManager(getContext());
        Cursor c = databaseHelper.query();
        try
        {
            while (c.moveToNext())
            {
                String data = c.getString(1);
                java.sql.Date d = java.sql.Date.valueOf(data);
                listContact.add(new Task(c.getString(0),d,c.getInt(2)));
            }
        }
        finally
        {
            c.close();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.task_fragment,container,false);

        scelta.add("Deadline");
        scelta.add("Data task");

        Spinner spinner = v.findViewById(R.id.spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(scelta,getActivity());
        spinner.setAdapter(adapter);


        recyclerView = v.findViewById(R.id.task_recyclerview);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),listContact);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }
}

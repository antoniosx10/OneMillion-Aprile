package unisa.it.pc1.provacirclemenu;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import unisa.it.pc1.provacirclemenu.model.Task;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class TaskFragment extends Fragment {


    View v;
    private RecyclerView recyclerView;

    private Spinner spinner;

    private Boolean firstTime = false;

    private FirebaseAuth userFirebase;
    private DatabaseReference mMessagesDBRef;

    private ArrayList<Task> mMessagesList = new ArrayList<>();

    private ItemTouchHelper.Callback itemTouchHelperCallback;
    private RecyclerViewAdapter recyclerViewAdapter;

    public TaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFirebase = FirebaseAuth.getInstance();
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Task").child(userFirebase.getUid());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.task_fragment,container,false);

        spinner = v.findViewById(R.id.spinner);
        final ArrayList<String> spinnerText = new ArrayList<>();
        spinnerText.add("Ordina per data inserimento");
        spinnerText.add("Ordinamento per deadLine");
        spinnerText.add("Ordinamento per categoria");
        SpinnerAdapter adapter = new SpinnerAdapter(spinnerText,getActivity());
        spinner.setAdapter(adapter);

        recyclerView = v.findViewById(R.id.task_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new RecyclerViewAdapter(getContext(),mMessagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMessagesList = queryMessagesAndAddthemToList();

        itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //recyclerViewAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(1, ItemTouchHelper.LEFT);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                Task t = recyclerViewAdapter.mData.get(viewHolder.getAdapterPosition());
                recyclerViewAdapter.deleteItem(viewHolder.getAdapterPosition());

                recyclerViewAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

                queryDeleteTask(t.getTaskId());

                return;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                float translationX = Math.min(-dX, viewHolder.itemView.getWidth());
                viewHolder.itemView.setTranslationX(-translationX);
                return;
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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

    private void queryDeleteTask(String id) {

        mMessagesDBRef.child(id).removeValue();

    }

}


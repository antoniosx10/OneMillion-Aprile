package unisa.it.pc1.provacirclemenu;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    private int grandezzaLista;

    public TaskFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFirebase = FirebaseAuth.getInstance();
        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Task").child(userFirebase.getUid());

        //mMessagesList = queryMessagesAndAddthemToList();

        mMessagesDBRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Task task = dataSnapshot.getValue(Task.class);
                    mMessagesList.add(task);
                    recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.getItemCount());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.task_fragment,container,false);

        spinner = v.findViewById(R.id.spinner);
        final ArrayList<String> spinnerText = new ArrayList<>();
        spinnerText.add("data");
        spinnerText.add("deadline");
        spinnerText.add("categoria");
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

                recyclerView.getRecycledViewPool().clear();
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());


                queryDeleteTask(t.getTaskId());

                return;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Paint background = new Paint();
                background.setColor(Color.parseColor("#50C878"));
                float translationX = Math.min(-dX, viewHolder.itemView.getWidth());
                viewHolder.itemView.setTranslationX(-translationX);

                Paint paint = new Paint();

                c.drawRect((float) viewHolder.itemView.getRight() + dX, (float) viewHolder.itemView.getTop(),
                        (float) viewHolder.itemView.getRight(), (float) viewHolder.itemView.getBottom(), background);

                String str = "FATTO";

                Paint mTxtPaint = new Paint();
                Paint.FontMetrics fm = new Paint.FontMetrics();
                mTxtPaint.setTextSize(40.0f);
                mTxtPaint.getFontMetrics(fm);
                mTxtPaint.setColor(Color.WHITE);
                mTxtPaint.setStrokeWidth(2);

                c.drawText(str, (float) viewHolder.itemView.getRight() + dX+70, (float) viewHolder.itemView.getTop() + 75, mTxtPaint);

                return;
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("OnITEM",parent.getItemAtPosition(position).toString());
                /*
                    Data = 0
                    Deadline = 1
                    Categoria = 2
                 */

                if(parent.getItemAtPosition(position).toString().equals("1")) {
                    Collections.sort(mMessagesList, new Comparator<Task>() {
                        @Override
                        public int compare(Task task1, Task task2) {
                            if (task1.getDeadline() == null || task2.getDeadline() == null)
                                return 0;
                            return task1.getDeadline().compareTo(task2.getDeadline());
                        }
                    });
                    recyclerViewAdapter = new RecyclerViewAdapter(getContext(),mMessagesList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else if(parent.getItemAtPosition(position).toString().equals("0")) {
                    Collections.sort(mMessagesList, new Comparator<Task>() {
                        @Override
                        public int compare(Task task1, Task task2) {
                            if (task1.getData() == null || task2.getData() == null)
                                return 0;
                            return task2.getData().compareTo(task1.getData());
                        }
                    });
                    recyclerViewAdapter = new RecyclerViewAdapter(getContext(),mMessagesList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else if(parent.getItemAtPosition(position).toString().equals("2")) {
                    Collections.sort(mMessagesList, new Comparator<Task>() {
                        @Override
                        public int compare(Task task1, Task task2) {
                            String cat1 = task1.getCategoria();
                            String cat2 = task2.getCategoria();
                            int imp1 = 3;
                            int imp2 = 3;
                            if(cat1.equals("da controllare")) {
                                imp1 = 2;
                            } else if(cat1.equals("importante")) {
                                imp1 = 1;
                            }
                            if(cat2.equals("da controllare")) {
                                imp2 = 2;
                            } else if(cat2.equals("importante")) {
                                imp2 = 1;
                            }
                            return imp1 - imp2;
                        }
                    });
                    recyclerViewAdapter = new RecyclerViewAdapter(getContext(),mMessagesList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerViewAdapter);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private ArrayList<Task> queryMessagesAndAddthemToList(){

        final ArrayList<Task> lista = new ArrayList<Task>();
        mMessagesDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessagesList.clear();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
    }



}


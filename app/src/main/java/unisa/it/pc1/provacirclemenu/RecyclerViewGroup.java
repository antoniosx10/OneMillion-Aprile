package unisa.it.pc1.provacirclemenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.User;

public class RecyclerViewGroup extends RecyclerView.Adapter<RecyclerViewGroup.MyViewHolder>{

    static Context mContext;
    static List<Group> mData;
    private DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Group");


    public RecyclerViewGroup(Context mContext, List<Group> mData) {
        this.mContext = mContext;
        this.mData = mData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.group_single_layout,parent,false);
        final RecyclerViewGroup.MyViewHolder myViewHolder = new RecyclerViewGroup.MyViewHolder(v);



        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.nome.setText(mData.get(position).getNome());
        Picasso.with(mContext).load(mData.get(position).getImmagine()).placeholder(R.drawable.ic_group_add_black_24dp).into(holder.foto);

        final String list_user_id = mData.get(position).getGroup_id();

        Query lastMessageQuery = mMessageDatabase.child(list_user_id).child("messages").limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String data = dataSnapshot.child("message").getValue().toString();
                Boolean seen = (Boolean) dataSnapshot.child("seen").getValue();
                holder.setMessage(data,seen);

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

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private CircleImageView foto;
        private TextView messaggio;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.group_single_name);
            foto = itemView.findViewById(R.id.group_single_image);
            messaggio = itemView.findViewById(R.id.group_single_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent groupIntent = new Intent(mContext, GroupActivity.class);
                    groupIntent.putExtra("group_id", mData.get(getPosition()).getGroup_id());
                    groupIntent.putExtra("group_name", nome.getText().toString());
                    mContext.startActivity(groupIntent);
                }
            });

        }

        public void setMessage(String message, boolean isSeen){
            messaggio.setText(message);

            if(!isSeen){
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.BOLD);
            } else {
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.NORMAL);
            }

        }
    }
}

package unisa.it.pc1.provacirclemenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.R;
import unisa.it.pc1.provacirclemenu.model.User;

/**
 * Created by Antonio on 25/03/2018.
 */

public class RecyclerViewAdapterContact extends RecyclerView.Adapter<unisa.it.pc1.provacirclemenu.RecyclerViewAdapterContact.MyViewHolder>{

    static Context mContext;
    static String data;
    static List<User> mData;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


    public RecyclerViewAdapterContact(Context mContext, List<User> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        Log.d("Recycle","volte");

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.nome.setText(mData.get(position).getDisplayName());
        Picasso.with(mContext).load(mData.get(position).getThumb_image()).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.foto);

/**
        userDatabase.child(mData.get(position).getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("online")){
                    String online = (String) dataSnapshot.child("online").getValue();
                    holder.setOnline(online);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
 **/

        final String list_user_id = mData.get(position).getUserId();

        Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

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

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nome;
        private CircleImageView foto;
        private TextView messaggio;
        //private ImageView online_img;


        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.user_single_name);
            foto = itemView.findViewById(R.id.user_single_image);
            //online_img = itemView.findViewById(R.id.user_single_online_icon);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatIntent = new Intent(mContext, ChatActivity.class);
                    chatIntent.putExtra("user_id", mData.get(getPosition()).getUserId());
                    chatIntent.putExtra("user_name", nome.getText().toString());
                    mContext.startActivity(chatIntent);
                }
            });


        }

        public void setMessage(String message, boolean isSeen){

            messaggio = itemView.findViewById(R.id.user_single_status);
            messaggio.setText(message);

            if(!isSeen){
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.BOLD);
            } else {
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.NORMAL);
            }

        }
/**
        public void setOnline(String online){

           if(online.equalsIgnoreCase("true")){
               online_img.setVisibility(ImageView.VISIBLE);
           }else{
               online_img.setVisibility(ImageView.INVISIBLE);

           }

        }
 **/
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

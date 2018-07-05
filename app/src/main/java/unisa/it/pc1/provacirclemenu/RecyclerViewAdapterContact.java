package unisa.it.pc1.provacirclemenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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


    public RecyclerViewAdapterContact(Context mContext, List<User> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.nome.setText(mData.get(position).getDisplayName());
        Picasso.with(mContext).load(mData.get(position).getThumb_image()).placeholder(R.mipmap.ic_launcher_round).into(holder.foto);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nome;
        private CircleImageView foto;
        private TextView messaggio;


        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.user_single_name);
            foto = itemView.findViewById(R.id.user_single_image);
            messaggio = itemView.findViewById(R.id.user_single_status);


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
    }






}

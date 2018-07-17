package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.model.Task;
import unisa.it.pc1.provacirclemenu.model.User;


public class RecyclerViewAdapterCreazioneGroup  extends RecyclerView.Adapter<RecyclerViewAdapterCreazioneGroup.MyViewHolder> {

    static Context mContext;
    static String data;
    static List<User> mData;
    static ArrayList<User> utentiSelezionati;

    private OnItemClickGroup mCallback;

    public RecyclerViewAdapterCreazioneGroup(Context mContext, List<User> mData,OnItemClickGroup listener ) {
        this.mContext = mContext;
        this.mData = mData;
        this.mCallback = listener;
        utentiSelezionati = new ArrayList<User>();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout,parent,false);
        final RecyclerViewAdapterCreazioneGroup.MyViewHolder myViewHolder = new RecyclerViewAdapterCreazioneGroup.MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterCreazioneGroup.MyViewHolder holder, int position) {
        holder.nome.setText(mData.get(position).getDisplayName());
        Picasso.with(mContext).load(mData.get(position).getThumb_image()).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.foto);


        final User user = mData.get(position);
        holder.itemView.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.WHITE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setSelected(!user.isSelected());
                holder.itemView.setBackgroundColor(user.isSelected() ? Color.parseColor("#00e5e5") : Color.WHITE);

                if(user.isSelected()) {
                    utentiSelezionati.add(user);

                } else {
                    utentiSelezionati.remove(user);
                }

               mCallback.onClick(user);

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
        //private ImageView online_img;


        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.user_single_name);
            foto = itemView.findViewById(R.id.user_single_image);
            //online_img = itemView.findViewById(R.id.user_single_online_icon);


        }
    }
}

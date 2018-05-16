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

import java.util.List;

import unisa.it.pc1.provacirclemenu.R;
import unisa.it.pc1.provacirclemenu.model.User;

/**
 * Created by Antonio on 25/03/2018.
 */

public class RecyclerViewAdapterContact extends RecyclerView.Adapter<unisa.it.pc1.provacirclemenu.RecyclerViewAdapterContact.MyViewHolder>{

    Context mContext;
    List<User> mData;


    public RecyclerViewAdapterContact(Context mContext, List<User> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_contact,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.numero.setText(mData.get(position).getNumber());

        holder.numero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,mData.get(position).getNumber()+"",Toast.LENGTH_LONG).show();
                    Intent goToUpdate = new Intent(mContext, ChatMessagesActivity.class);
                    goToUpdate.putExtra("USER_ID",mData.get(position).getUserId());
                    mContext.startActivity(goToUpdate);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_contact;
        private TextView nome;
        private TextView numero;
        //private ImageView foto;


        public MyViewHolder(View itemView) {
            super(itemView);


            item_contact = itemView.findViewById(R.id.contact_item);
            numero = itemView.findViewById(R.id.phone_contact);
            //foto = itemView.findViewById(R.id.img_contact);
        }
    }




}

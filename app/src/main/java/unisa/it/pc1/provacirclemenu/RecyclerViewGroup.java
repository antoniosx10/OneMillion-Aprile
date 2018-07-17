package unisa.it.pc1.provacirclemenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nome.setText(mData.get(position).getNome());
        Picasso.with(mContext).load(mData.get(position).getImmagine()).placeholder(R.drawable.ic_group_add_black_24dp).into(holder.foto);
        holder.messaggio.setText("");
    }

    @Override
    public int getItemCount() {
        return 0;
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
        }
    }
}

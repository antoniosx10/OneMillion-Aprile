package unisa.it.pc1.provacirclemenu;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import unisa.it.pc1.provacirclemenu.model.Task;

/**
 * Created by Antonio on 25/03/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    final Context mContext;
    List<Task> mData;
    Dialog myDialog;


    public RecyclerViewAdapter(Context mContext, List<Task> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_task,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contenuto.setText(mData.get(position).getContenuto());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
        String date=sdf.format(mData.get(position).getData());
        holder.data.setText(date);

        if(mData.get(position).getCategoria().equalsIgnoreCase("importante")){
            holder.categoria_img.setVisibility(ImageView.VISIBLE);
        }
    }

    public void deleteItem(int taskDaElim) {
        Task t = mData.get(taskDaElim);
        mData.remove(t);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_contact;
        private TextView contenuto;
        private TextView data;
        private ImageView categoria_img;


        public  MyViewHolder(View itemView) {
            super(itemView);

            item_contact = itemView.findViewById(R.id.task_item);
            contenuto = itemView.findViewById(R.id.name_task);
            data = itemView.findViewById(R.id.data_task);
            categoria_img = itemView.findViewById(R.id.Categoria_img);


        }
    }
}

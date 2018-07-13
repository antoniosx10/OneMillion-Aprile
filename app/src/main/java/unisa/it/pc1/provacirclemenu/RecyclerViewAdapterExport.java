package unisa.it.pc1.provacirclemenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import unisa.it.pc1.provacirclemenu.model.Task;

/**
 * Created by Antonio on 25/03/2018.
 */

public class RecyclerViewAdapterExport extends RecyclerView.Adapter<RecyclerViewAdapterExport.MyViewHolder> {

    static Context mContext;
    static List<Task> mData;
    static ArrayList<Task> taskSelezionati;

    private OnItemClick mCallback;

    Dialog myDialog;


    public RecyclerViewAdapterExport(Context mContext, List<Task> mData, OnItemClick listener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mCallback = listener;
        taskSelezionati = new ArrayList<Task>();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_task,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.contenuto.setText(mData.get(position).getContenuto());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
        String date=sdf.format(mData.get(position).getData());
        holder.data.setText(date);

        if(mData.get(position).getDeadline() != null){
            SimpleDateFormat deadLineformat = new SimpleDateFormat("dd-MM-yyy");
            String deadLineDate=deadLineformat.format(mData.get(position).getDeadline());
            holder.deadline.setText(deadLineDate);
        }

        if(mData.get(position).getCategoria().equalsIgnoreCase("importante")){
            holder.categoria_img.setVisibility(ImageView.VISIBLE);
        }else if(mData.get(position).getCategoria().equalsIgnoreCase("normale")){

        }else{
            holder.categoria_img.setImageResource(R.mipmap.dacontrollare);
            holder.categoria_img.setVisibility(ImageView.VISIBLE);
        }

        final Task task = mData.get(position);
        holder.itemView.setBackgroundColor(task.isSelected() ? Color.CYAN : Color.WHITE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setSelected(!task.isSelected());
                holder.itemView.setBackgroundColor(task.isSelected() ? Color.parseColor("#00e5e5") : Color.WHITE);

                if(task.isSelected()) {
                    taskSelezionati.add(task);

                } else {
                    taskSelezionati.remove(task);
                }

                mCallback.onClick(task);

            }
        });

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
        private TextView descrizione;
        private TextView deadline;


        public  MyViewHolder(View itemView) {
            super(itemView);

            item_contact = itemView.findViewById(R.id.task_item);
            contenuto = itemView.findViewById(R.id.name_task);
            data = itemView.findViewById(R.id.data_task);
            categoria_img = itemView.findViewById(R.id.Categoria_img);
            deadline = itemView.findViewById(R.id.deadline_task);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext,TaskActivity.class);
                    i.putExtra("task",mData.get(getPosition()));
                    mContext.startActivity(i);
                }
            });

        }
    }
}

package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.model.User;

public class RecyclerViewAdapterContactSend extends RecyclerView.Adapter<unisa.it.pc1.provacirclemenu.RecyclerViewAdapterContactSend.MyViewHolder> {

    static Context mContext;
    static String data;
    static List<User> mData;
    static String testo;
    static String imagePath;
    static String descrizione;
    static String categoria;
    static Date deadline;
    static String flagDettagli;

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    private static StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();

    public RecyclerViewAdapterContactSend(Context mContext, List<User> mData, String testo, String imagePath, String descrizione, String categoria, Date deadline, String flagDettagli) {
        this.mContext = mContext;
        this.mData = mData;
        this.testo = testo;
        this.imagePath = imagePath;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.deadline = deadline;
        this.flagDettagli = flagDettagli;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout,parent,false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.nome.setText(mData.get(position).getDisplayName());
        Picasso.with(mContext).load(mData.get(position).getThumb_image()).placeholder(R.drawable.ic_account_circle_black_24dp).into(holder.foto);


        final String list_user_id = mData.get(position).getUserId();

        Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String data = dataSnapshot.child("message").getValue().toString();
                Boolean seen = (Boolean) dataSnapshot.child("seen").getValue();
                holder.setMessage(data, seen);

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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(testo != null) {
                        if(flagDettagli.equals(true)){
                            sendMessage(mAuth.getCurrentUser().getUid(), mData.get(getPosition()).getUserId(),testo,descrizione,
                                    categoria,deadline,mData.get(getPosition()).getDisplayName());
                            Toast.makeText(mContext,"Task inviato",Toast.LENGTH_LONG).show();
                            ((Activity)mContext).finish();
                        }else{
                            sendMessage(mAuth.getCurrentUser().getUid(), mData.get(getPosition()).getUserId(),testo,mData.get(getPosition()).getDisplayName());
                            Toast.makeText(mContext,"Task inviato",Toast.LENGTH_LONG).show();
                            ((Activity)mContext).finish();
                        }
                    } else {
                        if(flagDettagli.equals("true")){
                            sendImage(mAuth.getCurrentUser().getUid(), mData.get(getPosition()).getUserId(), imagePath,
                                    descrizione,
                                    categoria,deadline,mData.get(getPosition()).getDisplayName());
                            Toast.makeText(mContext,"Task inviato",Toast.LENGTH_LONG).show();
                            ((Activity)mContext).finish();
                        }else{
                            sendImage(mAuth.getCurrentUser().getUid(), mData.get(getPosition()).getUserId(), imagePath,mData.get(getPosition()).getDisplayName());
                            Toast.makeText(mContext,"Task inviato",Toast.LENGTH_LONG).show();
                            ((Activity)mContext).finish();
                        }

                    }
                }
            });


        }

        public void setMessage(String message, boolean isSeen) {

            messaggio = itemView.findViewById(R.id.user_single_status);
            messaggio.setText(message);

            if (!isSeen) {
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.BOLD);
            } else {
                messaggio.setTypeface(messaggio.getTypeface(), Typeface.NORMAL);
            }

        }
    }


    private static void sendMessage(String senderId, String receiverId, String message,String nome) {

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + senderId + "/" + receiverId;
            String chat_user_ref = "messages/" + receiverId + "/" + senderId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(senderId).child(receiverId).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", senderId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


            mRootRef.child("Chat").child(senderId).child(receiverId).child("seen").setValue(true);
            mRootRef.child("Chat").child(senderId).child(receiverId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(receiverId).child(senderId).child("seen").setValue(false);
            mRootRef.child("Chat").child(receiverId).child(senderId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });

            DatabaseReference task_message_push = mRootRef.child("Task")
                    .child(receiverId).push();

            String push_id_task = task_message_push.getKey();

            unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task(message, new Date(),null, "", "normale",senderId,false,mAuth.getCurrentUser().toString());

            mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                    } else {

                    }
                }
            });
        }else{
            Toast.makeText(mContext,"Inserisci il tuo messaggio",Toast.LENGTH_LONG).show();
        }
    }



    private static void sendMessage(String senderId, String receiverId, String message,String descrizione,String categoria, Date deadline,String nome) {

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + senderId + "/" + receiverId;
            String chat_user_ref = "messages/" + receiverId + "/" + senderId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(senderId).child(receiverId).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", senderId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


            mRootRef.child("Chat").child(senderId).child(receiverId).child("seen").setValue(true);
            mRootRef.child("Chat").child(senderId).child(receiverId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(receiverId).child(senderId).child("seen").setValue(false);
            mRootRef.child("Chat").child(receiverId).child(senderId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });

            DatabaseReference task_message_push = mRootRef.child("Task")
                    .child(receiverId).push();

            String push_id_task = task_message_push.getKey();

            unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task(message, new Date(),deadline, descrizione, categoria,senderId,false,mAuth.getCurrentUser().toString());

            mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                    } else {

                    }
                }
            });
        }else{
            Toast.makeText(mContext,"Inserisci il tuo messaggio",Toast.LENGTH_LONG).show();
        }
    }

    private static void sendImage(final String senderId, String receiverId, String image,String nome) {

        Uri imageUri = Uri.fromFile(new File(image));

        final String current_user_ref = "messages/" + senderId + "/" + receiverId;
        final String chat_user_ref = "messages/" + receiverId + "/" + senderId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(senderId).child(receiverId).push();

        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".png");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    String download_url = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", senderId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }
        });

        DatabaseReference task_message_push = mRootRef.child("Task")
                .child(receiverId).push();

        String push_id_task = task_message_push.getKey();

        unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task("Immagine", new Date(),deadline, descrizione, categoria,senderId,false,mAuth.getCurrentUser().toString());

        mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                } else {

                }
            }
        });
    }



    private static void sendImage(final String senderId, String receiverId, String image,String descrizione,String categoria,Date deadline,String nome) {

        Uri imageUri = Uri.fromFile(new File(image));

        final String current_user_ref = "messages/" + senderId + "/" + receiverId;
        final String chat_user_ref = "messages/" + receiverId + "/" + senderId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(senderId).child(receiverId).push();

        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".png");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    String download_url = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", senderId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }
        });


        DatabaseReference task_message_push = mRootRef.child("Task")
                .child(receiverId).push();

        String push_id_task = task_message_push.getKey();

        unisa.it.pc1.provacirclemenu.model.Task task = new unisa.it.pc1.provacirclemenu.model.Task("Immagine", new Date(),deadline, descrizione, categoria,senderId,false,mAuth.getCurrentUser().toString());

        mRootRef.child("Task").child(receiverId).child(push_id_task).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                } else {

                }
            }
        });

    }
}

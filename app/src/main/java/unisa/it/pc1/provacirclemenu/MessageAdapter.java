package unisa.it.pc1.provacirclemenu;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.model.Messages;



public class MessageAdapter extends ArrayAdapter<Messages> {


    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Messages> objects) {
        super(context, resource, objects);


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.message_single_layout, null);

        final TextView messageText = (TextView) view.findViewById(R.id.message_text_layout);
        final CircleImageView profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
        final TextView displayName = (TextView) view.findViewById(R.id.name_text_layout);
        final ImageView messageImage = (ImageView) view.findViewById(R.id.message_image_layout);



        Messages c = getItem(position);
        String curre_user_id = mAuth.getCurrentUser().getUid();

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("displayName").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                displayName.setText(name);

                Picasso.with(profileImage.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_account_circle_black_24dp).into(profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(message_type.equals("text")) {

            if (from_user.equals(curre_user_id)){
                messageText.setBackgroundColor(Color.WHITE);
                messageText.setTextColor(Color.BLACK);
            }else{
               messageText.setBackgroundColor(Color.argb(255,255,157,0));
               messageText.setTextColor(Color.WHITE);
               messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }

            messageText.setText(c.getMessage());
            messageImage.setVisibility(View.INVISIBLE);


        } else {

            messageText.setVisibility(View.INVISIBLE);
           messageText.setPadding(0,0,0,0);
            Picasso.with(messageImage.getContext()).load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_photo_black_24dp).into(messageImage);

        }

        return view;
    }






}

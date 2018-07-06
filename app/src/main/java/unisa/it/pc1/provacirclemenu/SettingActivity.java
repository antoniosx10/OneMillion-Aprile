package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends Activity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUsers;

    private CircleImageView circleImageView;
    private TextView text_nome;

    private Button cambiaImmagine;

    private StorageReference imageStorage;

    private ProgressDialog progressDialog;

    private static final int GALLERY_PICK = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        text_nome = findViewById(R.id.text_display_name);
        circleImageView = findViewById(R.id.immagine_setting);

        cambiaImmagine = findViewById(R.id.cambia_img_btn);

        currentUsers = FirebaseAuth.getInstance().getCurrentUser();

        String currentUid = currentUsers.getUid();

        imageStorage = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.child("displayName").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                text_nome.setText(nome);


                    Picasso.with(SettingActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_account_circle_black_24dp).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingActivity.this).load(image)
                                    .placeholder(R.drawable.ic_account_circle_black_24dp).into(circleImageView);
                        }
                    });


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        cambiaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PROVA"   ,"prova");

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(SettingActivity.this);

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Setta immagine"),GALLERY_PICK);
            }
        });



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == GALLERY_PICK && requestCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage .activity(imageUri).setAspectRatio(1,1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setTitle("Caricamento immagine profilo");
                progressDialog.setMessage("Attendi il caricamnto dell'immagine");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                String currentUser = currentUsers.getUid();


                Uri resultUri = result.getUri();
                final File thumb_fiePath = new File(resultUri.getPath());


                Bitmap thumb_bitMap = null;
                try {
                    thumb_bitMap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_fiePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                StorageReference filePath = imageStorage.child("immagini_profilo").child(currentUser+" .jpg");
                final StorageReference thumb_filepath = imageStorage.child("immagini_profilo").child("thumb").child(currentUser+" .jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){

                                        Map update_HashMapm = new HashMap();
                                        update_HashMapm.put("Immagine",downloadUrl);
                                        update_HashMapm.put("thumb_image",thumb_downloadUrl);

                                        //salva il path nel real-time database
                                        databaseReference.updateChildren(update_HashMapm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }else{
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}

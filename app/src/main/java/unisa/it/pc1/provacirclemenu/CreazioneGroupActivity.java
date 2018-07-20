package unisa.it.pc1.provacirclemenu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import unisa.it.pc1.provacirclemenu.model.Group;
import unisa.it.pc1.provacirclemenu.model.Task;
import unisa.it.pc1.provacirclemenu.model.User;
import unisa.it.pc1.provacirclemenu.model.UtentiModel;

public class CreazioneGroupActivity extends AppCompatActivity implements OnItemClickGroup {

    private EditText nomeGruppo;
    private CircleImageView immagineGruppo;
    private RecyclerView recyclerView;
    private Button creaGruppo;

    private ProgressDialog progressDialog;

    private RecyclerViewAdapterCreazioneGroup recyclerViewAdapterCreazioneGroup;

    private ArrayList<User> utenti;


    private DatabaseReference mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    private java.util.ArrayList<User> mUsersList = new ArrayList<>();

    private FirebaseAuth mAuth;

    private Group group;

    private static final int GALLERY_PICK = 1;

    private StorageReference imageStorage;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_creazione_group);

        nomeGruppo = findViewById(R.id.nome_gruppo);

        immagineGruppo = findViewById(R.id.immagine_gruppo);

        recyclerView = findViewById(R.id.group_list);

        creaGruppo = findViewById(R.id.btn_crea_gruppo);

        mUsersList = queryUsersAndAddthemToList();

        imageStorage = FirebaseStorage.getInstance().getReference();


        group = new Group();



        recyclerViewAdapterCreazioneGroup = new RecyclerViewAdapterCreazioneGroup(this,mUsersList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapterCreazioneGroup);

        utenti = new ArrayList<User>();


        creaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!nomeGruppo.getText().toString().equalsIgnoreCase(""))&&(utenti.size()>0)){

                    DatabaseReference group_push =  mUsersDBRef.child(mAuth.getUid()).child("groups").push();

                    String push_id_group = group_push.getKey();

                    group.setGroup_id(push_id_group);

                    group.setNome(nomeGruppo.getText().toString());

                    mUsersDBRef.child(mAuth.getUid()).child("groups").child(push_id_group).child("group_id").setValue(push_id_group).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                            if (task.isSuccessful()) {

                            } else {

                            }
                        }
                    });

                    for(User temp: utenti){
                        mUsersDBRef.child(temp.getUserId()).child("groups").child(push_id_group).child("group_id").setValue(push_id_group).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                                if (task.isSuccessful()) {

                                } else {

                                }
                            }
                        });
                    }

                    databaseReference.child("Group").child(push_id_group).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            finish();
                        }
                    });


                }else{
                    Toast.makeText(getApplicationContext(),"Errore creazione gruppo",Toast.LENGTH_LONG).show();
                }

            }
        });



        immagineGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(CreazioneGroupActivity.this);

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Setta immagine"),GALLERY_PICK);
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == GALLERY_PICK && requestCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage .activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(CreazioneGroupActivity.this);
                progressDialog.setTitle("Caricamento immagine del gruppo");
                progressDialog.setMessage("Attendi il caricamnto dell'immagine");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();




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


                StorageReference filePath = imageStorage.child("immagini_gruppi").child(group.getGroup_id()+" .jpg");
                final StorageReference thumb_filepath = imageStorage.child("immagini_gruppi").child("thumb").child(group.getGroup_id()+" .jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){
                                        group.setImmagine(downloadUrl);
                                        progressDialog.dismiss();
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


    private ArrayList<User> queryUsersAndAddthemToList(){

        final ArrayList<User> lista = new ArrayList<User>();
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        user.setNumber(snap.child("number").getValue(String.class));
                        user.setUserId(snap.getKey());
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            if(!user.getUserId().equals(mAuth.getCurrentUser().getUid())){

                                /**

                                 for(String s : listaNumeri) {
                                 Log.d("Num",s);
                                 if(!s.substring(0,3).equals("+39")) {
                                 s = "+39" + s;
                                 }
                                 if(s.equals(user.getNumber())) {
                                 lista.add(user);
                                 notify();
                                 }
                                 }
                                 **/
                                lista.add(user);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return lista;
    }


    @Override
    public void onClick(User user) {
        if(user.isSelected()) {
            utenti.add(user);
        } else {
            utenti.remove(user);
        }
    }
}

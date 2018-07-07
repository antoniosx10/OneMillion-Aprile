package unisa.it.pc1.provacirclemenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import unisa.it.pc1.provacirclemenu.model.User;

public class Profiloactivity extends AppCompatActivity {

    private CircleImageView circleImageView;

    private TextView nome_profilo;
    private TextView task_text_count;
    private TextView numero_profilo;

    private DatabaseReference taskDatabase;

    private int countTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiloactivity);

        Intent i = getIntent();

        User user = (User) i.getSerializableExtra("utente");

        circleImageView = findViewById(R.id.immagine_profilo);

        nome_profilo = findViewById(R.id.nome_profilo);
        task_text_count = findViewById(R.id.task_text_count);
        numero_profilo = findViewById(R.id.numero_profilo);

        Picasso.with(Profiloactivity.this).load(user.getThumb_image())
                .placeholder(R.drawable.ic_account_circle_black_24dp).into(circleImageView);

        nome_profilo.setText(user.getDisplayName());
        numero_profilo.setText(user.getNumber());

        taskDatabase = FirebaseDatabase.getInstance().getReference();

        countTask = 0;



        taskDatabase.child("Task").child(user.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countTask++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        task_text_count.setText(countTask+"");





    }
}

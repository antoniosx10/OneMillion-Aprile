package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import unisa.it.pc1.provacirclemenu.model.Task;

public class TaskActivity extends Activity {

    private TextView sender;
    private TextView deadline;
    private TextView messaggio;
    private TextView descrizione;

    private ImageView categoria;
    private ImageView immagine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent i = getIntent();

        Task task = (Task) i.getSerializableExtra("task");

        sender = findViewById(R.id.detail_sender_task);
        deadline = findViewById(R.id.detail_deadline_task);
        messaggio = findViewById(R.id.detail_messaggio_task);
        descrizione = findViewById(R.id.detail_descrizione_task);

        categoria = findViewById(R.id.detail_categoria_task);
        immagine = findViewById(R.id.detail_image_task);


        sender.setText(task.getFrom());

        if(task.getDeadline() != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
            String date=sdf.format(task.getDeadline());

            deadline.setText(date);
        }

        if(task.getContenuto().equalsIgnoreCase("immagine")){
            immagine.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),task.getIsImage(),Toast.LENGTH_LONG).show();
            Picasso.with(getApplicationContext()).load(task.getIsImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(immagine);

        }else{
            messaggio.setText(task.getContenuto());

        }

        descrizione.setText(task.getDescrizione());



        if(task.getCategoria().equalsIgnoreCase("importante")){
            categoria.setVisibility(View.VISIBLE);
        }else{
            if(task.getCategoria().equalsIgnoreCase("da controllare")){
                categoria.setImageResource(R.mipmap.dacontrollare);
                categoria.setVisibility(View.VISIBLE);
            }
        }







    }
}

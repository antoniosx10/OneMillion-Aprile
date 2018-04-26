package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DettagliActivity extends Activity {

    private EditText etDeadline;
    private EditText etCategoria;
    private EditText etDescrizione;

    private String categoria;
    private String descrizione;
    private Date deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli);

        etDeadline = findViewById(R.id.etDeadline);
        etDescrizione = findViewById(R.id.etDescrizione);
        etCategoria = findViewById(R.id.etCategoria);

        categoria = "";
        descrizione = "";
        deadline = new Date();

        categoria = etCategoria.getText().toString();
        descrizione = etDescrizione.getText().toString();


        DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
        Date date = null;
        try {
            date = formatter.parse(etDeadline.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        deadline = date;

    }

    public void salvaDettagli(View v) {
        Task task = (Task) getIntent().getSerializableExtra("task");

        task.setCategoria(categoria);
        task.setDeadline(deadline);
        task.setDescrizione(descrizione);

        Intent i = new Intent(getApplicationContext(),CircleActivity.class);
        i.putExtra("taskDettagli",task);

        setResult(15, i);

        finish();

    }
}

package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import unisa.it.pc1.provacirclemenu.model.Task;


public class DettagliActivity extends Activity {

    private Spinner spCategoria;
    private EditText etDescrizione;
    private DatePicker dpDeadline;
    private String categoria;
    private String descrizione;
    private Date deadline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli);

        //etDeadline = findViewById(R.id.etDeadline);
        etDescrizione = findViewById(R.id.etDescrizione);
        //etCategoria = findViewById(R.id.etCategoria);

        spCategoria = (Spinner) findViewById(R.id.spinnerCategoria);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categorie_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spCategoria.setAdapter(adapter);

        categoria = "normale";
        descrizione = "";
        deadline = new Date();

        dpDeadline = (DatePicker) findViewById(R.id.dpDeadline);



    }

    public void inviaTask(View v) {
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                categoria = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        descrizione = etDescrizione.getText().toString();


        int day = dpDeadline.getDayOfMonth();
        int month = dpDeadline.getMonth();
        int year = dpDeadline.getYear();

        deadline = new Date(year,month,day);

        Log.d("Dati", "deadline: " + deadline + "descrizione: " + descrizione + "categoria: " + categoria);

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

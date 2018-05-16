package unisa.it.pc1.provacirclemenu.Application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ronnykibet on 11/20/17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //set database to persist
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

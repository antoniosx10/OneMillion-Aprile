package unisa.it.pc1.provacirclemenu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DbManager {

    private DBhelper dbhelper;
    private SQLiteDatabase mDb;

    public DbManager(Context context){
        dbhelper = new DBhelper(context);
    }
    public void open(){  //il database su cui agiamo è leggibile/scrivibile
        mDb=dbhelper.getWritableDatabase();

    }

    public void close(){ //chiudiamo il database su cui agiamo
        mDb.close();
    }

    public void save(Task task){
        SQLiteDatabase db=dbhelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put(DatabaseStrings.TESTO, task.getContenuto());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String data = dateFormat.format(date);
        cv.put(DatabaseStrings.DATA,data);
        cv.put(DatabaseStrings.IMMAGINE, task.getFoto());
        try
        {
            db.insert(DatabaseStrings.TBL_NAME, null,cv);
        }
        catch (SQLiteException sqle)
        {
            // Gestione delle eccezioni
        }
    }

    public Cursor query()
    {
        Cursor crs=null;
        try
        {
            SQLiteDatabase db=dbhelper.getReadableDatabase();
            crs=db.query(DatabaseStrings.TBL_NAME, null, null, null, null, null, null, null);
        }
        catch(SQLiteException sqle)
        {
            return null;
        }
        return crs;
    }
}

package unisa.it.pc1.provacirclemenu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper
{
    public static final String DBNAME="PROVA";

    public DBhelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String q="CREATE TABLE "+DatabaseStrings.TBL_NAME+
                "("+
                DatabaseStrings.TESTO+ " TEXT, "+
                DatabaseStrings.DATA+ " TEXT, "+
                DatabaseStrings.IMMAGINE + " INTEGER)";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStrings.TBL_NAME);

        // Create tables again
        onCreate(db);

    }

}
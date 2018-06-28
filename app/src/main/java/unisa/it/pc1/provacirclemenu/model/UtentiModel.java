package unisa.it.pc1.provacirclemenu.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by PC1 on 03/06/2018.
 */

public class UtentiModel {

    public UtentiModel() {

    }

    public ArrayList<String> getContattiTelefono(Context context) {
        ArrayList<String> listaNumeri = new ArrayList<String>();

        //Prendiamo tutti i contatti dal telefono
        Cursor cursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while (cursor.moveToNext()) {
            //String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String phoneNumber = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            listaNumeri.add(phoneNumber);
        }
        return listaNumeri;
    }


}

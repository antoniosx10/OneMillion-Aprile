package unisa.it.pc1.provacirclemenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;
import java.util.zip.Inflater;

public class SpinnerAdapter extends BaseAdapter {
    private List<String> scelta;
    private Activity activity;
    private LayoutInflater layoutInflater;

    public SpinnerAdapter(List<String> scelta, Activity activity){
        this.scelta = scelta;
        this.activity = activity;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return scelta.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view = convertView;
       if(convertView == null){
           view = layoutInflater.inflate(R.layout.spinner_item,null);
       }

        TextView tv = view.findViewById(R.id.textSpinner);
       tv.setText(scelta.get(position));
       return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view=  super.getDropDownView(position, convertView, parent);
        LinearLayout linearLayout = (LinearLayout) view;
        TextView textSpinner = view.findViewById(R.id.textSpinner);
        textSpinner.setGravity(Gravity.CENTER_HORIZONTAL);
        textSpinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }
}

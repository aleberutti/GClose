package com.becafe.gclose.View;

import android.content.Context;
import android.support.annotation.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.becafe.gclose.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> list;


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context=context;
        this.list=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return mySpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return mySpinnerView(position, convertView, parent);
    }

    public View mySpinnerView(int position, @Nullable View myView, @NonNull ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View spinnerView = inflater.inflate(R.layout.spinner_layout, parent, false);

        TextView tv = (TextView) spinnerView.findViewById(R.id.textView);
        tv.setText(list.get(position));

        return spinnerView;
    }
}

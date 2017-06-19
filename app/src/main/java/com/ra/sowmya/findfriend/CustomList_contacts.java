package com.ra.sowmya.findfriend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by sowmya on 4/5/17.
 */

public class CustomList_contacts extends ArrayAdapter<String> {

    public CustomList_contacts(Context context, Vector Contacts) {
        super(context, R.layout.cust_row, Contacts);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.cust_row,parent,false);
        String phone = getItem(position);
        TextView txt = (TextView) view.findViewById(R.id.rowone);
        txt.setText(phone);
        // txt.setText(SantString(phone));
        return view;
    }

    /*public static String SantString(String Phonenumber){
        *//*Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(Phonenumber);*//*
        String[] match = Phonenumber.split("\n");

        String temp=match[1].replaceAll("[-+.^: ,()]","");
        if(temp.length()==10){
            return match[0]+"\n"+temp;
        }else{
            String x="";
            for (int i = 2; i <temp.length() ; i++) {
                x+=temp.charAt(i);
            }
            return match[0]+"\n"+x;
        }
    }*/

}

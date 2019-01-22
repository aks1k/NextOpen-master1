package com.nextopen.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nextopen.R;
import com.nextopen.vo.CountryVO;

public class CountryAdapter extends ArrayAdapter<CountryVO>{
    private Context mContext;
    private CountryVO[] mCountries;
    private TextView mTxtPhone;

    public CountryAdapter(Context context, int textViewResourceId, CountryVO[] objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mCountries = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater.from(mContext));;
        View row = inflater.inflate(R.layout.item_spinner_country, parent, false);
        TextView label=(TextView)row.findViewById(R.id.txt_spinner_item);
        label.setText(mCountries[position].getCountryCode());

        return row;
    }
}

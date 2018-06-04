package com.example.admin.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProjectFragment extends Fragment {
    private static final String TAG="ProjectFragment";
    private String monthstart, monthend;
    private TextView txtProject;
    private ImageView imvProjectArrowLeft,imvProjectArrowRight;
    Calendar calendar;
    private SimpleDateFormat yyyymmdd  =  new SimpleDateFormat ("yyyy-MM-dd", Locale.TAIWAN);
    Toast tos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_fragment,container,false);

        monthstart = getActivity().getIntent().getExtras().getString("monthstart");
        monthend = getActivity().getIntent().getExtras().getString("monthend");

        txtProject=view.findViewById(R.id.txtProject);
        imvProjectArrowLeft=view.findViewById(R.id.imvProjectArrowLeft);
        imvProjectArrowRight=view.findViewById(R.id.imvProjectArrowRight);
        txtProject.setText(monthstart+"~"+monthend);
        calendar=Calendar.getInstance(Locale.TAIWAN);

        imvProjectArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    /*//先清除
                    sortValue.clear();
                    arrayListName.clear();
                    originalAmount.clear();
                    arrayListAmount.clear();*/

                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,-1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    date=yyyymmdd.parse(monthend.replace('/','-'));
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtProject.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    //ReSetMonth();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });

        imvProjectArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    /*sortValue.clear();
                    arrayListName.clear();
                    originalAmount.clear();
                    arrayListAmount.clear();*/
                    Date date=yyyymmdd.parse(monthstart.replace('/','-'));
                    calendar.setTime(date);
                    calendar.add(calendar.MONTH,+1);
                    monthstart=yyyymmdd.format(calendar.getTime());
                    date=yyyymmdd.parse(monthend.replace('/','-'));
                    calendar.roll(Calendar.DAY_OF_MONTH,-1);
                    monthend = yyyymmdd.format(calendar.getTime());
                    txtProject.setText(monthstart.replace('-','/')+"~"+monthend.replace('-','/'));
                    //ReSetMonth();
                }catch (Exception ex){
                    tos.setText("Error:"+ex.toString());
                }
            }
        });
        return view;
    }
}

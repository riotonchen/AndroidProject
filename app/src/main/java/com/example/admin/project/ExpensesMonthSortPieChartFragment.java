package com.example.admin.project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ExpensesMonthSortPieChartFragment extends Fragment  {

    private static final String TAG="IncomeSortFragment";
    PieChart pieChart;

    ArrayList<String> arrayListName = new ArrayList<>();
    ArrayList<String> arrayListAmount = new ArrayList<>();
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    float sum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expenses_monthsortpiechart_fragment,container,false);
        pieChart = (PieChart) view.findViewById(R.id.idPieChart);

        //讀取資料
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        Query("SELECT name,amount FROM mbr_accounting,sys_sort WHERE memberID=1 " +
                "AND mbr_accounting.type=0 AND mbr_accounting.sortID=sys_sort._id");
        sum=0f;
        for(String elem:arrayListAmount)
            sum+=Float.parseFloat(elem);
        for(int i=0;i<arrayListAmount.size();i++){
            float percent=Float.parseFloat(arrayListAmount.get(i))/sum*10000f;
            percent=Math.round(percent)/100f;
            arrayListAmount.set(i,String.valueOf(percent));
        }

        //右下標題
        pieChart.setDescription("總計："+(sum>=0?"$":"-$")+String.valueOf(sum));
        pieChart.setDescriptionColor(Color.WHITE);
        pieChart.setDescriptionTextSize(30);
        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.WHITE);
        //pieChart.setCenterTextColor(Color.WHITE);

        //中心圓
        pieChart.setHoleRadius(30f);//中心圓大小
        pieChart.setCenterTextSize(35);
        pieChart.setCenterText("%");


        pieChart.setTransparentCircleAlpha(0);


        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(30);
        //More options just check out the documentation!

        AddDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = e.toString().indexOf("(sum): ");
                String showAmount = e.toString().substring(pos1 + 7);

                for(int i = 0; i < arrayListAmount.size(); i++){
                    if(Float.parseFloat(arrayListAmount.get(i)) == Float.parseFloat(showAmount)){
                        pos1 = i;
                        break;
                    }
                }
                String showName = arrayListName.get(pos1);
                Toast.makeText(getActivity(),  showName + "："+showAmount + "%", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
        return view;
    }
    private void AddDataSet() {
        Log.d(TAG, "AddDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < arrayListAmount.size(); i++){
            yEntrys.add(new PieEntry(Float.parseFloat(arrayListAmount.get(i)) , i));
        }

        for(int i = 1; i < arrayListName.size(); i++){
            xEntrys.add(arrayListName.get(i));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    private void Query(String sqlCmd) {
        try {
            cur = db.rawQuery(sqlCmd, null);
            int rowsCount = cur.getCount();
            if (rowsCount != 0) {
                cur.moveToFirst();
                for (int i = 0; i < rowsCount; i++) {
                    arrayListName.add(cur.getString(0));
                    arrayListAmount.add(Integer.toString(cur.getInt(1)));
                    cur.moveToNext();
                }
            }
            cur.close();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}

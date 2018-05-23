package com.example.admin.project;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ExpendFragment extends Fragment {

    private static final String TAG="ExpendFragment";
    private Button btnTab,scan_btn,btnDialog;
    private SQLiteDatabase db;
    private final String DB_NAME = "MYLOCALDB";
    private EditText money,date,number,remark;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        scan_btn = (Button) view.findViewById(R.id.scan_btn);
        btnDialog = (Button) view.findViewById(R.id.btnDialog);
        money = (EditText) view.findViewById(R.id.money);
        date = (EditText) view.findViewById(R.id.date);
        number = (EditText) view.findViewById(R.id.number);
        remark = (EditText) view.findViewById(R.id.remark);

        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });

        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說話..."); //語音辨識 Dialog 上要顯示的提示文字

                startActivityForResult(intent, 1);

            }
        });
        Spinner spnSortClass=(Spinner)getActivity().findViewById(R.id.spinner);
       // db = SQLiteDatabase.openDatabase(DB_NAME, android.content.Context.MODE_PRIVATE);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("請掃描左邊QR-Code!!");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //把所有辨識的可能結果印出來看一看，第一筆是最 match 的。
                ArrayList result2 = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String all = "";

                for(int i = 0; i < result2.size(); i++){
                    all = all + result2.get(i) + "\n";
                }
                remark.setText(all, TextView.BufferType.EDITABLE);
                Toast.makeText(getActivity(), all,Toast.LENGTH_SHORT).show();
            }
        }
        if (result!= null)
        {
            if (result.getContents()==null)
            {
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String invoice = result.getContents();
                int money_temp = Integer.parseInt(invoice.substring(30,37),16);
                money.setText(money_temp, TextView.BufferType.EDITABLE);
                date.setText(invoice.substring(10,17), TextView.BufferType.EDITABLE);
                number.setText(invoice.substring(0,10), TextView.BufferType.EDITABLE);

                String answer = invoice+"\n"+number+"\n"+date+"\n"+money;
                //txv.setText();
                Toast.makeText(getActivity(),answer,Toast.LENGTH_SHORT).show();

            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}

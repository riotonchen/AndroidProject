package com.example.admin.project;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IncomeSortFragment extends Fragment implements OnItemClickListener {

    private static final String TAG="IncomeSortFragment";
    private Button btnTab;
    // static final String TB_NAME = "hotlist";
    static final String DB_NAME = "MYLOCALDB";
    //static String [] From = new String[]{"name","budget","cost"};
    DBHelper DH;
    SQLiteDatabase db;
    Cursor cur;
    SimpleCursorAdapter adapter;
    ListView lv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incomesort_fragment,container,false);
        btnTab = (Button) view.findViewById(R.id.btnTab);
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });
        DH = new DBHelper(getActivity());
        db = DH.getReadableDatabase();
        //db = getActivity().openOrCreateDatabase(DB_NAME,android.content.Context.MODE_PRIVATE ,null);
        adapter = new SimpleCursorAdapter(getActivity(),R.layout.item,cur,new String[]{"name","budget","cost"},
                new int[] {R.id.name,R.id.budget,R.id.cost},0);

        lv = (ListView) view.findViewById(R.id.revenue_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        Requery();
        DH.close();
        return view;
    }
    private void Requery() {
        /*String sqlCmd = "SELECT sys_Sort.name,mbr_MemberSort.budget,0 FROM sys_Sort,mbr_MemberSort " +
                "WHERE memberID=1 " +
                "AND sys_Sort._id=mbr_MemberSort.sortID " +
                "AND sys_Sort.type=0";*/
        String sqlCmd="SELECT 分類1,200,0";
        cur = db.rawQuery(sqlCmd, null);
        adapter.changeCursor(cur);
        cur.close();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}

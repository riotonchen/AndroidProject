package com.example.admin.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

public class TestAdapter extends StackAdapter<Integer> {


    public TestAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Integer data, int position, CardStackView.ViewHolder holder) {
        /*if (holder instanceof ColorItemLargeHeaderViewHolder) {
            ColorItemLargeHeaderViewHolder h = (ColorItemLargeHeaderViewHolder) holder;
            h.onBind(data, position);
        }
        if (holder instanceof ColorItemWithNoHeaderViewHolder) {
            ColorItemWithNoHeaderViewHolder h = (ColorItemWithNoHeaderViewHolder) holder;
            h.onBind(data, position);
        }*/
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            default:
                view = getLayoutInflater().inflate(R.layout.card_item, parent, false);
                return new ColorItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 6) {//TODO TEST LARGER ITEM
            return R.layout.card_item;
        }
        return 1;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;
        TextView text_view;
        Button btn;
        ImageView codeImg;
        Bitmap bmp;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            text_view=(TextView)view.findViewById(R.id.text_view);
            btn=(Button)view.findViewById(R.id.button4);
            codeImg=(ImageView)view.findViewById(R.id.codeImg);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Integer data, int position) {
            DBHelper DH = new DBHelper(getContext());
            SQLiteDatabase db = DH.getReadableDatabase();
            String sqlCmd="SELECT name,number FROM mbr_card WHERE card_id=\"" + data + "\"";
            String name="";
            String number="";
            try {
                Cursor cur = db.rawQuery(sqlCmd, null);
                int rowsCount = cur.getCount();
                if (rowsCount != 0) {
                    cur.moveToFirst();
                    for (int i = 0; i < rowsCount; i++) {
                        name=cur.getString(0);
                        number=cur.getString(1);
                        cur.moveToNext();
                    }
                }
                cur.close();
            } catch (Exception ex) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }

            bmp = null;
            try {
                if (number != null && !"".equals(number)) {
                    bmp = CreateOneDCode(number);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (bmp != null) {
                codeImg.setImageBitmap(bmp);
            }
            mTextTitle.setText(String.valueOf(position+1));
            text_view.setText("id:"+String.valueOf(data)+"卡名:"+name);

        }

    }

    /*static class ColorItemWithNoHeaderViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        TextView mTextTitle;

        public ColorItemWithNoHeaderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
        }

        @Override
        public void onItemExpand(boolean b) {
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position));
        }

    }
*/
    //-----------------------------------------------------------------------------------------------------------------------
    /*
    static class ColorItemLargeHeaderViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;

        public ColorItemLargeHeaderViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void onAnimationStateChange(int state, boolean willBeSelect) {
            super.onAnimationStateChange(state, willBeSelect);
            if (state == CardStackView.ANIMATION_STATE_START && willBeSelect) {
                onItemExpand(true);
            }
            if (state == CardStackView.ANIMATION_STATE_END && !willBeSelect) {
                onItemExpand(false);
            }
        }

        public void onBind(Integer data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(String.valueOf(position));
            itemView.findViewById(R.id.text_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CardStackView)itemView.getParent()).performItemClick(ColorItemLargeHeaderViewHolder.this);
                }
            });
        }

    }
*/
    public static Bitmap CreateOneDCode(String content) throws WriterException {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.CODE_128, 800, 200);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}

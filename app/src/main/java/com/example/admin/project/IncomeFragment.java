package com.example.admin.project;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class IncomeFragment extends Fragment {
    private static final String TAG="IncomeFragment";
    private Button btnTab;
    private ImageView imv;
    private Uri imgUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.income_fragment,container,false);
        imv = (ImageView) view.findViewById(R.id.imageView);
        return view;
    }
/*
    public void onGet(View v){
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }else{
            savePhoto();

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode ==100){
            showImg();

        }else{
            Toast.makeText(getActivity(),"沒拍到照片",Toast.LENGTH_LONG).show();
        }
    }

    public void showImg(){
        int iw,ih,vw,vh;
        BitmapFactory.Options option = new BitmapFactory.Options();
        ContentResolver cr = getActivity().getContentResolver();
        option.inJustDecodeBounds = true;
        try{
            BitmapFactory.decodeStream(
                    cr.openInputStream(imgUri),null,option);

        }catch (IOException e){
            Toast.makeText(getActivity(),"讀取照片資訊時發生錯誤",Toast.LENGTH_LONG).show();
            return;
        }
        iw = option.outWidth;
        ih = option.outHeight;
        vw = imv.getWidth();
        vh = imv.getHeight();

        int scaleFactor = Math.min(iw/vw,ih/vh);
        option.inJustDecodeBounds = false;
        option.inSampleSize = scaleFactor;

        Bitmap bmp= null;
        try{
            bmp = BitmapFactory.decodeStream(cr.openInputStream(imgUri),null,null);

        }
        catch (IOException e){
            Toast.makeText(getActivity(),"無法讀取照片",Toast.LENGTH_LONG).show();
        }
        imv.setImageBitmap(bmp);
    }


    public void savePhoto(){
        ContentResolver cr = getActivity().getContentResolver();
        imgUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(it,100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 200){
            savePhoto();
        }
        else {
            Toast.makeText(getActivity(),"需要權限",Toast.LENGTH_SHORT).show();
        }

    }*/
}

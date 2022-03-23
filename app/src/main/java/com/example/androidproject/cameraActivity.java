package com.example.androidproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.templates.ThumbnailTemplate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.androidproject.ml.ModelUnquant;

import org.tensorflow.lite.DataType;


import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.model.Model;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

public class cameraActivity extends AppCompatActivity {
    Button camera, find;
    ImageView imageview;
    TextView result;
    Bitmap bitmap;


    private List<String> labels;

    private static final int RequestPermissionCode = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera = (Button) findViewById(R.id.btn_camera);
        find = (Button) findViewById(R.id.btn_find);
        imageview = (ImageView) findViewById(R.id.imageView1);
        result = (TextView) findViewById(R.id.txt_output);

        EnableRuntimePermission();


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 7);




        }
    });

}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(bitmap.getWidth(),bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,dimension,dimension);
            imageview.setImageBitmap(bitmap);
            bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true);
            classifyImage(bitmap);
            //find.setOnClickListener(new View.OnClickListener() {
               // @Override
                //public void onClick(View view) {



                //}
            //});
        }


    }
    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(cameraActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(cameraActivity.this,"CAMERA permission allow the access to CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }

        }
    public void classifyImage (Bitmap bitmap){
        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*224*224*3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[224*224];
            bitmap.getPixels(intValues, 0, bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
            int pixel = 0;
            for (int i = 0; i<224; i++){
                for (int j = 0; j< 224; j++){
                    int val = intValues [pixel++];
                    byteBuffer.putFloat(((val>>16)&0xFF)*(1.f/255.f));
                    byteBuffer.putFloat(((val>>8)&0xFF)*(1.f/255.f));
                    byteBuffer.putFloat((val&0xFF)*(1.f/255.f));

                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();
            int maxPros = 0;
            float maxConfidence = 0;
            for (int i = 0;i<confidences.length;i++){
                if(confidences[i]>maxConfidence){
                    maxConfidence = confidences[i];
                    maxPros = i;
                }
            }
            //String[] classes = {"cardboard","glass","metal","paper", "plastic", "trash"};
            String[] classes = {"plastic","cardboard","glass"};
            result.setText(classes[maxPros]);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            ///TODO

        }
    }



}

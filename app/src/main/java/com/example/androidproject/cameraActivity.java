package com.example.androidproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Intent;
import android.graphics.Bitmap;


import android.graphics.Color;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidproject.ml.ModelUnquant;
import org.tensorflow.lite.DataType;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class cameraActivity extends AppCompatActivity {
    Button camera,logout;
    ImageView imageview;
    TextView result,detail;
    Bitmap bitmap;
    private static final int imageSize = 224;
    private static final int RequestPermissionCode = 1;
    private static final int RequestImage = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera = (Button) findViewById(R.id.btn_camera);
        logout = (Button) findViewById(R.id.btn_logout);
        imageview = (ImageView) findViewById(R.id.imageView1);
        result = (TextView) findViewById(R.id.txt_output);
        detail = (TextView) findViewById(R.id.txt_detail);

        //If user wants to logout and go back to main screen
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(cameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //To ensure user has given camera permissions
        EnableRuntimePermission();


        camera.setOnClickListener(new View.OnClickListener() {
            //This intent is start external activity to capture image
            @Override
        public void onClick(View v) {
            Intent takepictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takepictureIntent, RequestImage);
        }
    });


}
    @Override
    //onActivityResult retrieves data captures using takePictureIntent and stores it as bitmap in ImageView
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestImage && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(bitmap);


            //This uses bitmap image captured using phone camera and compares it with data stored in ML model to display result
            imageIdentifier(bitmap);

        }
    }

    public void EnableRuntimePermission(){
        //To check if camera permission is not allowed for app, user will see a toast to allow access
        if (ActivityCompat.shouldShowRequestPermissionRationale(cameraActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(cameraActivity.this,"CAMERA permission allow the access to CAMERA app",     Toast.LENGTH_LONG).show();
        }
        //To request camera access if user has not yet allowed/disallowed access
        else {
            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    public void imageIdentifier (Bitmap bitmap){
        try {
            //Resize image captured to 224*224 size
            bitmap = Bitmap.createScaledBitmap(bitmap,imageSize,imageSize,true);

            //Initialize ml model
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
            //ByteBuffer to hold image data that will sent to tflite as input
            ByteBuffer input = ByteBuffer.allocateDirect(4*imageSize*imageSize*3);
            //nativeorder method is used to allocate buffer with same byte order as bitmap image
            input.order(ByteOrder.nativeOrder());

            //initialize array to store pixels of 224*224 image
           int[] intValues = new int[imageSize*imageSize];
           //store pixel values to intValues for range of whole bitmap image captured
            bitmap.getPixels(intValues, 0, bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
            int pixel = 0;

            //obtain single pixel and load rgb value of it in input buffer that ml model will use
            for (int i = 0; i<imageSize; i++){
                for (int j = 0; j< imageSize; j++){
                    int p = intValues [pixel++];

                    //put red float values
                    input.putFloat(((p>>16)&0xFF)/255f);
                    //put green float values
                    input.putFloat(((p>>8)&0xFF)/255f);
                    //put blue float values
                    input.putFloat((p&0xFF)/255f);

                }
            }

            //load input
            inputFeature0.loadBuffer(input);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //output the result to user
            result(outputFeature0);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            //TODO

        }
    }
    public void result(TensorBuffer outputFeature0){

        // get the output of processed buffer and store in confidences array
        float[] confidences = outputFeature0.getFloatArray();
        int maxProb = 0;
        float maxConfidence = 0;
        //Iterate through the results and find the which class has maximum confidence to match the image captured
        for (int i = 0;i<confidences.length;i++){
            if(confidences[i]>maxConfidence){
                maxConfidence = confidences[i];
                maxProb = i;
            }
        }

        // classes that were created during training the model
        String[] classes = {"plastic", "paper", "trash", "metal", "glass", "cardboard"};

        //setText will display the result in text view to user
        result.setText(classes[maxProb].toUpperCase() + " "+(maxConfidence*100) + "%");
        if(classes[maxProb]=="plastic"){
            detail.setText("This plastic is recyclable. Please make sure to recycle at depot to earn money. " + '\n' +"Since last 70 years, 8.3 billion tons of plastic has been generated and only 9% of that has been recycled and rest is dumped. Let's do our part and conserve environment.");

        }
        else if(classes[maxProb]=="paper"){
            detail.setText("Paper is recyclable. Please make sure to throw it in the blue bin. " + '\n' +"Every ton of recycled paper can save 17 trees, 7000 gallons of water and 4000 kilowatts of energy ");

        }
        else if(classes[maxProb]=="trash"){
            detail.setText("This is Trash. Please throw in black bin." + '\n' +"Waste generated per person per day averages 0.74 kgs. Let us do our part and reduce waste generation");

        }
        else if(classes[maxProb]=="metal"){
            detail.setText("This metal is recyclable. Please throw in blue bin." + '\n' +"About 45% of world's steel comes from recycled metal while 33% of aluminium and 40% of copper is produced from same.");

        }
        else if(classes[maxProb]=="glass"){
            detail.setText("This glass is recyclable. If eligible glass bottles/containers can be recycled at depot to earn money. " + '\n' +"Energy saved from recycling 1 glass bottle can run a 100-watt bulb for 4 hours.");

        }
        else if(classes[maxProb]=="cardboard"){
            detail.setText("This cardboard is recyclable. Please throw in blue bin." + '\n' +"Recycling 1 ton of cardboard can save 390 Kwh of energy, 46 gallons of oil and 700 gallons of water.");

        }
    }


}

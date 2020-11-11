package com.example.musico.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musico.BuildConfig;
import com.example.musico.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DemoActivity extends AppCompatActivity {
    private String endPoint = "https://emotionpackedapi.cognitiveservices.azure.com/face/v1.0/";
    private static final String API_KEY = BuildConfig.ApiKey;

    private final FaceServiceClient faceServiceClient = new FaceServiceRestClient(endPoint, API_KEY);

    JSONObject jsonObject, jsonObject1;
    ImageView imageView;
    Bitmap mBitmap;
    boolean takePicture = false;

    private ProgressDialog detectionProgressDialog;
    Face[] facesDetected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        detectionProgressDialog = new ProgressDialog(this);

        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.billgates);
        imageView = findViewById(R.id.imageView);
        Toast.makeText(getApplicationContext(), "Press the Detect Button to take a picture. Press Identify to identify the person.", Toast.LENGTH_LONG).show();
//        imageView.setImageBitmap(mBitmap);
        Button btnDetect = findViewById(R.id.btnDetectFace);
//        Button btnIdentify = findViewById(R.id.btnIdentify);

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DemoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 0);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            detectAndFrame(bitmap);
        }
    }

    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());

        @SuppressLint("StaticFieldLeak") AsyncTask<InputStream, String, Face[]> detectTask =

                new AsyncTask<InputStream, String, Face[]>() {
                    String exceptionMessage = "";

                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            final com.microsoft.projectoxford.face.contract.Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    // returnFaceAttributes:
                                    new FaceServiceClient.FaceAttributeType[]{
                                            FaceServiceClient.FaceAttributeType.Emotion,
                                            FaceServiceClient.FaceAttributeType.Gender}
                            );

                            for (int i = 0; i < result.length; i++) {
                                jsonObject.put("happiness", result[i].faceAttributes.emotion.happiness);
                                jsonObject.put("sadness", result[i].faceAttributes.emotion.sadness);
                                jsonObject.put("surprise", result[i].faceAttributes.emotion.surprise);
                                jsonObject.put("neutral", result[i].faceAttributes.emotion.neutral);
                                jsonObject.put("anger", result[i].faceAttributes.emotion.anger);
                                jsonObject.put("contempt", result[i].faceAttributes.emotion.contempt);
                                jsonObject.put("disgust", result[i].faceAttributes.emotion.disgust);
                                jsonObject.put("fear", result[i].faceAttributes.emotion.fear);
                                Log.e(TAG, "doInBackground: " + jsonObject.toString());

                                jsonObject1.put((String.valueOf(i)), jsonObject);
                            }

                            Map<String, Object> retMap = new Gson().fromJson(jsonObject.toString(), new TypeToken<HashMap<String, Object>>() {
                            }.getType());
                            double maxVal = 0;
                            String maxKeyValue = "";
                            for (Map.Entry<String, Object> entry : retMap.entrySet()) {
                                if ((double) entry.getValue() > maxVal) {
                                    maxVal = (double) entry.getValue();
                                    maxKeyValue = entry.getKey();
                                }
                            }
                            final String emotionValue = maxKeyValue;
                            final double maxKey = maxVal;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO: 14-10-2020 Use the JsonObject to fetch most prob. emotion and match with database 
//                                    Toast.makeText(DemoActivity.this, "DATA" + jsonObject1.toString(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(DemoActivity.this, "Emotion Key: " + emotionValue + " , Max Value: " + maxKey, Toast.LENGTH_LONG).show();
//                                    Creating the Dialog Box to show the User's Emotion to User
                                    AlertDialog.Builder builder
                                            = new AlertDialog
                                            .Builder(DemoActivity.this)
                                            .setTitle(emotionValue + " Emotion detected")
                                            .setMessage("Do you want to Continue?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // Open corresponding detected Emotion Song List(Ex: Emotion Fragment)
////                                                    Toast.makeText(DemoActivity.this, "Opening " + emotionValue, Toast.LENGTH_SHORT).show();
//                                                       Intent FragmentIntent  = new Intent(DemoActivity.this, MainScreenFragment.class);
//                                                       startActivity(FragmentIntent);
                                                    if (emotionValue.equals("neutral")) {
                                                        //Open neutral Fragment
                                                        Intent mainIntent = new Intent(DemoActivity.this, MainActivity.class);
                                                        mainIntent.putExtra("emotion", "neutral");
                                                        startActivity(mainIntent);

                                                    } else if (emotionValue.equals("happiness")) {
                                                        //Open Happy Fragment
                                                        Intent mainIntent = new Intent(DemoActivity.this, MainActivity.class);
                                                        mainIntent.putExtra("emotion", "happiness");
                                                        startActivity(mainIntent);

                                                    } else if (emotionValue.equals("sadness")) {
                                                        //Open sad Fragment
                                                    } else { //Anger
                                                        //Open anger Fragment
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //cancel
                                                    dialogInterface.cancel();
                                                }
                                            });

                                    AlertDialog alertDialog = builder.create();

                                    alertDialog.show();


                                }
                            });

                            if (result == null) {
                                publishProgress(
                                        "Detection Finished. Nothing detected");
                                return null;
                            }
                            Log.e("TAG", "doInBackground: " + "   " + result.length);
                            publishProgress(String.format(
                                    "Detection Finished. %d face(s) detected",
                                    result.length));

                            return result;
                        } catch (Exception e) {
                            exceptionMessage = String.format(
                                    "Detection failed: %s", e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {

                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {

                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        detectionProgressDialog.dismiss();

                        facesDetected = result;

                        if (!exceptionMessage.equals("")) {
                            if (facesDetected == null) {
//                                showError(exceptionMessage + "\nNo faces detected.");
                                Toast.makeText(DemoActivity.this, "No faces detected", Toast.LENGTH_SHORT).show();
                            } else {
//                                showError(exceptionMessage);
                                Toast.makeText(DemoActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (result == null) {
                            if (facesDetected == null) {
//                                showError("No faces detected");
                                Toast.makeText(DemoActivity.this, "No faces detected", Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.e("TAG", "onPostExecute: " + facesDetected);

                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(
                                drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();
//                        Toast.makeText(getApplicationContext(), "Now you can identify the person by pressing the \"Identify\" Button", Toast.LENGTH_LONG).show();
                        takePicture = true;
                    }
                };

        detectTask.execute(inputStream);
    }

    private static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(9);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }
}
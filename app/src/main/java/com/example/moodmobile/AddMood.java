package com.example.moodmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosalgeek.android.photoutil.CameraPhoto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.BreakIterator;
import java.util.ArrayList;

import static com.example.moodmobile.R.id.image;
import static com.example.moodmobile.R.id.imageView;

public class AddMood extends AppCompatActivity {

    public static final int IMG_REQUEST = 21;
    private EditText reasonText;
    private Button publishButton;
    private ImageButton addImageButton;
    private Spinner moodSpinner;
    private Spinner ssSpinner;
    private CheckBox locationCheckBox;
    private String Feeling;
    private String socialSituation;
    private Mood currentMood;
    private String reason;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    private  String encodeImage;
    ImageButton ivCamera;
    private String SYNC_FILE = "sync.sav";




    protected static final String TAG = "AddMood";
    CameraPhoto cameraPhoto;

    final int CAMERA_REQUEST = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);
        final Mood currentMood = new Mood(null);

        reasonText = (EditText) findViewById(R.id.reason);
        publishButton = (Button) findViewById(R.id.publish);
        addImageButton = (ImageButton) findViewById(R.id.ivGallery);
        moodSpinner = (Spinner) findViewById(R.id.moodSpinner);
        ssSpinner = (Spinner) findViewById(R.id.ssSpinner);
        locationCheckBox = (CheckBox) findViewById(R.id.checkBox);
        ivCamera = (ImageButton) findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPhoto();
            }
        });




//        buildGoogleApiClient();
//
//
//        mGoogleApiClient


        // Create an ArrayAdapter using the mood_array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        moodSpinner.setAdapter(adapter);


        // Create another spinner about Social Situation
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.situation_array, android.R.layout.simple_spinner_dropdown_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ssSpinner.setAdapter(adapter2);


        locationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationCheckBox.isChecked()){
                    //Checked test
                    Context context = getApplicationContext();
                    CharSequence text = "Checked";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else{
                    //currentMood.setLocation(null);

                    //Unchecked test
                    Context context = getApplicationContext();
                    CharSequence text = "Unchecked";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });



        //Check the location box
        if (locationCheckBox.isChecked()) {
            Context context = getApplicationContext();
            CharSequence text = "Checked";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }


        publishButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                ElasticsearchMoodController.AddMoodsTask addMoodTask =
                        new ElasticsearchMoodController.AddMoodsTask();

                Feeling = moodSpinner.getSelectedItem().toString();
                currentMood.setFeeling(Feeling);

                socialSituation = ssSpinner.getSelectedItem().toString();

                 //This is for checking the value of CurrentMood and socialSituation

                Context context = getApplicationContext();
                CharSequence text = "Selected Mood: "+Feeling+"\nSocialSituation: "+socialSituation;
                int duration = Toast.LENGTH_LONG;
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

                currentMood.setMoodImage(encodeImage);

                reason = reasonText.getText().toString();

                setResult(RESULT_OK);
                try {currentMood.setMessage(reason);
                } catch (ReasonTooLongException e) {

 //                   Context context = getApplicationContext();
                    CharSequence text2 = "Reason is too long.";
                    int duration2 = Toast.LENGTH_SHORT;
                    Toast toast2 = Toast.makeText(context, text, duration);
                    toast2.show();
                };
                currentMood.setSituation(socialSituation);

                if (IsConnected() == true){
                    addMoodTask.execute(currentMood);
                } else{
                    SaveToFile(currentMood, 1);
                }

                Toast toast = Toast.makeText(context, String.valueOf(currentMood.getMoodImage()), Toast.LENGTH_LONG);
                toast.show();

                Intent MainpageIntent = new Intent(v.getContext(), MainPageActivity.class);
                startActivity(MainpageIntent);
                finish();


            }

        });


    }

    //When click the add Image button
    public void addImage(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = pictureDir.getPath();
        Uri data = Uri.parse(path);
        intent.setDataAndType(data, "image/*");
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){

        if (resultCode == RESULT_OK){
            if (requestCode == IMG_REQUEST){
                Uri imageUri = data.getData();

                InputStream inputStream;

                try{
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 320, 240,true);
                    ImageView moodImage = (ImageView) findViewById(R.id.moodImage);
                    moodImage.setImageBitmap(resized);
                    encodeImage = getEncoded64ImageStringFromBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        else if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 320, 240,true);
                ImageView moodImage = (ImageView) findViewById(R.id.moodImage);
                moodImage.setImageBitmap(resized);
                encodeImage = getEncoded64ImageStringFromBitmap(bitmap);
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    public void takeAPhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
                    String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            return imgString;
    }

    private boolean IsConnected(){
        Context context = this;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void SaveToFile(Mood mood, int task){
        SyncMood syncMood = new SyncMood(mood, task);
        ArrayList<SyncMood> syncList;

        try {
            FileInputStream fis = openFileInput(SYNC_FILE);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<SyncMood>>(){}.getType();
            syncList = gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            syncList = new ArrayList<SyncMood>();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        syncList.add(syncMood);

        try {
            FileOutputStream fos = openFileOutput(SYNC_FILE, 0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(syncList, writer);
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}


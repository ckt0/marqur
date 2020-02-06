package com.marqur.android;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Addmarker extends AppCompatActivity {
    private RecyclerView muploadlistview;
    private static final String TAG = "addmarker";
    private Button btnChoose;
    private EditText tTitle;
    private String uniqueID = UUID.randomUUID().toString();
    private Marker marker;
    private List<String> filenameList;
    private List<String> filedonelist;
    private String downloaduri;
    private EditText tContent;
    private String date_created;
    private List<Uri> phnuri=new ArrayList<>();
    private Button btnDone;
    private String date_modified;
    private List<Double> location=new ArrayList<>();
    private Content icontent;
    private List<Media> Mmedia=new ArrayList<>();
    private Media media;
    private int totalitemselected=1;
    Boolean containsimage=false;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final int PICK_IMAGE_REQUEST = 71;
    private UploadListAdapter uploadListAdapter;
    private int count=0;

    //Firebase
    private FirebaseStorage  firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    //database reference
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_addmarker);
        //Initialize Views and get current date
        filenameList=new ArrayList<>();
        filedonelist=new ArrayList<>();
        uploadListAdapter =new UploadListAdapter(filenameList,filedonelist);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        muploadlistview= (RecyclerView)findViewById(R.id.Recyclerview);
        btnDone= (Button)findViewById(R.id.Done);
        tTitle=(EditText) findViewById(R.id.ETitle);
        tContent=(EditText) findViewById(R.id.EContent);
        date_modified=date_created = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //firebase reference initialise
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user=firebaseAuth.getCurrentUser();


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();
        muploadlistview.setLayoutManager(new LinearLayoutManager(this));
        muploadlistview.setHasFixedSize(true);
        muploadlistview.setAdapter(uploadListAdapter);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });



        //Upload marker to firebase
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int j=0;j<totalitemselected;j++)
                    uploadImage(phnuri.get(j));


            }
        });



    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                totalitemselected=data.getClipData().getItemCount();
                containsimage=true;
                for (int i=0;i<totalitemselected;i++){

                    phnuri.add(i,data.getClipData().getItemAt(i).getUri());
                    String filename = getFileName(data.getClipData().getItemAt(i).getUri());
                    filenameList.add(filename);
                    filedonelist.add("Uploading");
                    uploadListAdapter.notifyDataSetChanged();


                }


            }
            else if(data.getData()!=null){
                containsimage=true;
                phnuri.add(data.getData());

            }
        }
        else{
            containsimage=false;
        }
    }

    //upload
    private void uploadImage(Uri fileuri) {
        String filename = getFileName(fileuri);
        if (fileuri != null) {

            StorageReference fileupload = storageReference.child("images").child(filename);


            fileupload.putFile(fileuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileupload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            for(int i=0;i<totalitemselected;i++) {
                                filedonelist.remove(i);
                                filedonelist.add(i, "done");
                            }
                            count++;
                            uploadListAdapter.notifyDataSetChanged();
                            String downloadUrl = uri.toString();
                            media=new Media(downloadUrl,filename);
                            Mmedia.add(media);
                            if(count==totalitemselected) {
                                icontent = new Content(tTitle.getText().toString().trim(), tContent.getText().toString(), Mmedia);
                                marker = new Marker(uniqueID, tTitle.getText().toString().trim(), user.getDisplayName(), date_created, date_modified, location, 0, 0, 0, 0, 0, icontent);
                                mDatabase.child("Marker").setValue(marker);
                                finish();
                            }
                            //Do what you want with the url
                        }

                });

            }

                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG,"yeeeeeee"+exception.toString());
                    // Handle unsuccessful uploads
                }
            });
        }

        }


    //currentlocation
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            location.add(mLastKnownLocation.getLatitude());
                            location.add(mLastKnownLocation.getLongitude());
                            }
                        else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                        }
                    }
                });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public String getFileName(Uri uri){
        String result=null;
        if(uri.getScheme().equals("content")) {
            Cursor cursor=getContentResolver().query(uri,null,null,null,null);
            try{
                if (cursor!=null&&cursor.moveToFirst()){
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
        }


}

package com.marqur.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
    private TextView tTitle;
    private String uniqueID = UUID.randomUUID().toString();
    private Uri filePath;
    private List<String> filenameList;
    private List<String> filedonelist;
    private String downloaduri;
    private TextView tContent;
    private String date_created;
    private Button btnDone;
    private String date_modified;
    private List<Double> location=new ArrayList<>();
    private Content icontent;
    private Media media;
    Boolean containsimage=false;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final int PICK_IMAGE_REQUEST = 71;
    private UploadListAdapter uploadListAdapter;
    //Firebase
    FirebaseStorage storage;
    private StorageReference storageReference;

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
        tTitle=(TextView)findViewById(R.id.Title);
        tContent=(TextView)findViewById(R.id.Content);
        date_modified=date_created = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //firebase reference
        storageReference = FirebaseStorage.getInstance().getReference();

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
                int totalitemselected=data.getClipData().getItemCount();
                for (int i=0;i<totalitemselected;i++){
                    Uri fileuri=data.getClipData().getItemAt(i).getUri() ;
                    String filename=getFileName(fileuri);
                    filenameList.add(filename);
                    uploadListAdapter.notifyDataSetChanged();
                }

                containsimage = true;
                filePath = data.getData();
            }
            else if(data.getData()!=null){
                filePath = data.getData();
                uploadImage();

            }
        }
        else{
            containsimage=false;
        }
    }

    //upload
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloaduri= uri.toString();
                                    //Do what you want with the url

                                }


                            });
                        }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
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

package com.marqur.android;

import android.content.Intent;
import android.database.Cursor;
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

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AddMarker extends AppCompatActivity {
    private static final String TAG = "addmarker";
    private final int PICK_IMAGE_REQUEST = 71;
    private RecyclerView muploadlistview;
    private Button btnChoose;
    private EditText tTitle;
    private String uniqueID = UUID.randomUUID().toString();
    private Marker marker;
    private List<String> filenameList;
    private List<String> filedonelist;
    private LatLng location;
    private EditText tContent;
    private String date_created;
    private List<Uri> phnuri = new ArrayList<>();
    private Button btnDone;
    private String date_modified;
    private Content icontent;
    private List<Media> Mmedia = new ArrayList<>();
    private Media media;
    // Geofire
    private GeoFire coordinates;
    private int totalitemselected = 0;
    private Boolean containsimage = false;
    private UploadListAdapter uploadListAdapter;
    private int count = 0;

    //Firebase
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    //database reference
    private DatabaseReference mDatabase;
    private Double latitude;
    private Double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_addmarker);
        //Initialize Views and get current date
        filenameList = new ArrayList<>();
        filedonelist = new ArrayList<>();


        uploadListAdapter = new UploadListAdapter(filenameList, filedonelist);
        btnChoose = (Button) findViewById(R.id.button_choose);
        muploadlistview = (RecyclerView) findViewById(R.id.upload_queue);
        btnDone = (Button) findViewById(R.id.button_done);
        tTitle = (EditText) findViewById(R.id.edit_title);
        tContent = (EditText) findViewById(R.id.edit_content);
        date_modified = date_created = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //firebase reference initialise
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        //Geofire initialisation
        coordinates = new GeoFire(mDatabase);


        //find the current panned coordinates
        getMapLocation();

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
                if (containsimage) {
                    for (int j = 0; j < totalitemselected; j++)
                        uploadImage(phnuri.get(j));

                } else {
                    noimage();
                }
            }
        });


    }

    private void getMapLocation() {
        Intent i = getIntent();

        latitude = i.getDoubleExtra("latitude", 0);
        longitude = i.getDoubleExtra("longitude", 0);


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                totalitemselected = data.getClipData().getItemCount();
                containsimage = true;
                for (int i = 0; i < totalitemselected; i++) {

                    phnuri.add(i, data.getClipData().getItemAt(i).getUri());
                    String filename = getFileName(data.getClipData().getItemAt(i).getUri());
                    filenameList.add(filename);
                    filedonelist.add("Uploading");
                    uploadListAdapter.notifyDataSetChanged();


                }


            } else if (data.getData() != null) {
                containsimage = true;
                totalitemselected++;
                phnuri.add(data.getData());
                String filename = getFileName(data.getData());
                filenameList.add(filename);
                filedonelist.add("Uploading");
                uploadListAdapter.notifyDataSetChanged();
            }
        } else {
            containsimage = false;
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
                            for (int i = 0; i < totalitemselected; i++) {
                                filedonelist.remove(i);
                                filedonelist.add(i, "done");
                            }
                            count++;
                            uploadListAdapter.notifyDataSetChanged();
                            String downloadUrl = uri.toString();
                            media = new Media(downloadUrl, filename);
                            Mmedia.add(media);
                            if (count == totalitemselected) {


                                icontent = new Content(tTitle.getText().toString().trim(), tContent.getText().toString(), Mmedia);
                                //marker = new Marker(tTitle.getText().toString().trim(), user.getDisplayName(), date_created, date_modified, location, 0, 0, 0, 0, 0, icontent);
                                mDatabase.child("Marker").child(uniqueID).setValue(marker);
                                ;
                                mDatabase.child("users").child(user.getUid()).child("location").setValue(location);
                                mDatabase.child("users").child(user.getUid()).child("markers").child(uniqueID).setValue("");

                                finish();
                            }
                            //Do what you want with the url
                        }

                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "yeeeeeee" + exception.toString());
                    // Handle unsuccessful uploads
                }
            });
        }

    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void noimage() {


        icontent = new Content(tTitle.getText().toString().trim(), tContent.getText().toString(), null);
        //marker = new Marker(tTitle.getText().toString().trim(), user.getDisplayName(), date_created, date_modified, location, 0, 0, 0, 0, 0, icontent);
        mDatabase.child("Marker").child(uniqueID).setValue(marker);

        mDatabase.child("users").child(user.getUid()).child("location").setValue(location);
        mDatabase.child("users").child(user.getUid()).child("markers").child(uniqueID).setValue("");

        finish();
    }

}

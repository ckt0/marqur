package com.marqur.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public class AddMarker extends AppCompatActivity {

    private static final String TAG = "addmarker";
    private final int PICK_IMAGE_REQUEST = 71;
    private RecyclerView muploadlistview;
    private Button btnChoose;
    private EditText tTitle;

    private Marker marker;

    private List<String> filenameList;
    private List<String> filedonelist;

    private FirebaseFirestore firestore ;
    private EditText tContent;
    private String date_created;
    private List<Uri> phnuri = new ArrayList<>();
    private Button btnDone;
    private String date_modified;
    private String markerId;
    private Content icontent;
    private List<Media> Mmedia = new ArrayList<>();
    private Media media;

    private int totalitemselected = 0;
    private Boolean containsimage = false;
    private UploadListAdapter uploadListAdapter;
    private int count = 0;

    //Firebase
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    //For Geohashing
    private String geoHash;

    //database reference

    private Double latitude;
    private Double longitude;
    private GeoPoint location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_create_marker);

        //generate an id
        markerId = UUID.randomUUID().toString();
        //Initialize Views and get current date
        filenameList = new ArrayList<>();
        filedonelist = new ArrayList<>();


        uploadListAdapter = new UploadListAdapter(filenameList, filedonelist);
        btnChoose = findViewById(R.id.button_choose);
        muploadlistview = findViewById(R.id.upload_queue);
        btnDone = findViewById(R.id.button_done);
        tTitle = findViewById(R.id.edit_title);
        tContent = findViewById(R.id.edit_content);
        date_modified = date_created = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //firebase reference initialise
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();

        user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Sorry, you need to login to do that!", Toast.LENGTH_SHORT).show();
            finish();
        }


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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        location=new GeoPoint(latitude,longitude);
        geoHash= GeoHash.encodeHash(new LatLong(latitude,longitude));
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadImage(Uri fileuri) {
        String filename = getFileName(fileuri);
        if (fileuri != null) {

            StorageReference fileupload = storageReference.child("images").child(filename);


            fileupload.putFile(fileuri).addOnSuccessListener(taskSnapshot -> {
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


                            icontent = new Content(tTitle.getText().toString().trim().replaceAll( "~"," " ), tContent.getText().toString(), Mmedia);
                            marker = new Marker(null,tTitle.getText().toString().trim().replaceAll( "~","" ), user.getDisplayName(), location,geoHash, date_created, date_modified, 0, 0, 0, 0, 0,icontent);
                            entertodb();


                        }
                        //Do what you want with the url
                    }

                });
            }).addOnFailureListener(exception -> {
                Log.e(TAG, "yeeeeeee" + exception.toString());
                // Handle unsuccessful uploads
            });
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
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
        marker = new Marker(null,tTitle.getText().toString().trim(), user.getDisplayName(),location,geoHash, date_created, date_modified, 0, 0, 0, 0, 0,icontent);
        entertodb();


    }
    private void entertodb(){
        firestore.collection("markers")
                .add(marker)
                .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        firestore.collection( "markers" ).document(documentReference.getId()).update( "markerid",documentReference.getId() );
                        firestore.collection("users").document(user.getUid()).update("markers", FieldValue.arrayUnion(markerId)).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

                    }
                } )

                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


        finish();

    }

}

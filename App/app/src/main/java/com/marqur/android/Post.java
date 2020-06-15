package com.marqur.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class Post extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.post);
        ImageView imageView = (ImageView) findViewById( R.id.post_image );
        TextView title = (TextView) findViewById( R.id.post_title );
        TextView details = (TextView) findViewById( R.id.post_details );
        Intent i = getIntent();
        String url=i.getStringExtra( "picurl" );
        title.setText( i.getStringExtra( "mar_title" ) );
        details.setText( i.getStringExtra( "mar_details" ) );
        if(url!=null) {
            Glide
                    .with( getApplicationContext() )
                    .load( url )
                    .centerCrop()
                    .placeholder( R.drawable.progressrot )
                    .into( imageView );
        }
        else
        {
            imageView.setImageResource( R.mipmap.marker );
        }


    }
}

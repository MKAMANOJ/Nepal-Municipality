package com.nabinbhandari.municipality.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nabinbhandari.municipality.R;

/**
 * Created at 11:32 PM on 1/8/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContentActivity extends AppCompatActivity {

    public static final String EXTRA_CONTENT = "extra_content";
    private Content content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        if (getIntent().hasExtra(EXTRA_CONTENT)) {
            content = (Content) getIntent().getSerializableExtra(EXTRA_CONTENT);
            start();
        } else {
            finish();
        }
    }

    private void start() {
        content.content_type = content.content_type.toLowerCase();
        FrameLayout contentHolder = findViewById(R.id.content_holder);
        if (content.content_type.contains("png") || content.content_type.contains("jpg") ||
                content.content_type.contains("jpeg") || content.content_type.contains("gif")) {
            PhotoView photoView = new PhotoView(this);
            contentHolder.addView(photoView);
            Glide.with(photoView).load(content.getUrl()).into(photoView);
        } else if (content.content_type.equals("pdf")) {
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unsupported content type!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onClickDownload(View view) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
    }

}

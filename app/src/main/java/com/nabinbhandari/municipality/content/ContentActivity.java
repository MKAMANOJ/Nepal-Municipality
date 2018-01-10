package com.nabinbhandari.municipality.content;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.nabinbhandari.downloader.FileDownloader;
import com.nabinbhandari.downloader.LoadCallback;
import com.nabinbhandari.municipality.R;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
            final PhotoView photoView = new PhotoView(this);
            contentHolder.addView(photoView);
            //Glide.with(photoView).load(content.getUrl()).into(photoView);

            try {
                FileDownloader a = new FileDownloader(new File(Environment.getExternalStorageDirectory(), "myCache"));
                a.loadBitmap(content.getUrl(), null, new LoadCallback<Bitmap>() {
                    @Override
                    public void onComplete(@Nullable final Bitmap output, @Nullable final Throwable t,
                                           @Nullable final String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (output != null) {
                                    photoView.setImageBitmap(output);
                                }
                                if (t != null) {
                                    t.printStackTrace();
                                }
                                if (message != null) {
                                    System.err.println(message);
                                    Toast.makeText(ContentActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (content.content_type.equals("pdf")) {
            final PDFView pdfView = new PDFView(this, null);
            contentHolder.addView(pdfView);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(content.getUrl());
                        pdfView.fromStream(url.openStream())
                                .enableAntialiasing(true)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(final Throwable t) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ContentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Unsupported content type!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onClickDownload(View view) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
    }

}

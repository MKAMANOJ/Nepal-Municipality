package com.nabinbhandari.municipality.content;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.nabinbhandari.downloader.FileDownloader;
import com.nabinbhandari.downloader.LoadCallback;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.retrofit.ImageUtils;
import com.nabinbhandari.retrofit.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created at 11:32 PM on 1/8/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContentActivity extends AppCompatActivity {

    public static final String EXTRA_CONTENT = "extra_content";

    private Content content;

    private ProgressDialog progressDialog;
    private Call<ResponseBody> currentCall;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void start() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        progressDialog.show();
        String type = content.content_type.toLowerCase();
        if (type.contains("png") || type.contains("jpg") || type.contains("jpeg") || type.contains("gif")) {
            handleImage();
        } else if (content.content_type.equals("pdf")) {
            handlePDF();
        } else {
            Toast.makeText(this, "Unsupported content type!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void handleImage() {
        final PhotoView photoView = new PhotoView(this);
        FrameLayout contentHolder = findViewById(R.id.content_holder);
        contentHolder.addView(photoView);

        try {
            FileDownloader downloader = new FileDownloader(this);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            currentCall = downloader.loadBitmap(content.getUrl(), options, new LoadCallback<Bitmap>() {
                @Override
                public void onComplete(@Nullable final Bitmap output, @Nullable final Throwable t,
                                       @Nullable final String message) {
                    if (t != null) new Exception(t).printStackTrace();
                    if (message != null) Utils.showToastOnUI(photoView, message);
                    if (output != null) ImageUtils.setBitmapOnUi(photoView, output);
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePDF() {
        final PDFView pdfView = new PDFView(this, null);
        FrameLayout contentHolder = findViewById(R.id.content_holder);
        contentHolder.addView(pdfView);

        try {
            FileDownloader downloader = new FileDownloader(this);

            currentCall = downloader.loadFile(content.getUrl(), new LoadCallback<File>() {
                @Override
                public void onComplete(@Nullable File output, @Nullable Throwable t,
                                       @Nullable String message) {
                    if (t != null) new Exception(t).printStackTrace();
                    if (message != null) Utils.showToastOnUI(pdfView, message);
                    if (output != null) {
                        try {
                            FileInputStream in = new FileInputStream(output);
                            pdfView.fromStream(in)
                                    .enableAntialiasing(true)
                                    .spacing(10)
                                    .onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            t.printStackTrace();
                                            Utils.showToastOnUI(pdfView, t.getMessage());
                                        }
                                    })
                                    .load();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickDownload(View view) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (currentCall != null) currentCall.cancel();
        super.onDestroy();
    }

}

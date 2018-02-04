package com.nabinbhandari.municipality.content;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.nabinbhandari.downloader.FileDownloader;
import com.nabinbhandari.downloader.LoadCallback;
import com.nabinbhandari.imageviewer.Utils;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.CKEditorFragment;
import com.nabinbhandari.municipality.MainActivity;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.municipality.menu.Category;

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

    private boolean destroyed = false;
    private FloatingActionButton downloadButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        downloadButton = findViewById(R.id.downloadButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        try {
            if (intent.hasExtra(EXTRA_CONTENT)) {
                content = (Content) intent.getSerializableExtra(EXTRA_CONTENT);
                setTitle(content.title);
                start();
            } else throw new Exception("No content!");
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            // will never happen.
            AppUtils.printStackTrace(e);
            Toast.makeText(this, "Unspecified Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
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
        downloadButton.setVisibility(View.GONE);
        progressDialog.show();
        String type = content.content_type.toLowerCase();
        FrameLayout contentHolder = findViewById(R.id.content_holder);
        contentHolder.removeAllViews();
        switch (type) {
            case "image":
                handleImage();
                break;
            case "pdf":
                handlePDF();
                break;
            case "html":
                handleHTML();
                break;
            default:
                Toast.makeText(this, "Unsupported content type!", Toast.LENGTH_SHORT).show();
                finish();
                break;
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
                    if (output != null) Utils.setBitmapOnUi(photoView, output);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResult(t, message);
                        }
                    });
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                public void onComplete(@Nullable File output, @Nullable final Throwable t,
                                       @Nullable final String message) {
                    if (output != null) {
                        try {
                            FileInputStream in = new FileInputStream(output);
                            pdfView.fromStream(in)
                                    .enableAntialiasing(true)
                                    .spacing(10)
                                    .onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            AppUtils.printStackTrace(t);
                                            Utils.showToastOnUI(pdfView, t.getMessage());
                                        }
                                    })
                                    .load();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResult(t, message);
                        }
                    });
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleHTML() {
        progressDialog.hide();
        FragmentManager fragmentManager = getSupportFragmentManager();
        String reference = Category.findSlugById(content.file_category_id) + "/" + content.key + "/content";
        CKEditorFragment fragment = CKEditorFragment.newInstance(reference);
        fragmentManager.beginTransaction().replace(R.id.content_holder, fragment).commit();
    }

    private void onResult(Throwable t, String message) {
        if (destroyed) return;
        progressDialog.hide();
        if (message != null) {
            if (message.contains("internet connection")) {
                message = getString(R.string.error_internet_problem);
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        downloadButton.setVisibility(View.VISIBLE);
        if (t != null) {
            AppUtils.printStackTrace(new Exception(t));
            downloadButton.setImageResource(android.R.drawable.ic_menu_revert);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start();
                }
            });
        } else {
            downloadButton.setImageResource(android.R.drawable.stat_sys_download);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Permissions.check(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            null, new PermissionHandler() {
                                @Override
                                public void onGranted() {
                                    downloadImpl();
                                }
                            });
                }
            });
        }
    }

    private void downloadImpl() {
        try {
            FileDownloader downloader = new FileDownloader(this);
            File cacheFile = downloader.getCacheFile(content.getUrl());
            if (!cacheFile.exists()) {
                throw new IOException("Unable to save file.");
            }
            FileInputStream in = new FileInputStream(cacheFile);
            File outDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name));
            File outFile = new File(outDir, content.original_filename);
            FileDownloader.saveStream(in, outFile);
            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{outFile.getAbsolutePath()}, null, null);
            Toast.makeText(this, "File saved to : " + outFile.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // never happens
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (currentCall != null) currentCall.cancel();
        progressDialog.dismiss();
        destroyed = true;
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        if (MainActivity.isBackground) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

}

package org.sil.languageforgeweb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;
    private View mProgressBar;

    private WebView view;

    private static final int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    public ValueCallback<Uri[]> uploadMultipleCallback;
    public ValueCallback<Uri> uploadSingleCallback;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED) uploadMultipleCallback.onReceiveValue(null);
        else if(requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK) {
            uploadMultipleCallback.onReceiveValue(new Uri[] {Uri.parse(data.getDataString())});
        }
        uploadMultipleCallback = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        view = (WebView) mContentView;

        // Setup Progress Indicator
        ProgressBar mProgress = (ProgressBar) mProgressBar;
        mProgress.setIndeterminate(true);
        view.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setVisibility(View.VISIBLE);
                if(progress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                uploadSingleCallback = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1+ only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                uploadSingleCallback = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                uploadSingleCallback = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMultipleCallback != null) {
                    uploadMultipleCallback.onReceiveValue(null);
                    uploadMultipleCallback = null;
                }

                uploadMultipleCallback = filePathCallback;
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMultipleCallback = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        view.setWebViewClient(new WebViewClient());

        // Restore last URL
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String currentUrl = settings.getString("currentUrl", null);


        if (TextUtils.isEmpty(currentUrl)) {
            view.loadUrl("https://languageforge.org");
        } else {
            view.loadUrl(currentUrl);
        }

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAppCacheEnabled(true);


    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        WebView view = (WebView) mContentView;
        editor.putString("currentUrl", view.getUrl());

        // Commit the edits!
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    WebView view = (WebView) mContentView;
                    if (view.canGoBack()) {
                        view.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}

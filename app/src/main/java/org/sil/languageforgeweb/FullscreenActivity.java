package org.sil.languageforgeweb;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
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

public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;
    private View mProgressBar;

    private WebView view;

//    private static final int FILECHOOSER_RESULTCODE = 1;
    public static final int REQUEST_SELECT_FILE = 100;
    public ValueCallback<Uri[]> uploadMultipleCallback;
    public ValueCallback<Uri> uploadSingleCallback;
    private static boolean multipleUpload = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult");
        if(resultCode == RESULT_CANCELED) {
            if(multipleUpload) uploadMultipleCallback.onReceiveValue(null);
            else uploadSingleCallback.onReceiveValue(null);
        }
        else if(requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK) {
            if(multipleUpload) uploadMultipleCallback.onReceiveValue(new Uri[] {Uri.parse(data.getDataString())});
            else uploadSingleCallback.onReceiveValue(Uri.parse(data.getDataString()));
        }
        uploadSingleCallback = null;
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

            /*
             * To the best of my understanding, file selection in Android WebView was not officially
             * supported until Android 5. Previous versions had various callbacks that were not
              * officially supported and therefore broke in subsequent releases. Android 4.4 removed
              * the unsupported callbacks. The result is that file selection works prior to Android
              * 4.4 (unofficially supported) and 5.0+ (officially supported). Android 4.4 does not
              * support file selection in WebView. The onShowFileChooser() listener is for Android
              * 5.0+. All the others are for earlier versions.
              * See https://github.com/delight-im/Android-AdvancedWebView/issues/4
              * - @Nateowami (Nathaniel Paulus)
             */

            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                startActivityForFileSelect(uploadMsg);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                startActivityForFileSelect(uploadMsg);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                startActivityForFileSelect(uploadMsg);
            }

            private void startActivityForFileSelect(ValueCallback<Uri> uploadMsg) {
                startActivityForFileSelect(uploadMsg, null);
            }

            private void startActivityForFileSelect(ValueCallback<Uri> uploadMsg, ValueCallback<Uri[]> uploadMsgs) {
                uploadSingleCallback = uploadMsg;
                uploadMultipleCallback = uploadMsgs;
                multipleUpload = uploadMsgs != null;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_SELECT_FILE);
            }

            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                startActivityForFileSelect(null, filePathCallback);
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
        view.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setWebContentsDebuggingEnabled(true);
        }

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

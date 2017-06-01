package org.sil.languageforgeweb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.webview);

        WebView view = (WebView) mContentView;

        // Restore last URL
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String currentUrl = settings.getString("currentUrl", null);

        if (TextUtils.isEmpty(currentUrl)) {
            view.setWebViewClient(new WebViewClient());
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

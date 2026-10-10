package com.pokestory.idle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 게임 본체는 GitHub Pages에 있고, 이 앱은 그걸 띄워주는 창입니다.
 * 게임을 수정해서 올리면 APK를 다시 설치하지 않아도 다음 실행 때 최신 버전이 열립니다.
 */
public class MainActivity extends Activity {

    private static final int BG = Color.parseColor("#12151d");
    private WebView web;
    private boolean showingOffline = false;

    private static final String OFFLINE_HTML =
        "<html><head><meta name='viewport' content='width=device-width,initial-scale=1'></head>"
        + "<body style='background:#12151d;color:#eceef5;font-family:sans-serif;display:flex;flex-direction:column;"
        + "align-items:center;justify-content:center;height:90vh;text-align:center;padding:0 24px'>"
        + "<h2>인터넷에 연결할 수 없어요</h2>"
        + "<p style='color:#8e96aa'>처음 실행할 때와 업데이트를 받을 때는 인터넷이 필요합니다.</p>"
        + "<a href='retry://go' style='margin-top:16px;padding:12px 24px;border-radius:10px;"
        + "background:#f2b84b;color:#1d1504;font-size:16px;text-decoration:none'>다시 시도</a></body></html>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        web.setBackgroundColor(BG);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // 세이브 데이터(localStorage) 저장
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setTextZoom(100);             // 폰 글꼴 크기 설정 때문에 화면이 깨지지 않게

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if ("retry".equals(u.getScheme())) {
                    showingOffline = false;
                    view.loadUrl(BuildConfig.GAME_URL);
                    return true;
                }
                Uri game = Uri.parse(BuildConfig.GAME_URL);
                if (u.getHost() != null && u.getHost().equals(game.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame() && !showingOffline) {
                    showingOffline = true;
                    view.loadDataWithBaseURL(null, OFFLINE_HTML, "text/html", "utf-8", null);
                }
            }
        });

        web.loadUrl(BuildConfig.GAME_URL);
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기를 눌러도 게임이 꺼지지 않고 홈으로만 나갑니다 (방치형이라).
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
    }

    @Override
    protected void onPause() {
        if (web != null) web.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }
}

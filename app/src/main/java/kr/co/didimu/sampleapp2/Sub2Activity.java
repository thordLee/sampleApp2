package kr.co.didimu.sampleapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Sub2Activity extends BaseActivity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);

        //웹뷰 설정
        webView = (WebView)findViewById(R.id.webview3);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        Sub2Activity.MyWebViewClient webViewClient = new Sub2Activity.MyWebViewClient();
        //webView.setWebChromeClient(new Sub2Activity.MyWebChromeClient());
        webView.setWebViewClient(webViewClient);

        String userAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent+" APP_HANNAME_Android");


        webView.loadUrl("http://www.hanname.com/mobile");
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Uri url = request.getUrl();
            if(url.toString().indexOf("hanname.com") > -1 ) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            startActivity(intent);

            return true;
        }



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //백프레스키만을 위해 지정한다면 여기도 괜찮겠죠?
        //if(this.webView.canGoBack()){
        //this.webView.goBack();
        //}
    }

}

package kr.co.didimu.sampleapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.net.URISyntaxException;

public class SubActivity extends BaseActivity {

    private WebView webView = null;
    WebView childView;

    WebSettings webSetting;
    long lastTimeBackPressed;
    ProgressDialog progressDialog;
    String myUrl="http://www.hanname.com";
    String childURL = "";
    int count =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        //웹뷰 설정

        webView = (WebView)findViewById(R.id.webview2);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//스크립트사용가능

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);

        progressDialog = new ProgressDialog(SubActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);



        //SubActivity.MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl("http://www.hanname.com/mobile/");



    }

    private class MyWebViewClient extends WebViewClient {
        public static final String INTENT_URL_START = "intent:";
        public static final String INTENT_FALLBACK_URL = "browser_fallback_url";
        public static final String URI_SCHEME_MARKET = "market://details?id=";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.toLowerCase().startsWith(INTENT_URL_START) || !url.toLowerCase().startsWith("http")) {
                Intent parsedIntent = null;
                try {
                    parsedIntent = Intent.parseUri(url, 0);
                    startActivity(parsedIntent);
                } catch(ActivityNotFoundException | URISyntaxException e) {
                    return doFallback(view, parsedIntent);
                }
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        private boolean doFallback(WebView view, Intent parsedIntent) {
            if (parsedIntent==null) {
                return false;
            }
            String fallbackUrl = parsedIntent.getStringExtra(INTENT_FALLBACK_URL);
            if (fallbackUrl != null) {
                view.loadUrl(fallbackUrl);
                return true;
            }
            String packageName = parsedIntent.getPackage();
            if (packageName != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + packageName)));
                return true;
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            myUrl = url;

            progressDialog.setTitle("한네임 - 셀프작명");
            progressDialog.setMessage("Loading");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            progressDialog.dismiss();

            myUrl = url;
            if (!childURL.equals("") || !childURL.equals(null) || !childURL.isEmpty()) {
                childURL = "";
                webView.removeView(childView);
            }
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            WebView newWebView = new WebView(SubActivity.this);
            WebSettings newWebSettings = newWebView.getSettings();
            newWebSettings.setJavaScriptEnabled(true);


            final Dialog dialog = new Dialog(SubActivity.this);
            dialog.setContentView(newWebView);

            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
            dialog.show();


            newWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                    childView.loadUrl("javascript:self.close();");
                    //window.removeView(childView);
                }
            });
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;


            /*
            count = 1;
            webView.removeAllViews();
            childView = new WebView(SubActivity.this);
            childView.getSettings().setJavaScriptEnabled(true);
            childView.setWebChromeClient(this);

             childView.setWebViewClient(new WebViewClient() {
                 @Override
                 public void onPageStarted(WebView view, String url, Bitmap favicon) {
                     super.onPageStarted(view, url, favicon);

                     childURL = url;

                     if (count == 1) {
                         count = 0;
                         //if (childURL.contains("웹브라우저로 띄우고싶은 url")) {
                             webView.removeView(childView);
                             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(childURL));
                             startActivity(intent);
                             childURL="";
                        // }
                     }
                 }

                 @Override
                 public void onPageFinished(WebView view, String url) {
                     super.onPageFinished(view, url);
                     count =1;
                 }
             });

             childView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
             webView.addView(childView);
             WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
             transport.setWebView(childView);
             resultMsg.sendToTarget();
             return true;
            */

            /*
            WebView newWebView = new WebView(SubActivity.this);
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            final Dialog dialog = new Dialog(SubActivity.this);
            dialog.setContentView(newWebView);
            dialog.show();
            newWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });
            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
            */


            //return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }



        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            final JsResult finalRes = result;
            myUrl = url;

            //AlertDialog 생성
            new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalRes.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
            return true;
          //  return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            final JsResult finalRes = result;
            myUrl = url;

            //AlertDialog 생성
            new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalRes.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
            //return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            final JsResult finalRes = result;
            myUrl = url;
            new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalRes.confirm();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalRes.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
            //return super.onJsConfirm(view, url, message, result);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();

        webView.pauseTimers();
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

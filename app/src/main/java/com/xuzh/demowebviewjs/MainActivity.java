package com.xuzh.demowebviewjs;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	WebView mWebView;


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		// Código de configuração
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		// Suporte js
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		// Definir a cor de fundo transparente
		mWebView.setBackgroundColor(Color.rgb(96, 96, 96));
		mWebView.setWebViewClient(new WebViewClientDemo());//Adicione uma página à classe de ouvintes correspondente
		//Carregar html com js
		mWebView.loadData("", "text/html", null);
		mWebView.loadUrl("file:///android_asset/test.html");




		Intent intent = new Intent();
		intent.setPackage("woyou.aidlservice.jiuiv5");
		intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
		startService(intent);//Iniciar serviço de impressão
		bindService(intent, connService, Context.BIND_AUTO_CREATE);
	}

	class WebViewClientDemo extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//
			//Ao abrir um novo link, o uso do WebView atual não usará outros navegadores no sistema
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			 /**
	         *
			  * Registre JavascriptInterface, onde o nome "lee" é levado casualmente.Se você usar "lee", use lee.método name em html ()
	         *
			  * Você pode chamar o mesmo método de nome em MyJavascriptInterface e os parâmetros devem ser consistentes.
	         */
			mWebView.addJavascriptInterface(new JsObject(), "lee");
		}

	}

	class JsObject {

		@JavascriptInterface
		public void funAndroid(final String i) {
			Toast.makeText(getApplicationContext(), "Chame o método local funAndroid via JS" + i,	Toast.LENGTH_SHORT).show();

			try {
				woyouService.printBarCode("1234567890", 8, 162, 2, 2,	callback);

				woyouService.printText(woyouService.getPrinterSerialNo( ) + "\n\n\n\n\n",callback);

				//woyouService.printerSelfChecking(callback);//O método AIDL usado aqui para imprimir
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void initViews() {
		mWebView = (WebView) findViewById(R.id.wv_view);
	}

	private IWoyouService woyouService;

	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			woyouService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			woyouService = IWoyouService.Stub.asInterface(service);
		}
	};

	ICallback callback = new ICallback.Stub() {

		@Override
		public void onRunResult(boolean success) throws RemoteException {
		}

		@Override
		public void onReturnString(final String value) throws RemoteException {
		}

		@Override
		public void onRaiseException(int code, final String msg)
				throws RemoteException {
		}
	};

}

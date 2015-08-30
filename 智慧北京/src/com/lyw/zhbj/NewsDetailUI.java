package com.lyw.zhbj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.TextSize;
import android.widget.ProgressBar;

public class NewsDetailUI extends Activity implements OnClickListener {
	private String url;
	private WebSettings settings;
	private int currentSelectTextSizePosition = 2;
	private int tempSelectTextSizePosition; // �ڶԻ�������ʱѡ�������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_detail);

		Intent intent = getIntent();
		url = intent.getStringExtra("url");

		initView();
	}

	private void initView() {
		findViewById(R.id.ib_title_bar_menu).setVisibility(View.GONE);
		findViewById(R.id.tv_title_bar_title).setVisibility(View.GONE);

		findViewById(R.id.ib_title_bar_back).setVisibility(View.VISIBLE);
		findViewById(R.id.ib_title_bar_textsize).setVisibility(View.VISIBLE);
		findViewById(R.id.ib_title_bar_share).setVisibility(View.VISIBLE);

		findViewById(R.id.ib_title_bar_back).setOnClickListener(this);
		findViewById(R.id.ib_title_bar_textsize).setOnClickListener(this);
		findViewById(R.id.ib_title_bar_share).setOnClickListener(this);

		WebView mWebView = (WebView) findViewById(R.id.wv_news_detail);
		final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.pb_news_detail);

		// ����WebView�����Ϣ
		settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // ����javascript�ű�
		// �����ϷŴ����С��ť������
		settings.setBuiltInZoomControls(true);
		// ����˫���Ŵ������С
		settings.setUseWideViewPort(true);

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// ��ǰҳ����������, ��Ҫ�ѽ���Ȧ������
				mProgressBar.setVisibility(View.GONE);
				// System.out.println("ҳ����������");
			}
		});

		mWebView.loadUrl(url); // ����ָ�����ӵ�ַ������
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_title_bar_back:// ����
			finish();
			break;
		case R.id.ib_title_bar_textsize:// �ı������С
			showTextSizeDialog();
			break;
		case R.id.ib_title_bar_share:// ����

			break;

		default:
			break;
		}
	}

	// �ı������С�Ի���
	private void showTextSizeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(NewsDetailUI.this);
		builder.setTitle("ѡ�������С");
		String[] items = { "���������", "�������", "��������", "С������", "��С������" };
		tempSelectTextSizePosition = currentSelectTextSizePosition;
		builder.setSingleChoiceItems(items, currentSelectTextSizePosition,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						tempSelectTextSizePosition = which;
						//dialog.dismiss();
					}
				});

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentSelectTextSizePosition = tempSelectTextSizePosition;
				changeWebViewTextSize();

			}
		});
		builder.setNegativeButton("ȡ��",null);
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	/**
	 * ����currentSelectTextSizePosition�������ı�����
	 */
	protected void changeWebViewTextSize() {
		switch (currentSelectTextSizePosition ) {
		case 0:
			settings.setTextSize(TextSize.LARGEST);
			break;
		case 1:
			settings.setTextSize(TextSize.LARGER);
			break;
		case 2:
			settings.setTextSize(TextSize.NORMAL);
			break;
		case 3:
			settings.setTextSize(TextSize.SMALLER);
			break;
		case 4:
			settings.setTextSize(TextSize.SMALLEST);
			break;
		default:
			break;
		}
	}

}

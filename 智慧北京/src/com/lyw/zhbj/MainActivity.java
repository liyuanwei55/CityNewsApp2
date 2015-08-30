package com.lyw.zhbj;

import com.lyw.zhbj.utils.SharesPreferenceUntils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements AnimationListener {
	public static final String IS_MAINUI_OPEN = "IS_MAINUI_OPEN";
	private LinearLayout ll_bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		ll_bg = (LinearLayout) findViewById(R.id.ll_bg);

		RotateAnimation rotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(2000);
		rotate.setFillAfter(true);

		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(2000);
		scale.setFillAfter(true);
		
		AlphaAnimation alphaAnima = new AlphaAnimation(0, 1);
		alphaAnima.setDuration(2000);
		alphaAnima.setFillAfter(true);
		
		AnimationSet setAnima = new AnimationSet(false);
		setAnima.addAnimation(rotate);
		setAnima.addAnimation(scale);
		setAnima.addAnimation(alphaAnima);
		
		setAnima.setAnimationListener(this);
		
		ll_bg.startAnimation(setAnima);
		
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		boolean is_open = SharesPreferenceUntils.getBoolean(this, IS_MAINUI_OPEN, false);
		if(is_open){
			Intent intent = new Intent(MainActivity.this, MainUIActivity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(intent);
		}
		
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

}

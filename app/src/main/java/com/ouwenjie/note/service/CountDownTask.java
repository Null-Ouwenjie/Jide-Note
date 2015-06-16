package com.ouwenjie.note.service;

import android.os.AsyncTask;
import android.widget.Button;

import com.ouwenjie.note.utils.LogUtils;

/**
 * 倒计时按钮倒计时任务类
 * 
 * @author Sam Sun
 * @version $Revision:1.0.0, $Date: 2013-1-17 上午10:56:59
 */
public class CountDownTask extends AsyncTask<Void, Integer, Void> {

	private int maxSecond = 60; // 倒计时秒数
	private String title = "重发验证码"; // 倒计时按钮标题
	private Button countDownBtn; // 倒计时按钮

	public CountDownTask(Button countDownBtn) {

		this.countDownBtn = countDownBtn;
	}

	public CountDownTask(Button countDownBtn, String title) {

		this.countDownBtn = countDownBtn;
		this.title = title;
	}

	public CountDownTask(Button countDownBtn, int mSec) {

		this.countDownBtn = countDownBtn;
		this.maxSecond = mSec;
	}

	public CountDownTask(Button countDownBtn, int mSec, String title) {

		this.countDownBtn = countDownBtn;
		this.maxSecond = mSec;
		this.title = title;
	}

	@Override
	protected Void doInBackground(Void... params) {

		int count = maxSecond;

		while (!isCancelled() && count >= 0) {

			try {
				LogUtils.d("倒计时==" + count);
				publishProgress(count);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				--count;
			}
		}

		return null;
	}

	@Override
	protected void onPreExecute() {
		// 预处理
		countDownBtn.setEnabled(false);
		countDownBtn.setBackgroundResource(android.support.v7.appcompat.R.color.material_deep_teal_200);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		// 更新进度
		countDownBtn.setText(title + "(" + values[0] + ")");

		if (values[0] == 0) { // 计数完成，则复位

			countDownBtn.setEnabled(true);
			countDownBtn.setBackgroundResource(android.support.v7.appcompat.R.color.material_deep_teal_500);
			countDownBtn.setText(title);
		}
	}
}

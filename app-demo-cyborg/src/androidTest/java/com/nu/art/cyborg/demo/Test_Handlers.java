/*
 * cyborg-core is an extendable  module based framework for Android.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.cyborg.demo;

import android.os.Handler;
import android.os.Message;
import android.support.test.runner.AndroidJUnit4;

import com.nu.art.belog.Logger;
import com.nu.art.cyborg.core.modules.ThreadsModule;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.nu.art.cyborg.core.CyborgBuilder.getModule;

/**
 * Created by TacB0sS on 05/04/2018.
 */
@RunWith(AndroidJUnit4.class)
public class Test_Handlers
	extends Logger {

	Handler handler;

	@Test
	public void test_RemoveRunnableNotWorking() {
		for (int i = 0; i < 10; i++) {
			test_RemoveRunnableNotWorking_();
		}
	}

	public void test_RemoveRunnableNotWorking_() {
		class LogRunnable
			implements Runnable {

			final String log;
			int count = 0;

			private LogRunnable(String log) {this.log = log;}

			@Override
			public void run() {
				logDebug(log);
				if (count == 10)
					return;

				count++;
				handler.postDelayed(this, 300);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Runnable runnable1 = new LogRunnable("Runnable-1");
		//		Runnable runnable2 = new LogRunnable("Runnable-2");

		handler = getModule(ThreadsModule.class).getDefaultHandler("test-handler");

		handler.post(runnable1);
		//		handler.postDelayed(runnable2, 100);
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logInfo("Removing Runnable 1");
		handler.removeCallbacks(runnable1);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_RemoveRunnable() {
		class LogRunnable1
			implements Runnable {

			final String log;
			int count = 0;

			private LogRunnable1(String log) {this.log = log;}

			@Override
			public void run() {
				logDebug(log);
				if (count == 10)
					return;

				count++;
				Message m = Message.obtain(handler, this);
				m.obj = this;
				handler.sendMessageDelayed(m, 3000);
			}
		}

		Runnable runnable1 = new LogRunnable1("Runnable-1");
		Runnable runnable2 = new LogRunnable1("Runnable-2");

		handler = getModule(ThreadsModule.class).getDefaultHandler("test-handler");

		handler.post(runnable1);
		handler.postDelayed(runnable2, 100);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		logInfo("Removing Runnable 1");
		handler.removeCallbacksAndMessages(runnable1);

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

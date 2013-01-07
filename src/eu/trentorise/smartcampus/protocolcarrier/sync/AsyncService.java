/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.protocolcarrier.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import eu.trentorise.smartcampus.protocolcarrier.common.Constants;
import eu.trentorise.smartcampus.protocolcarrier.common.PcIntents;
import eu.trentorise.smartcampus.protocolcarrier.common.Setup;
import eu.trentorise.smartcampus.protocolcarrier.common.Status;

public class AsyncService extends Service {

	private Context context;
	private Status status;
	private BroadcastReceiver mConnReceiver;
	private BroadcastReceiver actionsReceiver;
	private Map<String, Timer> timersMap = new HashMap<String, Timer>();
	private Map<String, AsyncTimerTask> timerTasksMap = new HashMap<String, AsyncTimerTask>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		status = new Status(context);

		/*
		 * ConnectivityManager Broadcast Receiver
		 */
		mConnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				// String reason =
				// intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
				// boolean isFailover =
				// intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER,
				// false);
				//
				// NetworkInfo currentNetworkInfo = (NetworkInfo) intent
				// .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				// NetworkInfo otherNetworkInfo = (NetworkInfo) intent
				// .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

				if (status.isOnline() && noConnectivity) {
					// online --> offline
					status.setOnline(false);

					for (Entry<String, AsyncTimerTask> entry : timerTasksMap.entrySet()) {
						AsyncTimerTask att = entry.getValue();
						att.cancel();
					}

					for (Entry<String, Timer> entry : timersMap.entrySet()) {
						Timer timer = entry.getValue();
						timer.cancel();
						timer.purge();
					}
					timersMap.clear();

					Log.d(this.getClass().getName(), "Going offline, timers stopped");
				} else if (!status.isOnline() && !noConnectivity) {
					// offline --> online
					status.setOnline(true);

					for (Entry<String, AsyncTimerTask> entry : timerTasksMap.entrySet()) {
						String appToken = entry.getKey();
						AsyncTimerTask att = entry.getValue();
						if (att.isEnabled()) {
							att = new AsyncTimerTask(context, appToken, status);
							att.setEnabled(true);
							timerTasksMap.put(appToken, att);
							Timer timer = new Timer(appToken, true);
							timersMap.put(appToken, timer);
							timer.scheduleAtFixedRate(att, Setup.TIMERTASK_ASYNC_DELAY, Setup.TIMERTASK_ASYNC_PERIOD);
						}
					}

					Log.d(this.getClass().getName(), "Going online, timers started");
				}
			}
		};

		registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		/*
		 * Actions Broadcast Receiver
		 */
		actionsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String appToken = intent.getStringExtra(Constants.INTENT_EXTRA_APP_TOKEN);

				if (appToken != null && PcIntents.INTENT_START.equals(intent.getAction())) {
					// start
					Timer timer = timersMap.get(appToken);

					if (timer == null) {
						AsyncTimerTask att = new AsyncTimerTask(context, appToken, status);
						att.setEnabled(true);
						timerTasksMap.put(appToken, att);

						timer = new Timer(appToken, true);
						timersMap.put(appToken, timer);
						timer.scheduleAtFixedRate(att, Setup.TIMERTASK_ASYNC_DELAY, Setup.TIMERTASK_ASYNC_PERIOD);

						Log.d(this.getClass().getName(), "AsyncTimerTask for " + appToken + " started >");
					} else {
						Log.d(this.getClass().getName(), "AsyncTimerTask for " + appToken + " already started ~");
					}
				} else if (appToken != null && PcIntents.INTENT_STOP.equals(intent.getAction())) {
					// stop
					Timer timer = timersMap.get(appToken);
					AsyncTimerTask att = timerTasksMap.get(appToken);
					if (timer != null && att != null) {
						att.setEnabled(false);
						att.cancel();
						timer.cancel();
						timer.purge();
						timersMap.remove(appToken);

						Log.d(this.getClass().getName(), "AsyncTimerTask for " + appToken + " stopped []");
					}
				}
			}
		};

		registerReceiver(actionsReceiver, new IntentFilter(PcIntents.INTENT_START));
		registerReceiver(actionsReceiver, new IntentFilter(PcIntents.INTENT_STOP));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		for (Entry<String, Timer> entry : timersMap.entrySet()) {
			Timer timer = entry.getValue();
			timer.cancel();
			timer.purge();
		}
		timersMap.clear();

		unregisterReceiver(mConnReceiver);
		unregisterReceiver(actionsReceiver);
	}

}

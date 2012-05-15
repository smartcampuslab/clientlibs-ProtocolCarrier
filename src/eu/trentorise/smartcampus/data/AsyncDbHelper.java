package eu.trentorise.smartcampus.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.trentorise.smartcampus.common.Constants.Method;
import eu.trentorise.smartcampus.common.Setup;
import eu.trentorise.smartcampus.custom.AsyncMessageRequest;
import eu.trentorise.smartcampus.custom.DeferredConf;
import eu.trentorise.smartcampus.custom.MessageRequest;
import eu.trentorise.smartcampus.exceptions.InvocationException;

public class AsyncDbHelper extends SQLiteOpenHelper {

	public AsyncDbHelper(Context context) {
		super(context, Setup.DB_NAME, null, Setup.DB_VERSION);

		/*
		 * FOR DEBUG ONLY
		 */
		if (Setup.DEBUG) {
			onUpgrade(this.getWritableDatabase(), Setup.DB_VERSION, Setup.DB_VERSION);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String initQuery = "CREATE TABLE " + Setup.DB_ASYNC_TABLE;
		initQuery += " (";
		initQuery += Setup.DB_ASYNC_COL_TIMESTAMP + " INTEGER";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_UUID + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_METHOD + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_TARGETHOST + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_TARGETADDRESS + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_BODY + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_QUERY + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_CONTENTTYPE + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_APP_TOKEN + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_AUTH_TOKEN + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_CALLBACK + " TEXT";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_REQUEST_TIMEOUT + " INTEGER";
		initQuery += ", ";
		initQuery += Setup.DB_ASYNC_COL_RESPONSE_TIMEOUT + " INTEGER";
		initQuery += ");";

		db.execSQL(initQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String upgradeQuery = "DROP TABLE IF EXISTS " + Setup.DB_ASYNC_TABLE;
		db.execSQL(upgradeQuery);
		onCreate(db);
	}

	/*
	 * Query methods
	 */
	// count all AsyncMessageRequest
	public int countAllAsyncMessageRequest(String appToken) {

		int counter = 0;

		String[] projection = new String[] { Setup.DB_ASYNC_COL_UUID };
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;

		if (appToken != null && !appToken.isEmpty()) {
			selection = new String();
			selection += Setup.DB_ASYNC_COL_APP_TOKEN + "=?";
			selectionArgs = new String[] { appToken };
		} else {
			return counter;
		}

		Cursor cursor = this.getReadableDatabase().query(Setup.DB_ASYNC_TABLE, projection, selection, selectionArgs,
				groupBy, having, orderBy);

		if (cursor.moveToFirst()) {
			do {
				counter++;
			} while (cursor.moveToNext());
		}
		cursor.close();

		return counter;
	}

	// get all MessageRequests
	public Map<String, MessageRequest> getAllMessageRequests(String appToken) {

		Map<String, MessageRequest> map = new HashMap<String, MessageRequest>();

		String[] projection = new String[] { Setup.DB_ASYNC_COL_UUID, Setup.DB_ASYNC_COL_METHOD,
				Setup.DB_ASYNC_COL_TARGETHOST, Setup.DB_ASYNC_COL_TARGETADDRESS, Setup.DB_ASYNC_COL_BODY,
				Setup.DB_ASYNC_COL_QUERY, Setup.DB_ASYNC_COL_CONTENTTYPE };
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;

		if (appToken != null && !appToken.isEmpty()) {
			selection = new String();
			selection += Setup.DB_ASYNC_COL_APP_TOKEN + "=?";
			selectionArgs = new String[] { appToken };
		} else {
			return map;
		}

		Cursor cursor = this.getReadableDatabase().query(Setup.DB_ASYNC_TABLE, projection, selection, selectionArgs,
				groupBy, having, orderBy);

		if (cursor.moveToFirst()) {
			do {
				String uuid = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_UUID));
				String method = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_METHOD));
				String targetHost = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETHOST));
				String targetAddress = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETADDRESS));
				String body = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_BODY));
				String query = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_QUERY));
				String contentType = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_CONTENTTYPE));
				MessageRequest msgRequest = new MessageRequest(targetHost, targetAddress);
				msgRequest.setMethod(Method.valueOf(method));
				msgRequest.setBody(body);
				msgRequest.setQuery(query);
				msgRequest.setContentType(contentType);

				map.put(uuid, msgRequest);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return map;
	}

	// get all AsyncMessageRequests
	public List<AsyncMessageRequest> getAllAsyncMessageRequest(String appToken) {

		List<AsyncMessageRequest> list = new ArrayList<AsyncMessageRequest>();

		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;

		if (appToken != null && !appToken.isEmpty()) {
			selection = new String();
			selection += Setup.DB_ASYNC_COL_APP_TOKEN + "=?";
			selectionArgs = new String[] { appToken };
		} else {
			return list;
		}

		Cursor cursor = this.getReadableDatabase().query(Setup.DB_ASYNC_TABLE, projection, selection, selectionArgs,
				groupBy, having, orderBy);

		if (cursor.moveToFirst()) {
			do {
				String method = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_METHOD));
				String targetHost = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETHOST));
				String targetAddress = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETADDRESS));
				String body = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_BODY));
				String query = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_QUERY));
				String contentType = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_CONTENTTYPE));
				MessageRequest msgRequest = new MessageRequest(targetHost, targetAddress);
				msgRequest.setMethod(Method.valueOf(method));
				msgRequest.setBody(body);
				msgRequest.setQuery(query);
				msgRequest.setContentType(contentType);

				long timestamp = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TIMESTAMP));
				String uuid = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_UUID));
				String appTokenDb = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_APP_TOKEN));
				String authToken = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_AUTH_TOKEN));
				String callback = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_CALLBACK));
				long requestTimeout = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_REQUEST_TIMEOUT));
				long responseTimeout = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_RESPONSE_TIMEOUT));

				AsyncMessageRequest aMsgRequest = new AsyncMessageRequest(timestamp, uuid, msgRequest, appTokenDb,
						authToken, callback, requestTimeout, responseTimeout);
				list.add(aMsgRequest);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return list;
	}

	// get AsyncMessageRequest by uuid
	public AsyncMessageRequest getAsyncMessageRequestByUuid(String appToken, String uuid) throws InvocationException {

		AsyncMessageRequest aMsgRequest = null;

		String[] projection = null; // new String[] { Setup.DB_ASYNC_COL_UUID };
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;

		if (appToken != null && !appToken.isEmpty() && uuid != null && !uuid.isEmpty()) {
			selection = new String();
			selection += Setup.DB_ASYNC_COL_APP_TOKEN + "=?" + " AND " + Setup.DB_ASYNC_COL_UUID + "=?";
			selectionArgs = new String[] { appToken, uuid };
		} else {
			return aMsgRequest;
		}

		Cursor cursor = this.getReadableDatabase().query(Setup.DB_ASYNC_TABLE, projection, selection, selectionArgs,
				groupBy, having, orderBy);

		if (cursor.getCount() > 1) {
			throw new InvocationException("Duplicated request-message: " + uuid);
		}

		if (cursor.moveToFirst()) {
			do {
				String method = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_METHOD));
				String targetHost = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETHOST));
				String targetAddress = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TARGETADDRESS));
				String body = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_BODY));
				String query = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_QUERY));
				String contentType = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_CONTENTTYPE));
				MessageRequest msgRequest = new MessageRequest(targetHost, targetAddress);
				msgRequest.setMethod(Method.valueOf(method));
				msgRequest.setBody(body);
				msgRequest.setQuery(query);
				msgRequest.setContentType(contentType);

				long timestamp = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_TIMESTAMP));
				String appTokenDb = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_APP_TOKEN));
				String authToken = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_AUTH_TOKEN));
				String callback = cursor.getString(cursor.getColumnIndex(Setup.DB_ASYNC_COL_CALLBACK));
				long requestTimeout = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_REQUEST_TIMEOUT));
				long responseTimeout = cursor.getLong(cursor.getColumnIndex(Setup.DB_ASYNC_COL_RESPONSE_TIMEOUT));

				aMsgRequest = new AsyncMessageRequest(timestamp, uuid, msgRequest, appTokenDb, authToken, callback,
						requestTimeout, responseTimeout);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return aMsgRequest;
	}

	// delete all AsyncMessageRequests
	public void deleteAsyncMessageRequests(String appToken) {
		if (appToken != null && !appToken.isEmpty()) {
			String where = Setup.DB_ASYNC_COL_APP_TOKEN + "=?";
			String[] whereArgs = new String[] { appToken };
			this.getWritableDatabase().delete(Setup.DB_ASYNC_TABLE, where, whereArgs);
		} else {
			return;
		}
	}

	// delete AsyncMessageRequest by uuid
	public void deleteAsyncMessageRequestByUuid(String appToken, String uuid) {
		if (appToken != null && !appToken.isEmpty() && uuid != null && !uuid.isEmpty()) {
			String where = Setup.DB_ASYNC_COL_APP_TOKEN + "=?" + " AND " + Setup.DB_ASYNC_COL_UUID + "=?";
			String[] whereArgs = new String[] { appToken, uuid };
			this.getWritableDatabase().delete(Setup.DB_ASYNC_TABLE, where, whereArgs);
		} else {
			return;
		}
	}

	// add new AsyncMessageRequest
	public String addAsyncMessageRequest(MessageRequest msgRequest, String appToken, String authToken, String callback,
			DeferredConf deferredConf) {
		if (deferredConf == null) {
			deferredConf = new DeferredConf();
		}

		ContentValues values = new ContentValues();
		UUID messageID = UUID.randomUUID();
		values.put(Setup.DB_ASYNC_COL_UUID, messageID.toString());
		values.put(Setup.DB_ASYNC_COL_METHOD, msgRequest.getMethod().toString());
		values.put(Setup.DB_ASYNC_COL_TARGETHOST, msgRequest.getTargetHost());
		values.put(Setup.DB_ASYNC_COL_TARGETADDRESS, msgRequest.getTargetAddress());
		values.put(Setup.DB_ASYNC_COL_BODY, msgRequest.getBody());
		values.put(Setup.DB_ASYNC_COL_QUERY, msgRequest.getQuery());
		values.put(Setup.DB_ASYNC_COL_CONTENTTYPE, msgRequest.getContentType());
		values.put(Setup.DB_ASYNC_COL_APP_TOKEN, appToken);
		values.put(Setup.DB_ASYNC_COL_AUTH_TOKEN, authToken);
		values.put(Setup.DB_ASYNC_COL_CALLBACK, callback);
		values.put(Setup.DB_ASYNC_COL_REQUEST_TIMEOUT, deferredConf.getRequestTimeout());
		values.put(Setup.DB_ASYNC_COL_RESPONSE_TIMEOUT, deferredConf.getResponseTimeout());
		values.put(Setup.DB_ASYNC_COL_TIMESTAMP, System.currentTimeMillis());

		this.getWritableDatabase().insert(Setup.DB_ASYNC_TABLE, null, values);

		return messageID.toString();
	}

}

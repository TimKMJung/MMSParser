package ssoim.com.mmsparser.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.text.MessageFormat;

import ssoim.com.mmsparser.R;
import ssoim.com.mmsparser.data.Constants;
import ssoim.com.mmsparser.data.Msg;
import ssoim.com.mmsparser.views.ParserActivity;


/**
 * Created by renewinspirit on 2017. 6. 19..
 */

public class ParseMMSService extends IntentService {

public ParseMMSService() {
	super(Constants.TAG);
}

public static String parseString;



@Override
protected void onHandleIntent(@Nullable Intent intent) {
	ScanAndParseMMS();
}

public void ScanAndParseMMS() {
	
	Uri inboxUri = Uri.parse("content://mms");
	String brAddress = Constants.ADDRESS_NUM;
	
	final String[] projection = {"*"};
	
	
	ContentResolver cr = getContentResolver();
	Cursor cursor = cr.query(inboxUri, projection, null, null, null);
	if (cursor != null) {
		if (cursor.moveToFirst()) {
			do {
				
				Msg msg = new Msg(cursor.getString(cursor.getColumnIndex("_id")));
				msg.setAddress(getMmsAddr(msg.getID()));
				
				String add = getMmsAddr(msg.getID());
				
				if (add.contains(brAddress)) {
					parseString = ParseMMS(msg);
				}
				
			} while (cursor.moveToNext());
			
		}
		cursor.close();
	}
}


public String getMmsAddr(String id) {
	String sel = new String("msg_id=" + id);
	String uriString = MessageFormat.format("content://mms/{0}/addr", id);
	Uri uri = Uri.parse(uriString);
	Cursor c = getContentResolver().query(uri, null, sel, null, null);
	String name = "";
	while (c.moveToNext()) {
		String t = c.getString(c.getColumnIndex("address"));
		if (!(t.contains("insert")))
			name = name + t + " ";
	}
	c.close();
	return name;
}


private String ParseMMS(Msg msg) {
	String subStr = "";

	Uri uri = Uri.parse("content://mms/part");
	String mmsId = "mid = " + msg.getID();
	Cursor c = getContentResolver().query(uri, null, mmsId, null, null);
	while (c.moveToNext()) {
		
		String pid = c.getString(c.getColumnIndex("_id"));
		String type = c.getString(c.getColumnIndex("ct"));
		if ("text/plain".equals(type)) {
			msg.setBody(msg.getBody() + c.getString(c.getColumnIndex("text")));

			/* if you want to do sub string */
//			int startIndex = msg.getBody().indexOf("*YOUR STRING*");
//			int endIndex = startIndex + *some length*;
//			subStr = msg.getBody().substring(startIndex, endIndex);
			
			subStr = msg.getBody().toString();


			// * Notification Declarataion */
			Intent intent = new Intent(this, ParserActivity.class);
			int requestCode = 0;

			String title = Constants.NOTIFICATION_TITLE;
			String desc = Constants.NOTIFICATION_DESC;

			PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
//			Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.mipmap.push_icon)
					.setContentTitle(title)
					.setContentText(desc)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent);

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(0, noBuilder.build());

			return subStr;
		}
		
		
	}
	c.close();

	return subStr;

}
	
	
}

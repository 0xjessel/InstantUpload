package net.jessechen.test;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * @author jessechen
 *
 */
public class InstantUploadActivity extends Activity {
	
	private PhotosObserver instUploadObserver = new PhotosObserver();
	private String saved;
	private TextView tv;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tv = (TextView)findViewById(R.id.textview);
		
		this.getApplicationContext()
				.getContentResolver()
				.registerContentObserver(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false,
						instUploadObserver);
		Log.d("INSTANT", "registered content observer");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (saved != null) {
			tv.setText(saved);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.getApplicationContext().getContentResolver().unregisterContentObserver(instUploadObserver);
		Log.d("INSTANT", "unregistered content observer");
	}

	private class PhotosObserver extends ContentObserver {

		public PhotosObserver() {
			super(null);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Media media = readFromMediaStore(getApplicationContext(),
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			saved = "I detected " + media.file.getName();
			Log.d("INSTANT", "detected picture");
		}
	}

	private Media readFromMediaStore(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, "date_added DESC");
		Media media = null;
		if (cursor.moveToNext()) {
			int dataColumn = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(dataColumn);
			int mimeTypeColumn = cursor
					.getColumnIndexOrThrow(MediaColumns.MIME_TYPE);
			String mimeType = cursor.getString(mimeTypeColumn);
			media = new Media(new File(filePath), mimeType);
		}
		cursor.close();
		return media;
	}

	public class Media {
		public File file;
		public String type;

		public Media(File file, String type) {
			this.file = file;
			this.type = type;
		}
	}
}
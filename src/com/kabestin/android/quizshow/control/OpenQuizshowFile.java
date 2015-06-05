package com.kabestin.android.quizshow.control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.kabestin.android.quizshow.utilities.Constants;
import com.kabestin.android.quizshow.view.Quizshow;

public class OpenQuizshowFile implements 
	OptionsItemHandler, ActivityResultHandler, OnClickListener {
	
	private Uri outputFileUri;
	Activity source;

	public OpenQuizshowFile() {
	}

	public OpenQuizshowFile(Activity source) {
		this.source = source;
	}

	@Override
	public boolean handleOptionsItem(Activity source, MenuItem item) {
		this.source = source;
		openImageIntent();
		return true;
	}
	
	// Code an answer from David Manpearl on Stack Overflow
	// http://stackoverflow.com/questions/4455558/allow-user-to-select-camera-or-gallery-for-image

	private String getUniquePhotoName() {
		Time now = new Time();
		now.setToNow();
		return now.format2445();
	}

	private void openImageIntent() {

		// Determine Uri of camera image to save.
		final File root = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "MyDir" + File.separator);
		root.mkdirs();

		final File sdImageMainDirectory = new File(root, getUniquePhotoName());
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = source.getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
				captureIntent, 0);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("*/*");
		galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		// Chooser of filesystem options.
		Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		// Add the camera options.
		//chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
		//		cameraIntents.toArray(new Parcelable[] {}));
		
		Intent getContentIntent = FileUtils.createGetContentIntent();

	    chooserIntent = Intent.createChooser(getContentIntent, "Select a file");

		source.startActivityForResult(chooserIntent, Constants.QUIZSHOW_DATA);
		
	}

	public void handleActivityResult(Activity source, Intent sourceIntent) 
	{
		this.source = source;
		
		outputFileUri = sourceIntent.getData();
		((Quizshow)source).setDataFileUri(outputFileUri);		
	}	

	public void onClick(View v) {	
		openImageIntent();
	}
	
}

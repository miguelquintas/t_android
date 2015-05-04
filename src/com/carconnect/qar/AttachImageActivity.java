package com.carconnect.qar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import utils.StorageUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class AttachImageActivity extends Activity {

	protected static final String STATE_ATTACHED_IMAGE_FILE_URI = "STATE_ATTACHED_IMAGE_FILE_URI";
	protected static final String STATE_IMAGE_FROM_CAMERA = "STATE_IMAGE_FROM_CAMERA";

	protected static final int REQUEST_CODE_PHOTO_GALLERY = 100500;
	protected static final int REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY = 100501;

	protected File galleryImageFile;
	protected File cameraImageFile;
	protected boolean imageFromCamera = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraImageFile = StorageUtil.getOutputMediaFile(this, StorageUtil.MEDIA_TYPE_IMAGE);
		galleryImageFile = StorageUtil.getOutputMediaFile(this, StorageUtil.MEDIA_TYPE_IMAGE);
		if (savedInstanceState != null) {
			imageFromCamera = savedInstanceState.getBoolean(STATE_IMAGE_FROM_CAMERA);
			if (imageFromCamera) {
				cameraImageFile = (File) savedInstanceState.getSerializable(STATE_ATTACHED_IMAGE_FILE_URI);
			} else {
				galleryImageFile = (File) savedInstanceState.getSerializable(STATE_ATTACHED_IMAGE_FILE_URI);
			}
			showImagePreview();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(STATE_ATTACHED_IMAGE_FILE_URI, imageFromCamera ? cameraImageFile : galleryImageFile);
		outState.putBoolean(STATE_IMAGE_FROM_CAMERA, imageFromCamera);
		super.onSaveInstanceState(outState);
	}

	protected void startCamera() {
		cameraImageFile = StorageUtil.getOutputMediaFile(this, StorageUtil.MEDIA_TYPE_IMAGE);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraImageFile));
		startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY);
	}

	protected void startGallery() {
		try{
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_CODE_PHOTO_GALLERY);
		} catch(Exception e){
			Toast.makeText(getApplicationContext(), "Make sure you have an external memory card", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PHOTO_GALLERY:
			if (resultCode == Activity.RESULT_OK) {
				String realPath = StorageUtil.getRealPathFromURI(this, data.getData());
				if (realPath != null) {
					galleryImageFile = new File(realPath);
					imageFromCamera = false;
					showImagePreview();
				} else {
					Toast.makeText(this, "Image not found", Toast.LENGTH_LONG).show();
				}
			}
			break;
		case REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY:
			if (resultCode == Activity.RESULT_OK) {
				imageFromCamera = true;
				showImagePreview();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected File getAttachedImageFile() {
		return imageFromCamera ? cameraImageFile : galleryImageFile;
	}

	protected boolean isAttachedFileEmpty() {
		try {
			FileInputStream fis = new FileInputStream(getAttachedImageFile());
			int b = fis.read();
			fis.close();
			return b == -1;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
	}

	protected void showImagePreview(){
		
	}
}

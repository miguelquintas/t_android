package com.carconnect.qar;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Fragment;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class ScanFragmentActivity extends Fragment{

	Button mButton;
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
        System.loadLibrary("iconv");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activity_scan, container, false);
		
		mButton = (Button) view.findViewById(R.id.button);
		FrameLayout preview = (FrameLayout) view.findViewById(R.id.cameraPreview);
		
		autoFocusHandler = new Handler();
		mCamera = getCameraInstance();

		// Instance barcode scanner
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		mPreview = new CameraPreview(getActivity(), mCamera, previewCb, autoFocusCB);
		
		preview.addView(mPreview);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (barcodeScanned) {
					barcodeScanned = false;
					// scanText.setText("Scanning...");
					mCamera.setPreviewCallback(previewCb);
					mCamera.startPreview();
					previewing = true;
					mCamera.autoFocus(autoFocusCB);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		mPreview.getHolder().removeCallback(mPreview);
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					
					System.out.println(sym.getData());
					// scanText.setText("barcode result " + sym.getData());
					barcodeScanned = true;
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}

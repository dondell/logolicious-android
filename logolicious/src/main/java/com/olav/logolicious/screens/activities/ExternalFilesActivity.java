package com.olav.logolicious.screens.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.olav.logolicious.R;
import com.olav.logolicious.customize.adapters.ExternalFilesAdapter;
import com.olav.logolicious.customize.datamodel.ExternalFilesModel;
import com.olav.logolicious.util.FileUtil;
import com.olav.logolicious.util.GlobalClass;
import com.olav.logolicious.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExternalFilesActivity extends Activity implements OnClickListener{

	private static final String TAG 							= "ExternalFilesActivity";
	private static final String MODE_KEY 						= "mode";
	private static int MODE 									= -1;
	public static final int MODE_LOGO_UPLOADING 			    = 0;
	public static final int MODE_IMAGE_BATCHING 			    = 1;
	public static final int MODE_FOLDER 						= 2;
	// Message types
	public static final int MESSAGE_UPDATE_PC_FILELIST 			= 1;

	// Layout view
	private TextView mTitle;
	private static GridView external_files;

	public static File file;
	private FileFilter fileFilter 								= null;

	boolean mExternalStorageAvailable 							= false;
	boolean mExternalStorageWriteable 							= false;

	private String previousPathGoBack 							= "";
	// Path to the SD Card
	public static String currentPath							= "";
	private static String INTERNAL_PATH 						= null;
	private static String EXTERNAL_PATH 						= null;
	public static String SLIDE_ACTIVITY_RESULT 					= "ok";
	public static boolean HAS_SELECTED_FILE 					= false;
	public static Context baseContext;
	public static boolean keepRunning 							= true;
	public static Handler mHandler;
	private static ExternalFilesAdapter myListAdapter;
	private static ArrayList<ExternalFilesModel> list_images 	=  null;
	public static List<String> array_selected 					= new ArrayList<String>();

	private Button buttonCreateFolder, buttonChckAll, buttonUnChckAll, buttonDone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//Get browsing mode
		Bundle b = getIntent().getExtras();
		if(null != b){
			MODE = b.getInt(MODE_KEY);
		}

		baseContext = getBaseContext();
		setContentView(R.layout.files_tab);
		setTitle("Select Images");
		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);
		mTitle = (TextView) findViewById(R.id.textViewCurrentPath);
		mTitle.setText("");
		external_files = (GridView) findViewById(R.id.external_files);
		external_files.setOnItemClickListener(listener);

		checkMediaAvailability();

		buttonCreateFolder = (Button) findViewById(R.id.buttonCreateFolder);
		buttonChckAll = (Button) findViewById(R.id.buttonChckAll);
		buttonUnChckAll = (Button) findViewById(R.id.buttonUnChckAll);
		buttonDone = (Button) findViewById(R.id.buttonDone);
		// on click Listener
		buttonCreateFolder.setOnClickListener(this);
		buttonChckAll.setOnClickListener(this);
		buttonUnChckAll.setOnClickListener(this);
		buttonDone.setOnClickListener(this);
		
		if(MODE == MODE_LOGO_UPLOADING){
			setTitle("Select Logo's");
			buttonCreateFolder.setVisibility(View.GONE);
			buttonDone.setText("Upload");
		}

		// sdcard file filter
		fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return ((f.isDirectory()
						|| f.getName().toLowerCase(Locale.getDefault()).endsWith(".jpg")
						|| f.getName().toLowerCase(Locale.getDefault()).endsWith(".jpeg")
						|| f.getName().toLowerCase(Locale.getDefault()).endsWith(".png")
						|| f.getName().toLowerCase(Locale.getDefault()).endsWith(".gif"))
						&& f.getName() != ".android_secure");
			}
		};

	}

	@Override
	public void onResume(){
		super.onResume();
		list_images.clear();
		array_selected.clear();
		currentPath = "";
	}

	// check for storage availability
	private void checkMediaAvailability() {
		
		/**
		 * To find locations on internal storage for your app, use
		 * getFilesDir(), called on any Context (such as your Activity, to get a
		 * File object.
		 * 
		 * To get a location on external storage unique for your app, use
		 * getExternalFilesDir(), called on any Context (such as your Activity,
		 * to get a File object.
		 * 
		 * To get a standard location on external storage for common types of
		 * files (e.g., movies), use getExternalStoragePublicDirectory() on
		 * Environment.
		 * 
		 * To get the root of external storage, use
		 * getExternalStorageDirectory() on Environment.
		 */

		String state = Environment.getExternalStorageState();
		// Check if the SD CARD is mounted
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			// Log.i(TAG, "Media is Mounted");
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			// Log.i(TAG, "Media is read only");
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			/**
			 * Something else is wrong. It may be one of many other states, but
			 * all we need to know is we can neither read nor write
			 */
			mExternalStorageAvailable = mExternalStorageWriteable = false;
			Toast.makeText(getApplicationContext(), "No Storage available!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// external
		if(mExternalStorageAvailable == true && mExternalStorageWriteable == true){
			// external
			if(FileUtil.isDir("/storage/"))
				EXTERNAL_PATH = "/storage/";
			else if(FileUtil.isDir("/sdcard/"))
				EXTERNAL_PATH = "/sdcard/";
			else if(FileUtil.isDir("/storage/sdcard0/"))
				EXTERNAL_PATH = "/storage/sdcard0/";
			else if(FileUtil.isDir("/storage/emulated/0/"))
				EXTERNAL_PATH = "/storage/emulated/0/";
		}

		// internal
		/**
		 * we detected that the default storage is same with internal path storage so we don't use
		 * Environment.getExternalStorageDirectory().getPath() but instead we use "/storage/".
		 */
//		 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
		INTERNAL_PATH =  Environment.getExternalStorageDirectory().getPath();
			
		displayInternalAndExternalPath();
	}
	
	private void displayInternalAndExternalPath(){
		Log.i(TAG, "Internal Path = " + INTERNAL_PATH);
		Log.i(TAG, "External Path = " + EXTERNAL_PATH);
		list_images = new ArrayList<ExternalFilesModel>();
		ArrayList<ExternalFilesModel> model = new ArrayList<ExternalFilesModel>();
		
		model.add(new ExternalFilesModel(R.drawable.ic_action_sd_storage, getResources().getString(R.string.InternalStorage), INTERNAL_PATH, false));
		
		if(EXTERNAL_PATH!=null){
			model.add(new ExternalFilesModel(R.drawable.ic_action_sd_storage, getResources().getString(R.string.ExternalStorage), EXTERNAL_PATH, false));
		}
		myListAdapter = new ExternalFilesAdapter(this, model, GlobalClass.imageLoader, MODE);
		external_files.setAdapter(myListAdapter);
		external_files.setStackFromBottom(false);
	}

	// load device files
	private void loadDevicePicOnly() {
		list_images = new ArrayList<ExternalFilesModel>();
		myListAdapter = new ExternalFilesAdapter(this, getAnyFiles(new File(currentPath).listFiles(fileFilter)), GlobalClass.imageLoader, MODE);
		external_files.setAdapter(myListAdapter);
		external_files.setStackFromBottom(false);
	}

	private void getExternalFiles(File[] files) {
		list_images = new ArrayList<ExternalFilesModel>();
		myListAdapter = new ExternalFilesAdapter(this, getAnyFiles(files), GlobalClass.imageLoader, MODE);
		external_files.setAdapter(myListAdapter);
		external_files.setStackFromBottom(false);
		external_files.refreshDrawableState();
	}

	private ArrayList<ExternalFilesModel> getAnyFiles(File[] files) {
		// calling the customized model for files
		ArrayList<ExternalFilesModel> models = new ArrayList<ExternalFilesModel>();
		// put two action for going to root dir in sdcard and going back
		models.add(new ExternalFilesModel(R.drawable.home, "Root", "", false));
		models.add(new ExternalFilesModel(R.drawable.ic_action_back, "Go Back", "", false));
		if (files != null) {
			for (File file : files) {
				// icons for storages
				if (file.getName().equals("sdcard0")) {
					models.add(new ExternalFilesModel(R.drawable.ic_action_sd_storage, "Internal Storage", file.getAbsolutePath(), false));
				} else if (file.getName().equals("sdcard1")) {
					models.add(new ExternalFilesModel(R.drawable.ic_action_sd_storage, "External Storage", file.getAbsolutePath(), false));
					// Directory to ignore
				} else if (file.getName().equals("usbdisk") || file.getName().equals("emulated") || file.getName().equals("removable")) {
				} else if (file.isDirectory()){
					models.add(new ExternalFilesModel(R.drawable.folder, file.getName(), file.getAbsolutePath(), false));
				} else {
					models.add(new ExternalFilesModel(R.drawable.document, file.getName(), file.getAbsolutePath(), true));
				}
			}
		} else {
			Log.d(TAG, "No files retrieved. Try retrieving external files.");
			models.add(new ExternalFilesModel(-1, "No image found", file.getAbsolutePath(), false));
		}

		list_images = new ArrayList<ExternalFilesModel>(models);
		return models;
	}

	private OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			TextView c = (TextView) v.findViewById(R.id.rowFiles);
			final CheckBox cb = (CheckBox) v.findViewById(R.id.checkBoxFileBrowser);
			String stringClickFile = c.getText().toString()
					.replace("Internal Storage", INTERNAL_PATH)
					.replace("External Storage", EXTERNAL_PATH);

			File clickedFile = null;
			try {
				clickedFile = new File(currentPath, stringClickFile).getCanonicalFile();
				file = new File("" + clickedFile);
				Log.i(TAG, "file " + file.getAbsolutePath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			currentPath = clickedFile.getAbsolutePath().replace("/Go Back","");
			String lastfolder = StringUtil
					.splitStr("" + currentPath, "/")
					.get(StringUtil.splitStr("" + currentPath, "/").size() - 1);

			int selectedRow = (int) id;
			// Go to root file directory
			if (selectedRow == 0 && !currentPath.equals(INTERNAL_PATH) && !currentPath.equals(EXTERNAL_PATH)) {
				currentPath = "";
				mTitle.setText("");
				displayInternalAndExternalPath();
			// Go back previous path
			} else if (selectedRow == 1 && stringClickFile.equals("Go Back")) {
				// check if has back directory
				int sizeOfPath = StringUtil.splitStr(currentPath, "/").size();
				if (sizeOfPath > 1) {
					/**
					 * replace last path with blank to exclude in path and to get the previous
					 */
					previousPathGoBack = currentPath.replace(lastfolder, "");
					// set the current path as previous path
					currentPath = previousPathGoBack;
					mTitle.setText(currentPath);
					loadDevicePicOnly();
				} else {
					// no back path. already in the root directory
					previousPathGoBack = currentPath;
					currentPath = "";
					mTitle.setText("");
					displayInternalAndExternalPath();
				}
			} else {
				if (file.isDirectory()) {
					if (lastfolder.equals("sdcard0")) 
						currentPath = "/storage/sdcard0/";
					else if (lastfolder.equals("sdcard1"))
						currentPath = "/storage/sdcard1/";
					else
						previousPathGoBack = currentPath;
					
					loadDevicePicOnly();
					mTitle.setText(currentPath);
				} else {
					// a file with no absolute path or the complete path file selected
					if (file.isFile()) {
						currentPath = currentPath.replace(lastfolder, "");
						mTitle.setText(currentPath);
						Log.d(TAG, "isFile - file has been clicked " + stringClickFile);
						setClickListenImageForChckbx(cb, position, currentPath + c.getText().toString());
					}
				}
			}
		}
	};

	private void setClickListenImageForChckbx(final CheckBox cb, int position, final String path){

		if(cb.isChecked()) {
			cb.setChecked(false);
			if(isSelectedPictureExist(array_selected, path)){
				array_selected.remove(path);
			}
		} else {
			cb.setChecked(true);
			if(!isSelectedPictureExist(array_selected, path)){
				array_selected.add(path);
			}
		}

		StringBuilder imageList = new StringBuilder();
		for(String s : array_selected){
			imageList.append(s);
			imageList.append(",");
		}
		Log.i("selected images", imageList.toString());
	}

	public static boolean isSelectedPictureExist(List<String> array_selected, String path){
		for(String s : array_selected){
			if(s.equals(path)){
				return true;
			}
		}
		return false;
	}

	public static void checkAllPictures(List<ExternalFilesModel> arr){
		for(ExternalFilesModel s : list_images){
			if(FileUtil.isFile(s.getPath()))
				array_selected.add(s.getPath());
		}
		myListAdapter.notifyDataSetChanged();
	}

	public static void uncheckAllPictures(List<ExternalFilesModel> arr){
		array_selected.clear();
		myListAdapter.notifyDataSetChanged();
	}

	private void alertWithFileSelected(final String pathContrainer) {
		new AlertDialog.Builder(ExternalFilesActivity.this)
		.setTitle("Selected Image")
		.setMessage("Image: " + pathContrainer)
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int button) {
				HAS_SELECTED_FILE = true;
				closeActivityAndCreateResult();
			}
		}).show();
	}

	private void closeActivityAndCreateResult() {
		// Create the result Intent and
		Intent intent = new Intent();
		intent.putExtra(SLIDE_ACTIVITY_RESULT, "1");
		// Set result and finish this Activity
		setResult(Activity.RESULT_OK, intent);
		finish(); // close activity
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeActivityAndCreateResult();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dialogCreateFolder(final String path) {
		final Dialog d = new Dialog(ExternalFilesActivity.this);
		d.setContentView(R.layout.create_folder);
		d.setTitle("Create Folder");
		d.setCancelable(true);
		d.getWindow().setLayout((int) (ActivityMainEditor.DEVICE_WIDTH * .7), ViewGroup.LayoutParams.WRAP_CONTENT);

		final EditText folderName = (EditText) d.findViewById(R.id.folderName);
		Button save = (Button) d.findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean success = FileUtil.createFolder(path, folderName.getText().toString().trim());
				if(success){
					Toast.makeText(getApplicationContext(), "Folder sucessfully created " + path +  File.separator + folderName.getText(), Toast.LENGTH_SHORT).show();
					d.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "Unable to create", Toast.LENGTH_SHORT).show();
				}
			}
		});

		d.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.buttonCreateFolder:
			dialogCreateFolder(mTitle.getText().toString());
			break;
		case R.id.buttonChckAll:
			checkAllPictures(list_images);
			break;
		case R.id.buttonUnChckAll:
			uncheckAllPictures(list_images);
			break;
		case R.id.buttonDone:
			String[] strarray = array_selected.toArray(new String[array_selected.size()]);
			// transfer data to resulting Activity
//			ActivityMainEditor.selectImages = Arrays.toString(strarray);
			Intent data = new Intent();
			data.putExtra("SelectedLogo", strarray);
			setResult(RESULT_OK, data);
			finish();
			break;
		}
	}
	
	public static void startActivityImageBrowsingForResult(Activity act, int requestCode, int mode){
		Intent intent = new Intent(act, ExternalFilesActivity.class);
		intent.putExtra(MODE_KEY, mode);
		act.startActivityForResult(intent, requestCode);
	}

}
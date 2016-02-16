package com.lipl.youthconnect.youth_connect.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DynamicForm;
import com.lipl.youthconnect.youth_connect.util.MultipartUtility;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.FieldDetails;
import com.lipl.youthconnect.youth_connect.pojo.Options;
import com.lipl.youthconnect.youth_connect.pojo.ReportForm;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

public class CameraCaptureActivity extends ActionBarActivity implements AnimationListener {

	int TAKE_PHOTO_CODE =20202;
	int IMAGE_CAPTURE = 12122;
	public static File PhotoToBeUpload = null;
	public static File CapturedImgFile = null;
	public static ArrayList<String> GalleryList = new ArrayList<String>();

	public static Uri CurrentUri = null;
	public static String sFilePath = ""; 
	LinearLayout layoutRemoveChange;

	
	RelativeLayout mainL;
	ImageView imgPhoto;
	TextView btnSubmit;
	
	Animation animation;
	RelativeLayout layout;
	boolean moveUp = false;
	boolean moveDown = false;
	LinearLayout layoutTransparent;

	TextView btnRemovePic;
	TextView btnCahngePic;

	private static String userResponseId;
	private static String title;
	private static String no_of_times_report_submited;

	boolean isUserLeavingPage = false;
	private boolean isAnyNetworkOperationIsProcessing = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_image_layout);
		
		YouthConnectSingleTone.getInstance().context = this;

		mainL = (RelativeLayout) findViewById(R.id.mainL);
		//tvaddress = (TextView) findViewById(R.id.address);
	    
	    userResponseId = getIntent().getExtras().getString("userResponseId");
		
		title = getIntent().getExtras().getString("report_title");
		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(Html.fromHtml("<b>"+title+"</b><br> Take Picture(Optional)"));
		
		no_of_times_report_submited = getIntent().getExtras().getString("no_of_times_report_submited");
		TextView tvNo_of_times_report_submited = (TextView) findViewById(R.id.no_of_times_report_submited);
		tvNo_of_times_report_submited.setText(no_of_times_report_submited);

		layoutRemoveChange = (LinearLayout) findViewById(R.id.layoutRemoveChange);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
		imgPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCamera();			
			}
		});
		
		btnRemovePic = (TextView) findViewById(R.id.btnRemovePic);
		btnRemovePic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imgPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera));
				layoutRemoveChange.setVisibility(View.GONE);
				CapturedImgFile = null;
			}
		});
		btnCahngePic = (TextView) findViewById(R.id.btnCahngePic);
		btnCahngePic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCamera();
			}
		});
		
		btnSubmit = (TextView) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
	}
	
	@Override
	protected void onDestroy() {
		if(isUserLeavingPage) {
			CapturedImgFile = null;
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if((CapturedImgFile != null) && 
				(CapturedImgFile.getPath() != null) && 
				(CapturedImgFile.getPath().length() > 0) ){
			ImageView imageView1 = (ImageView) findViewById(R.id.imgPhoto);
			BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;
            final Bitmap bitmap = BitmapFactory.decodeFile(CapturedImgFile.getPath(),
                    options);
			imageView1.setImageBitmap(bitmap);
			imageView1.setBackgroundResource(R.drawable.transparent_circle);
			if(layoutRemoveChange == null) layoutRemoveChange = (LinearLayout) findViewById(R.id.layoutRemoveChange);
			layoutRemoveChange.setVisibility(View.VISIBLE);

			FileOutputStream out = null;
			String filename = CapturedImgFile.getName();
			try {
				out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startCamera() {
		
	   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	   // Specify the output. This will be unique.
	   setsFilePath(getTempFileString());
	   //
	   intent.putExtra(MediaStore.EXTRA_OUTPUT, CurrentUri);
	   //
	   // Keep a list for afterwards
	   FillPhotoList();
	   //
	   // finally start the intent and wait for a result.
	   startActivityForResult(intent, IMAGE_CAPTURE);
	}

	private void FillPhotoList()
	{
	   // initialize the list!
	   GalleryList.clear();
	   String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
	   // intialize the Uri and the Cursor, and the current expected size.
	   Cursor c = null;
	   Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	   //
	   // Query the Uri to get the data path.  Only if the Uri is valid.
	   if (u != null)
	   {
		  c = managedQuery(u, projection, null, null, null);
	   }

	   // If we found the cursor and found a record in it (we also have the id).
	   if ((c != null) && (c.moveToFirst()))
	   {
		  do
		  {
			// Loop each and add to the list.
			GalleryList.add(c.getString(0));
		  }
		  while (c.moveToNext());
	   }
	}

	private String getTempFileString()
	{
	   // Only one time will we grab this location.
	   final File path = new File(Environment.getExternalStorageDirectory(),
			 getString(getApplicationInfo().labelRes));
	   //
	   // If this does not exist, we can create it here.
	   if (!path.exists())
	   {
		  path.mkdir();
	   }
	   //
	   return new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg").getPath();
	}

	public void setsFilePath(String value)
	{
	   // We just updated this value. Set the property first.
	   sFilePath = value;
	   //
	   // initialize these two
	   PhotoToBeUpload = null;
	   CurrentUri = null;
	   //
	   // If we have something real, setup the file and the Uri.
	   if (!sFilePath.equalsIgnoreCase(""))
	   {
		  PhotoToBeUpload = new File(sFilePath);
		  CurrentUri = Uri.fromFile(PhotoToBeUpload);
	   }
	}

	public void openCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
		startActivityForResult(intent, TAKE_PHOTO_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == IMAGE_CAPTURE)
		   {
			  // based on the result we either set the preview or show a quick toast splash.
			  if (resultCode == android.app.Activity.RESULT_OK)
			  {
				 // This is ##### ridiculous.  Some versions of Android save
				 // to the MediaStore as well.  Not sure why!  We don't know what
				 // name Android will give either, so we get to search for this
				 // manually and remove it.
				 String[] projection = { MediaStore.Images.ImageColumns.SIZE,
										 MediaStore.Images.ImageColumns.DISPLAY_NAME,
										 MediaStore.Images.ImageColumns.DATA,
										 BaseColumns._ID};
				 //
				 // intialize the Uri and the Cursor, and the current expected size.
				 Cursor c = null;
				 Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				 //
				 if (PhotoToBeUpload != null)
				 {
					// Query the Uri to get the data path.  Only if the Uri is valid,
					// and we had a valid size to be searching for.
					if ((u != null) && (PhotoToBeUpload.length() > 0))
					{
					   c = managedQuery(u, projection, null, null, null);
					}
					//
					// If we found the cursor and found a record in it (we also have the size).
					if ((c != null) && (c.moveToFirst()))
					{
					   do
					   {
						  // Check each area in the gallary we built before.
						  boolean bFound = false;

						  for (String sGallery : GalleryList)
						  {
							 if (sGallery.equalsIgnoreCase(c.getString(1)))
							 {
								bFound = true;
								break;
							 }
						  }
						  //
						  // To here we looped the full gallery.
						  if (!bFound)
						  {
							 // This is the NEW image.  If the size is bigger, copy it.
							 // Then delete it!
							 File f = new File(c.getString(2));

							 // Ensure it's there, check size, and delete!
							 if ((f.exists()) && (PhotoToBeUpload.length() < c.getLong(0)) && (PhotoToBeUpload.delete()))
							 {
								// Finally we can stop the copy.
								try
								{
								   PhotoToBeUpload.createNewFile();
								   FileChannel source = null;
								   FileChannel destination = null;
								   try
								   {
									  source = new FileInputStream(f).getChannel();
									  destination = new FileOutputStream(PhotoToBeUpload).getChannel();
									  destination.transferFrom(source, 0, source.size());
								   }
								   finally
								   {
									  if (source != null)
									  {
										 source.close();
									  }
									  if (destination != null)
									  {
										 destination.close();
									  }
								   }
								}
								catch (IOException e)
								{
								   // Could not copy the file over.
								   Toast.makeText(this, "ErrorOccured", Toast.LENGTH_SHORT).show();
								}
							 }
							 //
							 ContentResolver cr = getContentResolver();
							 cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								BaseColumns._ID + "=" + c.getString(3), null);
							 break;
						  }
					   } while (c.moveToNext());
					}
				 }
			  }

			  if(PhotoToBeUpload.exists()){
				  CapturedImgFile = PhotoToBeUpload;
			  }
		   }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		if(isAnyNetworkOperationIsProcessing == false) {
			YouthConnectSingleTone.getInstance().isBackFromSubmit = true;
			isUserLeavingPage = true;
			super.onBackPressed();
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
		if(moveUp)
			layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(moveDown){
			moveDown = false;
			moveUp = false;
			layout.setVisibility(View.GONE);
			layout.setDrawingCacheEnabled(false);
		} 
		
		animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
	private class ReportSubmissionAsyncTask extends AsyncTask<Void, Void, Boolean>{

		String TAG = "ReportEntrySubmissionAsyncTask";
		String userResponseId = null;
		String submitMessage = null;
		ReportForm mReportForm = null;
		private boolean isChangePassword = false;

		private ProgressDialog mProgressDialog = null;

		public ReportSubmissionAsyncTask(ReportForm mReportForm ){
			this.mReportForm = mReportForm;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			btnSubmit.setEnabled(false);
			btnCahngePic.setEnabled(false);
			btnRemovePic.setEnabled(false);
			imgPhoto.setEnabled(false);
			isAnyNetworkOperationIsProcessing = true;
			mProgressDialog = ProgressDialog.show(CameraCaptureActivity.this, "Loading", "Please wait...");
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			String apikey = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
			if(apikey == null)
				return null;

			String charset = "UTF-8";
			String requestURL = Constants.BASE_URL+Constants.REPORT_SUBMIT_REQUEST_URL;

			try {
				MultipartUtility multipart = new MultipartUtility(requestURL, charset, apikey);

				for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
					multipart.addFormField("orderElement[] : ", mReportForm.getFormFieldList().get(i).getFieldId()+"");
					Log.i("Result", "orderElement[] : "+ mReportForm.getFormFieldList().get(i).getFieldId()+"");
				}

				multipart.addFormField("FbField[fb_form_id]", mReportForm.getFormDetailsList().get(0).getFormID());
				Log.i("Result", "FbField[fb_form_id]" + mReportForm.getFormDetailsList().get(0).getFormID());

				for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
					FieldDetails fieldDetails = mReportForm.getFormFieldList().get(i).getFieldDetails();
					if(fieldDetails.getFieldType() == DynamicForm.FieldType.FILE){
						File fileTobeUpload = new File(fieldDetails.getFieldValue());
						if(fileTobeUpload.exists()) {

							String something = fileTobeUpload.getName();
							String extension = something.substring(something.lastIndexOf(".")+1);

							if(extension.equalsIgnoreCase("jpg")
									|| extension.equalsIgnoreCase("jpeg")
									|| extension.equalsIgnoreCase("bmp")
									|| extension.equalsIgnoreCase("png")){

								/*Log.i("Submit Form", CapturedImgFile.toString());
								Log.i("Submit Form : Name", CapturedImgFile.getName());
								multipart.addFilePart("field_snap", CapturedImgFile);*/

								//Bitmap bmp = BitmapFactory.decodeFile(CapturedImgFile.getPathForImage());
								Bitmap bmp = Util.decodeFile(fileTobeUpload, 400);
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
								InputStream in = new ByteArrayInputStream(bos.toByteArray());
								//ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");

								OutputStream outputStream = null;

								try {
									// write the inputStream to a FileOutputStream
									outputStream =
											new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
													getString(getApplicationInfo().labelRes)+"file-new.jpeg"));

									int read = 0;
									byte[] bytes = new byte[1024];

									while ((read = in.read(bytes)) != -1) {
										outputStream.write(bytes, 0, read);
									}

									System.out.println("Done!");

								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									if (in != null) {
										try {
											in.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									if (outputStream != null) {
										try {
											// outputStream.flush();
											outputStream.close();
										} catch (IOException e) {
											e.printStackTrace();
										}

									}
								}

								File file = new File(Environment.getExternalStorageDirectory(),
										getString(getApplicationInfo().labelRes)+"file-new.jpeg");
								if(file != null && file.exists()) {

									multipart.addFilePart("FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]", file);
									Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
									multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][name]", file.getName());
									Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
								}

							} else {

								multipart.addFilePart("FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]", fileTobeUpload);
								Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
								multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][name]", fileTobeUpload.getName());
								Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
							}
						}
					} else if(fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT
							|| fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX
							|| fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO) {
						for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
							Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
							if(option.isSelected()) {
								multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][]", option.getOptionId()+"");
								Log.i("Result", "FbField[field_" + "FbField[field_" + fieldDetails.getFieldID() + "][]"+ option.getOptionId()+"");
							}
						}
					} else{
						multipart.addFormField("FbField[field_"+fieldDetails.getFieldID()+"]", fieldDetails.getFieldValue());
						Log.i("Result", "FbField[field_"+fieldDetails.getFieldID()+"]"+ fieldDetails.getFieldValue());
					}
				}

				multipart.addFormField("lattitude", "");
				multipart.addFormField("longitude", "");

				if(CapturedImgFile != null && CapturedImgFile.exists()) {

					/*Log.i("Submit Form", CapturedImgFile.toString());
					Log.i("Submit Form : Name", CapturedImgFile.getName());
					multipart.addFilePart("field_snap", CapturedImgFile);*/

					//Bitmap bmp = BitmapFactory.decodeFile(CapturedImgFile.getPathForImage());
					Bitmap bmp = Util.decodeFile(CapturedImgFile, 400);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
					InputStream in = new ByteArrayInputStream(bos.toByteArray());
					//ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");

					OutputStream outputStream = null;

					try {
						// write the inputStream to a FileOutputStream
						outputStream =
								new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
										getString(getApplicationInfo().labelRes)+"holder-new.jpeg"));

						int read = 0;
						byte[] bytes = new byte[1024];

						while ((read = in.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);
						}

						System.out.println("Done!");

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (outputStream != null) {
							try {
								// outputStream.flush();
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}

					File file = new File(Environment.getExternalStorageDirectory(),
							getString(getApplicationInfo().labelRes)+"holder-new.jpeg");
					if(file != null && file.exists()) {

						Log.i("Submit Form", file.toString());
						Log.i("Submit Form : Name", file.getName());
						multipart.addFilePart("field_snap", file);
					}
				}

				List<String> response = multipart.finish();

				System.out.println("SERVER REPLIED:");
				String res = "";
				for (String line : response) {
					res = res + line + "\n";
				}
				Log.i(TAG, res);

	            /* {"Response":
	             * 		[{"submitStatus":"Success",
	             * "		submitMessage":"Report submitted successfully.",
	             * "	userResponseId":"38"}]
	             * }
	             */
				try {
				if(res != null && res.length() > 0 && res.charAt(0) == '{'){
					JSONObject jsonObject = new JSONObject(res);
					if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
						String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
						if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
							isChangePassword = true;
							return null;
						}
					}
				}

				JSONArray jsonArray;
				JSONObject jObject;

					jObject = new JSONObject(res);

					jsonArray = jObject.getJSONArray("Response");
					for (int index = 0; index < jsonArray.length(); index++) {
						JSONObject jsonObject = jsonArray.getJSONObject(index);

						if(jsonObject != null && jsonObject.isNull("Apikey") == false){
							String msg = jsonObject.getString("Apikey");
							submitMessage = msg+"\nPlease logout and login again to get your app work.";
							return false;
						}

						String submitStatus = jsonObject.optString("submitStatus", "");
						submitMessage = jsonObject.optString("submitMessage", "");
						userResponseId = jsonObject.optString("userResponseId", "");

						if(submitStatus.equalsIgnoreCase("failed")){
							// {"field_344":"This field is required"}

							String res1 = submitMessage;
							String str = "";
							String[] fields = res1.split(":");
							if(fields != null && fields.length > 0){
								str = fields[0];
								str = str.replace("{","");
								str = str.replace("\"","");
							}

							String fieldName = "";
							for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
								String formID = "field_"+mReportForm.getFormFieldList().get(i).getFieldId();
								if(formID != null && formID.equalsIgnoreCase(str)){
									fieldName = mReportForm.getFormFieldList().get(i).getFieldDetails().getLabelName();
								}
							}

							if(fieldName != null && fieldName.length() > 0) {
								submitMessage = fieldName + " field is required.";
							}

							return false;
						}

						if(submitStatus.equalsIgnoreCase("Success")){
							return true;
						}

						return false;
					}
				} catch (JSONException e) {
					submitMessage = "Getting error response from server.";
					Log.e(TAG, "doInBackground()", e);
				}

			} catch(SocketTimeoutException exception){
				submitMessage = "Connection Problem.";
				Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
			} catch(ConnectException exception){
				submitMessage = "Connection Problem.";
				Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
			} catch(ConnectTimeoutException e){
				submitMessage = "Connection time out.\nPlease reset internet connection.";
			}catch (IOException ex) {
				submitMessage = "Network Error occured.";
				System.err.println(ex);
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			super.onPostExecute(result);
			btnSubmit.setEnabled(true);
			btnCahngePic.setEnabled(true);
			btnRemovePic.setEnabled(true);
			imgPhoto.setEnabled(true);
			isAnyNetworkOperationIsProcessing = false;

			if(mProgressDialog != null){
				mProgressDialog.dismiss();
			}

			if(isChangePassword){
				AlertDialog.Builder builder = new AlertDialog.Builder(CameraCaptureActivity.this, R.style.AppCompatAlertDialogStyle);
				builder.setTitle(getResources().getString(R.string.password_changed_title));
				builder.setMessage(getResources().getString(R.string.password_changed_description));
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(CameraCaptureActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("Exit me", true);
						startActivity(intent);
						finish();
					}
				});
				builder.show();

				return;
			}

			if(result == true){
				if(CapturedImgFile != null && CapturedImgFile.exists()) {
					/*Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Submitted successfully with picture.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
					View snackbarView = snackbar.getView();
					TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
					tv.setTextColor(Color.WHITE);
					TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
					tvAction.setTextColor(Color.CYAN);
					snackbar.show();*/
					showAlertDialog("Submitted successfully with picture.", "Feedback Submit", "OK");
					} else{
					/*Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Submitted successfully.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
					View snackbarView = snackbar.getView();
					TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
					tv.setTextColor(Color.WHITE);
					TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
					tvAction.setTextColor(Color.CYAN);
					snackbar.show();*/
					showAlertDialog("Submitted successfully.", "Feedback Submit", "OK");
				}
			} else{
				Log.e("Submit Time","ErrorMsg : "+ submitMessage);
				Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), submitMessage, Snackbar.LENGTH_LONG).setAction("OK", new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
				View snackbarView = snackbar.getView();
				TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
				tv.setTextColor(Color.WHITE);
				TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
				tvAction.setTextColor(Color.CYAN);
				snackbar.show();
			}
		}
	}

	/**
	 * To Show Material Alert Dialog
	 *
	 * @param message
	 * @param title
	 * */
	private void showAlertDialog(String message, String title, String positiveButtonText){

		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isUserLeavingPage = true;
				YouthConnectSingleTone.getInstance().isCameraCaptureActivityFinish = true;
				finish();
			}
		});
		builder.show();
	}
}
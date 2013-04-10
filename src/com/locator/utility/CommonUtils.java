package com.locator.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class CommonUtils {
	
	public static String TAG = "CommonUtils";
	
	public static void showToastMessage(Context context, String message) {
		Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    	t.setGravity(Gravity.CENTER, 0, 0);
    	t.show();
	}
	
	public static void showToastMessageLong(Context context, String message) {
		Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
    	t.setGravity(Gravity.BOTTOM, 0, 0);
    	t.show();
	}
	
public static String saveImageToSdcard(Bitmap image) {
		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
    	String filePath = null;
		
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        mExternalStorageAvailable = mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        mExternalStorageAvailable = true;
	        mExternalStorageWriteable = false;
	    } else {
	        mExternalStorageAvailable = mExternalStorageWriteable = false;
	        Log.d(TAG, "External storage is not available");
	    }

	    if(mExternalStorageAvailable && mExternalStorageWriteable) {
			String sdcardPath = Environment.getExternalStorageDirectory().toString();
			File fileDir = new File(sdcardPath + "/eQsMobilePrototype/");
			
			if(!fileDir.exists()) {
				fileDir.mkdir();
			}
			
			Random ran = new Random();
			int newOffSet = ran.nextInt();
			File file = new File(fileDir, "DefectPicture"+ newOffSet +".png");
			try {
				OutputStream fOut = new FileOutputStream(file);
				image.compress(Bitmap.CompressFormat.PNG, 85, fOut);
				fOut.flush();
				fOut.close();
				filePath = file.getPath();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
		return filePath;

	    }

	public static boolean deleteFileFromSdcard(String fileName) {
		boolean res = false;
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        mExternalStorageAvailable = mExternalStorageWriteable = true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        mExternalStorageAvailable = true;
	        mExternalStorageWriteable = false;
	    } else {
	        mExternalStorageAvailable = mExternalStorageWriteable = false;
	        Log.d(TAG, "External storage is not available");
	    }
	    if(mExternalStorageAvailable && mExternalStorageWriteable) {
	    	if( !TextUtils.isEmpty(fileName) ) {
		        File file = new File(fileName);
		        if(null != file && file.exists()) {
					res = file.delete();
		        }
			            
			}
	    }
		
		return res;
		
	}
	/*public static boolean isConnectedToInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			
			HttpGet httpGet = new HttpGet(Constants.BASE_URL);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 5000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			try {
				httpClient.execute(httpGet);
				return true;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    return false;		
		
	}*/
	
	/*public static int postData(String data, String url) {
		
		Log.d(TAG, " postData : " + data + " url :" + url);
		int res =0 ;
		HttpClient httpclient =ServerProxy.getHttpClient();
		HttpPost httppost = new HttpPost(url);
		StringEntity entity = null;
		URI uri;
		try {
			entity = new StringEntity(data);
			httppost.setEntity(entity);
			uri = new URI(url);
			httppost.setURI(uri);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Accept", "application/json");
			HttpResponse keyResponse = null;  
			keyResponse = httpclient.execute(httppost);
			StatusLine status = keyResponse.getStatusLine();
			Log.d(TAG, " Status from Server : " + status.getReasonPhrase());
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			res = Constants.ASSETS_OPEN_ERROR;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			res = Constants.ASSETS_OPEN_ERROR;
		}catch (ClientProtocolException e) {
			e.printStackTrace();
			res = Constants.ASSETS_OPEN_ERROR;
		} catch (IOException e) {
			e.printStackTrace();
			res = Constants.ASSETS_OPEN_ERROR;
		}
		
		return res;
	}*/
	
	
	
	/*public static String createJsonData(Defect defect, Car car) {
		
        StringBuffer jsonStr = new StringBuffer();
        // group
        jsonStr = jsonStr.append("{\"comment\":\"Test Comment\",\"defectGroup\":\"");
        jsonStr = jsonStr.append(defect.getGroup().getName());
        // object
        jsonStr = jsonStr.append("\",\"defectObject\":\"");
        jsonStr = jsonStr.append(defect.getObject().getName());
        // position
        jsonStr = jsonStr.append("\",\"defectPosition\":\"");
        jsonStr = jsonStr.append(defect.getPositions().get(0).getName());
        // type
        jsonStr = jsonStr.append("\",\"defectType\":\"");
        jsonStr = jsonStr.append(defect.getType().getName());
        // maingroup
        jsonStr = jsonStr.append("\",\"maingroup\" : \"");
        jsonStr = jsonStr.append(defect.getMainGroup().getName());
        
        //jsonStr = jsonStr.append("\" ,\"vehicleId\" : { \"pin\" : \"03244\", \"spj\" : \"2011\" }");
        
        jsonStr = jsonStr.append("\" ,\"vehicleId\" : { \"pin\" : \"");
        jsonStr = jsonStr.append(car.getId() + "\",");
        
        jsonStr = jsonStr.append(" \"spj\" : \"");
        jsonStr = jsonStr.append(car.getSpj() + "\" }");
        
        
        // defect image
        String filePath = defect.getImageBitmapFile();
        long fileSize =0;
        //byte[] imageByteArray = null; 
        if( !TextUtils.isEmpty(filePath) ) {
        	
            File file = new File(filePath);
            if(null != file && file.exists()) {
            	
            	FileInputStream id;
                try {
                	id = new FileInputStream(file);
                	 fileSize=file.length();
                     byte[] bytes1 = new byte[(int)fileSize];
                     int offset = 0;
                     int numRead = 0;
					while (offset < bytes1.length
					          && (numRead=id.read(bytes1, offset, bytes1.length-offset)) >= 0) {
					       offset += numRead;
					}
					
					jsonStr = jsonStr.append(",\"defectImage\":{  \"imageData\":\"");
	                jsonStr = jsonStr.append(Base64.encodeToString(bytes1, Base64.NO_WRAP));
	                    
	                // image name
                    jsonStr = jsonStr.append("\",\"imageName\":\"");
                    jsonStr = jsonStr.append(file.getName());

                    // image size
                    jsonStr = jsonStr.append("\",\"imageSize\":");
                    jsonStr = jsonStr.append(Integer.toString((int)fileSize));

                    // image type
                    jsonStr = jsonStr.append(",\"imageType\":\"png\" }");
	                    
				}  catch (FileNotFoundException e2) {
					e2.printStackTrace();
				}catch (IOException e1) {
					e1.printStackTrace();
				} catch (BufferUnderflowException e) {
	                   e.printStackTrace();
	            }
                
               
            }
        	
        }
        
        jsonStr = jsonStr.append("}");
        return jsonStr.toString();
 }*/
	
	
}

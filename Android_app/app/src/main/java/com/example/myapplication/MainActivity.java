package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int  MY_PERMISSIONS_REQUEST = 100;

    public String responseFromServer;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     *
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        }
        TextView textView = (findViewById(R.id.textView));
        textView.setText("Choose photo from gallery");
        Button chooseButton = (findViewById(R.id.ButtonUploadPhoto));
        chooseButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                Uri selectedImage = data.getData();
                String filePath = getPath(selectedImage);
                String fileExtn = filePath.substring(filePath.lastIndexOf(".") + 1);

                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    ImageView myImage = (ImageView) findViewById(R.id.imageView);
                    myImage.setImageBitmap(image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                //sendData();
                //image_name_tv.setText(filePath);

// To dismiss the dialog

                //ProgressDialog dialog = ProgressDialog.show(this, "Down Loading", "Please wait ...", true);
                if (fileExtn.equals("img") || fileExtn.equals("jpg") || fileExtn.equals("jpeg")
                        || fileExtn.equals("gif") || fileExtn.equals("png")) {
                    Toast.makeText(getApplicationContext(), "You have chosen a photo" + fileExtn, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong format of a photo", Toast.LENGTH_LONG).show();
                }
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(this));
                }
                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("myscript");
                PyObject obj = pyobj.callAttr("main", filePath);
                Log.i("Info2", String.valueOf(obj));
                TextView textView = (findViewById(R.id.textView));
                textView.setText(obj.toString() + " people found");
                //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();


                //Log.i("Info",filePath);
//                try {
//                    sendNetworkRequest(selectedImage,filePath,fileExtn);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    RequestPlis(selectedImage,filePath,fileExtn);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                //sendData(filePath,fileExtn);
                //uploadFile(selectedImage);
                //Toast.makeText(getApplicationContext(), HowManyPeople, Toast.LENGTH_LONG).show();
            }
        }
    }


    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
//    public void RequestPlis(Uri fileUri, String fileP, String fileExtn) throws IOException {
//        File file = new File(fileP);
//        URL url = new URL("https://crowdeval-api-2iagetvpbq-uc.a.run.app/img-to-number");
//        HttpsURLConnection conn =
//                (HttpsURLConnection) url.openConnection();
//
//// some arbitrary text for multitext boundary
//// only 7-bit US-ASCII digits max length 70
//        String boundary_string = "kjq391jdox2jc18rjf9";
//// we want to write out
//        //Log.i("Info1","Content-Type", "multipart/form-data \"");
//        conn.setDoOutput(true);
//        //conn.setDoInput(true);
//        conn.setRequestMethod("POST");
//        conn.addRequestProperty("accept", "application/json; boundary="+boundary_string);
//        conn.setRequestProperty("Content-Type", "multipart/form-data \"");
//        //conn.addRequestProperty("accept", "application/json; boundary="+boundary_string);
//        //conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary_string);
//
//        //Log.i("Info0",String.valueOf(conn.getResponseCode()));
//// now we write out the multipart to the body
//        OutputStream conn_out = conn.getOutputStream();
//        Log.i("Info1", String.valueOf(conn));
//        BufferedWriter conn_out_writer = new BufferedWriter(new OutputStreamWriter(conn_out));
//        Log.i("Info2", String.valueOf(conn));
//// write out multitext body based on w3 standard
//// https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
//        conn_out_writer.write("\r\n--" + boundary_string + "\r\n");
//        conn_out_writer.write(
//                "file="+file.getName()+";" +
//                "type="+  "image/"+ fileExtn+
//                "\r\n\r\n");
//        conn_out_writer.flush();
//        Log.i("Info3",conn_out_writer.toString());
//
//// payload from the file
//        FileInputStream file_stream = new FileInputStream(file);
//// write direct to outputstream instance, because we write now bytes and not strings
//        int read_bytes;
//        byte[] buffer = new byte[1024];
//        while((read_bytes = file_stream.read(buffer)) != -1) {
//            conn_out.write(buffer, 0, read_bytes);
//        }
//        conn_out.flush();
//// close multipart body
//        conn_out_writer.write("\r\n--" + boundary_string + "--\r\n");
//        conn_out_writer.flush();
//
//// close all the streams
//        conn_out_writer.close();
//        conn_out.close();
//        file_stream.close();
//// execute and get response code
//        int response = conn.getResponseCode();
//        Log.i("Info4", String.valueOf(response));
//        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
//    }
//    public static MultipartBody.Part getMultiPartImage(String fileParameter, File file) {
//        if (file != null)
//            return MultipartBody.Part.createFormData(fileParameter, file.getName(),
//                    RequestBody.create(MediaType.parse("image/*"), file));
//        return null;
//    }
//    public void sendNetworkRequest(Uri fileUri, String fileP, String fileExtn) throws FileNotFoundException {
//        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .connectTimeout(60, TimeUnit.SECONDS);
//        File file = new File(fileP);
//        String filePath = file.getAbsolutePath();
//        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//
//        // you can change the format of you image compressed for what do you want;
//        // now it is set up to 640 x 480;
//        Bitmap bmpCompressed = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        // CompressFormat set up to JPG, you can change to PNG or whatever you want;
//        bmpCompressed.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        //String data = Arrays.toString(bos.toByteArray());
//        String data = bmpCompressed.toString();
//        Log.i("Info6",data);
//
//        // create RequestBody instance from file
////        RequestBody requestFile =
////                RequestBody.create(
////                        MediaType.parse(multipart/form-data),
////                        file
////                );
//
//        MultipartBody.Part fileRequest = getMultiPartImage("files",file);
//        Log.i("Info7",fileRequest.toString());
//        //MultipartBody.Part body =
//        //        MultipartBody.Part.createFormData("file",file.getName(), requestFile);
//        //Log.i("Info8",body.toString());
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        Log.i("Info9",logging.toString());
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        Log.i("Info10",logging.toString());
//
//        okhttpClientBuilder.addInterceptor(logging);
//        Log.i("Info11",okhttpClientBuilder.toString());
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("https://crowdeval-api-2iagetvpbq-uc.a.run.app/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okhttpClientBuilder.build());
//        Log.i("Info12",builder.toString());
//        Retrofit retrofit = builder.build();
//        Log.i("Info13",retrofit.toString());
//        FastAPI client = retrofit.create(FastAPI.class);
//        Log.i("Info14",client.toString());
//
//        Call<ResponseBody> call = client.upload(fileRequest);
//        Log.i("Info15",call.toString());
//
//
//        call.enqueue(new Callback<ResponseBody>() {
//
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                responseFromServer = response.toString();
//                Log.i("Info",responseFromServer);
//                Toast.makeText(getApplicationContext(), responseFromServer, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                Log.e("Upload error:", t.getMessage());
//                Toast.makeText(getApplicationContext(), "rip", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//
//
//    //public String getPath(Uri uri) {
//    //    String[] projection = {MediaStore.MediaColumns.DATA};
//    //    Cursor cursor = managedQuery(uri, projection, null, null, null);
//    //    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//    //    cursor.moveToFirst();
//
//    //    return cursor.getString(column_index);
//    //}
//    public String getNumberFromApi (String filePath)
//    {
//        return "10";
//    }
//    public String ConvertImageToBinary(String filePath) { // useless chyba
//        byte[] byteArrayImage = new byte[0];
//        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
//        Bitmap bm = BitmapFactory.decodeFile(filePath);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//        return String.valueOf(baos.toByteArray());
//    }
    /*public void sendData(String filePath, String fileExtn) // nie działa najprawdopodobniej w JSON złe informacje
    {
        HashMap<String, String> data = new HashMap<String, String>();

        //String url = "https://crowdeval-api-2iagetvpbq-uc.a.run.app/docs#/default/detect_people_return_number_img_to_number_post";
        String url = "https://crowdeval-api-2iagetvpbq-uc.a.run.app/file-to-number";
        RequestQueue queue = Volley.newRequestQueue(this);
        //String img = ConvertImageToBinary();
        //File file = new File(filePath);
        //byte[] bytes = file.toByteArray("file.png");
        //return Response.ok(bytes).build();
        File f = new File(filePath);
        //String imageName = f.getName();
        data.put("file:",String.valueOf(f));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String ans = null;
                        try {
                            ans = response.getString("ans");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), ans, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    AccessibilityService mContext = null;
                    ConnectivityManager cm = (ConnectivityManager) mContext
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = null;
                    if (cm != null) {
                        activeNetwork = cm.getActiveNetworkInfo();
                    }
                    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                        Toast.makeText(getApplicationContext(), "Server is not connected to internet.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your device is not connected to internet.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException
                        || (error.getCause().getMessage() != null
                        && error.getCause().getMessage().contains("connection"))) {
                    Toast.makeText(getApplicationContext(), "Your device is not connected to internet.",
                            Toast.LENGTH_SHORT).show();
                } else if (error.getCause() instanceof MalformedURLException) {
                    Toast.makeText(getApplicationContext(), "Bad Request.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                        || error.getCause() instanceof JSONException
                        || error.getCause() instanceof XmlPullParserException) {
                    Toast.makeText(getApplicationContext(), "Parse Error (because of invalid json or xml).",
                            Toast.LENGTH_SHORT).show();
                } else if (error.getCause() instanceof OutOfMemoryError) {
                    Toast.makeText(getApplicationContext(), "Out Of Memory Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "server couldn't find the authenticated request.",
                            Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "Server is not responding.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                        || error.getCause() instanceof ConnectTimeoutException
                        || error.getCause() instanceof SocketException
                        || (error.getCause().getMessage() != null
                        && error.getCause().getMessage().contains("Connection timed out"))) {
                    Toast.makeText(getApplicationContext(), "Connection timeout error",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "An unknown error occurred.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(jsonObjectRequest);
    }*/
}
package test.bwei.com.zhangjian20171008xiangji;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bwei.imageloaderlibrary.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	private static final int CROP_SMALL_PICTURE = 2;
	protected static Uri tempUri;
	private CircleImageView iv_personal_icon;
    private SharedPreferences sp;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iv_personal_icon = (CircleImageView) findViewById(R.id.iv_personal_icon);
        sp = MyUtil.getSharedPreferencesInstance(this);

        getUserInfo();
    }

	/**
	 * 显示修改头像的对话框
	 */
	public void showChoosePicDialog(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("设置头像");
		String[] items = { "选择本地照片", "拍照" };
		builder.setNegativeButton("取消", null);
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case CHOOSE_PICTURE: // 选择本地照片
						Intent openAlbumIntent = new Intent(
								Intent.ACTION_PICK,
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						openAlbumIntent.setType("image/*");
						startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
						break;
					case TAKE_PICTURE: // 拍照
						takePicture();
						break;
				}
			}
		});
		builder.create().show();
	}

    private void getUserInfo() {

        OkHttpClient okHttpClient=new OkHttpClient();

        Request request=new Request.Builder().url(Api.USER_MINUTE)
                .addHeader("userid",sp.getString("userid",""))
                .addHeader("cltid",sp.getString("cltid","1"))
                .addHeader("token",sp.getString("token",""))
                .addHeader("mobile",sp.getString("mobile",""))
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String string = response.body().string();
                Message msg=new Message();
                msg.what=1;
                msg.obj=string;
                handler.sendMessage(msg);

            }
        });

    }


    public void showChoosePicDialog02(View v) {

            OkHttpClient okHttpClient=new OkHttpClient();
            FormBody.Builder builder=new FormBody.Builder();
            builder.add("headimgurl",sp.getString("pic",""));
            builder.add("nickname","");
            builder.add("sex","男");
            builder.add("birthday","19961204");
            builder.add("district_id","2");
            builder.add("city_id","2");
            builder.add("children_count","1");
            builder.add("realname","张锋");
            builder.add("children","张葳蕤");

            Request.Builder builder2=new Request.Builder().url(Api.USER_ALTER).post(builder.build());
            //添加请求头
            builder2.addHeader("userid",sp.getString("userid",""));
            builder2.addHeader("cltid",sp.getString("cltid","1"));
            builder2.addHeader("token",sp.getString("token",""));
            builder2.addHeader("mobile",sp.getString("mobile",""));
            final Request request = builder2.build();
            Call call=okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i("rrr",response.body().string().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            setResult(500);

                        }
                    });



                }
            });




    }

	private void takePicture() {
		String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		if (Build.VERSION.SDK_INT >= 23) {
			// 需要申请动态权限
			int check = ContextCompat.checkSelfPermission(this, permissions[0]);
			// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
			if (check != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
		}
		Intent openCameraIntent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment
				.getExternalStorageDirectory(), "image.jpg");
		//判断是否是AndroidN以及更高的版本
		if (Build.VERSION.SDK_INT >= 24) {
			openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			tempUri = FileProvider.getUriForFile(MainActivity.this, "com.lt.uploadpicdemo.fileProvider", file);
		} else {
			tempUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "image.jpg"));
		}
		// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 如果返回码是可以用的
			switch (requestCode) {
				case TAKE_PICTURE:
					startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
					break;
				case CHOOSE_PICTURE:
					startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
					break;
				case CROP_SMALL_PICTURE:
					if (data != null) {
						setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
					}
					break;
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 *
	 * @param uri
	 */
	protected void startPhotoZoom(Uri uri) {
		if (uri == null) {
			Log.i("tag", "The uri is not exist.");
		}
		tempUri = uri;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_SMALL_PICTURE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 *
	 * @param
	 */
	protected void setImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Log.d(TAG,"setImageToView:"+photo);
			//photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
			iv_personal_icon.setImageBitmap(photo);
			uploadPic(photo);
		}
	}

	private void uploadPic(Bitmap bitmap) {
		// 上传至服务器
		// ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
		// 注意这里得到的图片已经是圆形图片了
		// bitmap是没有做个圆形处理的，但已经被裁剪了
		String imagePath = ImageUtils.savePhoto(bitmap, Environment
				.getExternalStorageDirectory().getAbsolutePath(), String
				.valueOf(System.currentTimeMillis()));
		Log.e("imagePath", imagePath+"");
		if(imagePath != null){
			// 拿着imagePath上传了
			// ...
			Log.i(TAG,"imagePath:"+imagePath);
//            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            File file=new File(imagePath);
            OkHttpClient okHttpClient=new OkHttpClient();
            //创建RequestBody 封装file参数
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            //创建RequestBody 设置类型等
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), fileBody)
                    .build();
            //创建Request
            Request request = new Request.Builder().url(Api.USER_IMG)
                    .addHeader("userid",sp.getString("userid",""))
                    .addHeader("cltid",sp.getString("cltid","1"))
                    .addHeader("token",sp.getString("token",""))
                    .addHeader("mobile",sp.getString("mobile",""))
                    .post(requestBody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Message msg=new Message();
                    msg.what=0;
                    msg.obj=string;
                    handler.sendMessage(msg);
                }
            });
		}


	}

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                String s = msg.obj.toString();
                try {
                    JSONObject obj=new JSONObject(s);
                    JSONObject data=obj.optJSONObject("data");
                    Log.e("SSSS",s+"上传图片之后");
                    SharedPreferences.Editor edit = sp.edit();
                    if(data!=null){
                        Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        edit.putString("pic",data.optString("pic"));
                        edit.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (msg.what==1){
                String string = msg.obj.toString();
                Log.i("qqq",string);
                try {
                    JSONObject object=new JSONObject(string);
                    JSONObject data = object.optJSONObject("data");
                    if (data!=null){
                        String headimgurl = data.getString("headimgurl");
                        Log.i("www",headimgurl);
                        DisplayImageOptions options3 = ImageLoaderUtils.getOptions3();
                        ImageLoader.getInstance().displayImage(headimgurl,iv_personal_icon,options3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

		} else {
			// 没有获取 到权限，从新请求，或者关闭app
			Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
		}
	}
}
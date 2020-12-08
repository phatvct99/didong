package Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tutorial_v1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAvatarActivity extends AppCompatActivity {
    Toolbar userAvaTB;
    Button ChonAnhThuVien, ChupAnhMoi,updateAvabtn;
    CircleImageView circleImageView;
    File file;
    Bitmap bitmap;
    MultipartBody.Part body;
    private static final int PICK_IMAGE = 1;
    private static final int IMAGE_CAPTURE = 2;
    UserAccount userAccount=new UserAccount();
    IMyService iMyService;
    boolean flag=false,flag2=false;
    SharedPreferences sharedPreferences;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_avatar);

        setUIReference();
        ActionToolBar();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        userAccount= (UserAccount) getIntent().getSerializableExtra("userAcc");
        String avurl="http://149.28.24.98:9000/upload/user_image/";

        Picasso.get().load(avurl+userAccount.getAva()).placeholder(R.drawable.account_fragment_useravatar).error(R.drawable.account_fragment_useravatar).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);

        ChonAnhThuVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //TODO: choose image using pictures from gallery - DONE

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/png || jpg");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        ChupAnhMoi.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //TODO: choose image using pictures from - DONE

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMAGE_CAPTURE);
            }
        });
        updateAvabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: button click to confirm change profile-picture
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                confirmChangeProfilePicture();

            }
        });
    }

    private void confirmChangeProfilePicture() {
        updateAvabtn.setClickable(false);
        updateAvabtn.setEnabled(false);

        alertDialog.show();
        iMyService.changeAva(body, userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {

                        if(stringResponse.isSuccessful()){


                            if(stringResponse.body().toString().contains("success"))
                            {
                                String responseString=stringResponse.body().toString();
                                try {
                                    JSONObject parent = new JSONObject(responseString);
                                    JSONObject jo = parent.getJSONObject("user");
                                    userAccount.setAva(jo.getString("image"));
                                    flag=true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                flag=false;
                            }}

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);
                        Toast.makeText(UserAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        flag=false;

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);

                        if(flag==true)
                        { Toasty.success(UserAvatarActivity.this, "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();

                            // Truyền data vào intent
                            data.putExtra("usernewAcc", userAccount);

                            // Đặt resultCode là Activity.RESULT_OK to
                            // thể hiện đã thành công và có chứa kết quả trả về
                            setResult(Activity.RESULT_OK, data);

                            // gọi hàm finish() để đóng Activity hiện tại và trở về MainActivity.
                            finish();
                        }
                        else
                            Toast.makeText(UserAvatarActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        updateAvabtn.setEnabled(true);
                        updateAvabtn.setClickable(true);

                    }
                });
    }


    @Override
    protected void onActivityResult(int RequestCode, int ResultCode, Intent data) {

        super.onActivityResult(RequestCode, ResultCode, data);
        if (RequestCode == PICK_IMAGE) {
            if (ResultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                final String path = getPathFromUri(selectedImageUri);

                if (path != null) {
                    file = new File(path);
                    selectedImageUri = Uri.fromFile(file);
                }
                circleImageView.setImageURI(selectedImageUri);
            }
        }
        else {
            if (ResultCode == Activity.RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                saveImage(bitmap, "temp");
                circleImageView.setImageBitmap(bitmap);
            }
        }
    }

    private void saveImage(Bitmap bitmap, String name) {
        String root = getFilesDir().getAbsolutePath();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + name + ".jpg";
        file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root  +fname);
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPathFromUri(Uri contentUri)
    {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if(cursor.moveToFirst()) {
            int column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column);
        }
        cursor.close();
        return res;
    }

    private void ActionToolBar() {
        setSupportActionBar(userAvaTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userAvaTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userAvaTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUIReference() {
        userAvaTB=findViewById(R.id.userAvaTB);
        circleImageView=findViewById(R.id.userAvatar);
        ChonAnhThuVien=findViewById(R.id.ThuVienAnh);
        ChupAnhMoi=findViewById(R.id.ChupAnhMoi);
        updateAvabtn=findViewById(R.id.updateAva);
    }



}
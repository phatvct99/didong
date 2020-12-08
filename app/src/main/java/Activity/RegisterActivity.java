package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;


public class RegisterActivity extends AppCompatActivity {

    EditText TaiKhoanText, NgaySinhText,SdtText, HoTenText, MatKhauText, XacNhanText,DiaChiText,GioiTinhText,MoTaText;
    TextView logText;
    Button RegButton;
    String myDateOfBirth;
    String[] temp;
    String name = "";
    String failText="";
    String username = "";
    String mobile = "";
    String password = "";
    String DateOfBirth ="";
    public static final String URL = "http://149.28.24.98:9000/register";
    String reEnterPassword = "";
    String DiaChi="",MoTa="",GioiTinh="",token="";
    UserAccount userAccount;
    private DatePickerDialog.OnDateSetListener birthdayListener;
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    Boolean flag=false;
    AlertDialog alertDialog;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUIReference();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        iMyService=retrofitClient.create(IMyService.class);

        logText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckValidInput()) {

                    SignUp();
                }

            }
        });

    }

    private void SignUp() {

        RegButton.setClickable(false);
        RegButton.setEnabled(false);

        alertDialog.show();
        iMyService.registerUser(name,username,password,mobile,DiaChi,MoTa,GioiTinh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Response<String> stringResponse) {

                        if (stringResponse.isSuccessful()) {

                            //Convert stringResponse --> JSONObject

                            //Assign userAccount by JSONObject

                            //flag = true;
                            if(stringResponse.body().toString().contains("name"))
                            {
                                String responseString = stringResponse.body().toString();
                                try {
                                    JSONObject jo=new JSONObject(responseString);
                                    userAccount=new UserAccount(jo.getString("name"),"",
                                            jo.getString("phone"),
                                            jo.getString("image"),
                                            jo.getString("email"),
                                            stringResponse.headers().get("Auth-token"),
                                            jo.getString("gender"),
                                            jo.getString("description"),
                                            jo.getString("address"),
                                            password,
                                            jo.getString("_id")
                                    );
                                    flag=true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                flag = false;
                            }



                        } else {
                            //stringResponse = null
                            //feedback error for user
                            failText = "cc";
                            String responseString = null;
                            try {
                                responseString = stringResponse.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            try {
                                JSONObject parent = new JSONObject(responseString);
                                if(responseString.contains("errors")) {
                                    JSONArray jo = parent.getJSONArray("errors");
                                    for (int i = 0; i < jo.length(); i++) {
                                        JSONObject jsonObject = jo.getJSONObject(i);
                                        failText = jsonObject.getString("msg");
                                    }
                                    flag = false;
                                }
                                else {
                                    failText = parent.getString("message");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                failText = e.toString();
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        flag = false;

                    }
                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);


                        if (flag == true) {
                            //New Intent for ActiveAccountActivity
                            Intent intent = new Intent(RegisterActivity.this, ActiveAccountActivity.class);
                            intent.putExtra("userAcc", userAccount);
                            intent.putExtra("change",0);
                            startActivity(intent);

                        } else {
                            Toast.makeText(RegisterActivity.this, failText, Toast.LENGTH_SHORT).show();
                            RegButton.setClickable(true);
                            RegButton.setEnabled(true);
                        }

                    }
                });

    }
    private boolean CheckValidInput() {
        boolean valid = true;

        //get data from user's inputs
        name = HoTenText.getText().toString();
        username = TaiKhoanText.getText().toString().trim();
        mobile = SdtText.getText().toString().trim();
        password = MatKhauText.getText().toString().trim();
        reEnterPassword = XacNhanText.getText().toString().trim();
        DiaChi=DiaChiText.getText().toString();
        MoTa=MoTaText.getText().toString();
        GioiTinh=GioiTinhText.getText().toString();

        //Check validation from user's inputs
        //name, username, mobile, password, confirm password

        if (name == null || name.length() < 3 || name.length() >40)
        {
            HoTenText.setError("Invalid Name");
            valid = false;
        }
        else {
            HoTenText.setError(null);
        }

        if (username == null || username.length() < 6 || username.length() >40)
        {
            TaiKhoanText.setError("Enter valid email");
            valid = false;
        }
        else {
            TaiKhoanText.setError(null);
        }

        if (mobile == null || mobile.length() != 10)
        {
            SdtText.setError("Invalid Phone Number");
            valid = false;
        }
        else {
            SdtText.setError(null);
        }

        if (password == null || password.length() < 8 || password.length() > 16)
        {

            MatKhauText.setError("Invalid Password");
            valid = false;

        }
        else {
            MatKhauText.setError(null);
        }

        if (reEnterPassword == null || !reEnterPassword.equals(password))
        {
            XacNhanText.setError("Wrong Confirm Password");
            valid = false;
        }
        else {
            XacNhanText.setError(null);
        }

        return valid;


    }
    private void setUIReference() {

        TaiKhoanText=findViewById(R.id.taikhoanText);
        SdtText=findViewById(R.id.sdtText);
        HoTenText=findViewById(R.id.HoTenText);
        MatKhauText=findViewById(R.id.matkhauText);
        XacNhanText=findViewById(R.id.xacnhanText);
        logText=findViewById(R.id.logText);
        RegButton=findViewById(R.id.regisBtn);
        GioiTinhText=findViewById(R.id.gioitinhText);
        DiaChiText=findViewById(R.id.diachiText);
        MoTaText=findViewById(R.id.motaText);
    }

}
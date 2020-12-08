package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import Model.courseItem;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;

public class CourseDetailActivity extends AppCompatActivity {

    TextView title;
    courseItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        title = findViewById(R.id.courseDetail_name);

        item = (courseItem) getIntent().getSerializableExtra("item");
        title.setText(item.getTitle());
    }
}
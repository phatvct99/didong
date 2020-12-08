package Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.tutorial_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.categoryRVAdapter;
import Adapter.topCourseAdapter;
import Model.category_item;
import Model.courseItem;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import Retrofit.*;

public class CartFragment extends Fragment{
    //Instances for Course - Lastest (moi nhat)
    RecyclerView recyclerView1;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    Adapter.topCourseAdapter topCourseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_feature, container, false);
        //TODO - New courses
        /*{
            recyclerView1 = rootView.findViewById(R.id.top_course1_recyclerView);
            recyclerView1.setHasFixedSize(true);

            LoadNewCourses();

            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            topCourseAdapter = new topCourseAdapter(getContext(), courseItems, this);
            recyclerView1.setAdapter(topCourseAdapter);
        }*/
        return  rootView;
    }
    boolean flag=false;
    private void LoadNewCourses() {


        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getAllCourse().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(@NonNull String s) {
                        if(s.contains("vote"))
                        {
                            try {
                                JSONArray parent = new JSONArray(s);

                                for(int i=0; i<parent.length(); i++){
                                    JSONObject jo = parent.getJSONObject(i);
                                    JSONObject jo2 = jo.getJSONObject("vote");

                                    courseItem citem = new courseItem();
                                    citem.setTitle(jo.getString("name"));
                                    citem.setTotalVote(jo2.getInt("totalVote"));
                                    citem.setUrl(jo.getString("image"));
                                    citem.setRating(jo.getInt("ranking"));
                                    citem.setFee(jo.getString("price"));
                                    citem.setDiscount(jo.getInt("discount"));
                                    citem.setID(jo.getString("_id"));
                                    Log.v("name", citem.getTitle());
                                    courseItems.add(citem);
                                    topCourseAdapter.notifyDataSetChanged();
                                }
                                flag = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        if(flag == true){
                            Toasty.success(getContext(), "Success2", Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }
}

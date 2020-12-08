package Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
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
import Activity.CourseDetailActivity;
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
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeatureFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeatureFragment extends Fragment implements topCourseAdapter.ItemClickListener {

    //Instance for ImageSlider
    ImageSlider imageSlider;
    ArrayList<SlideModel> imageList = new ArrayList<>();

    //Instances for Course - Lastest (moi nhat), Free (mien phi), Best (Tot nhat)
    RecyclerView recyclerView1,recyclerView2,recyclerView3;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    ArrayList<courseItem> courseItems2 = new ArrayList<>();
    ArrayList<courseItem> courseItems3 = new ArrayList<>();
    Adapter.topCourseAdapter topCourseAdapter,topCourseAdapter2,topCourseAdapter3;

    //Instance for category
    ArrayList<category_item> items = new ArrayList<>();
    categoryRVAdapter adapter;
    RecyclerView recyclerView;


    public FeatureFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_feature, container, false);

        // TODO - Image slider
        {
            imageSlider = rootView.findViewById(R.id.image_slider);

            imageList.add(new SlideModel(R.drawable.slide1, "Java Programming Masterclass for Software Developers"));
            imageList.add(new SlideModel(R.drawable.slide2, "Machine Learning A-Z"));
            imageList.add(new SlideModel(R.drawable.slide3, "Docker & Kubernetes: The Practical Guide"));
            imageList.add(new SlideModel(R.drawable.slide4, "2021 Python for Machine Learning & Data Science Masterclass"));

            imageSlider.setImageList(imageList, true);
        }
        //TODO - Categories
        {
            //LOAD CATEGORY
            recyclerView = rootView.findViewById(R.id.category_recyclerView);
            //recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            LoadAllCategory();

            adapter = new categoryRVAdapter(getContext(), items);
            recyclerView.setAdapter(adapter);
        }

        //TODO - New courses
        {
            recyclerView1 = rootView.findViewById(R.id.top_course1_recyclerView);
            recyclerView1.setHasFixedSize(true);

            LoadNewCourses();

            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            topCourseAdapter = new topCourseAdapter(getContext(), courseItems, this);
            recyclerView1.setAdapter(topCourseAdapter);
        }
        //TODO - freecourse
        {
            recyclerView2 = rootView.findViewById(R.id.top_course1_recyclerView1);
            recyclerView2.setHasFixedSize(true);

            LoadFreeCourses();

            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            topCourseAdapter2 = new topCourseAdapter(getContext(), courseItems2, this);
            recyclerView2.setAdapter(topCourseAdapter2);
        }
        //TODO - top course
        {
            recyclerView3 = rootView.findViewById(R.id.top_course1_recyclerView2);
            recyclerView3.setHasFixedSize(true);

            LoadTopCourse();

            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            topCourseAdapter3 = new topCourseAdapter(getContext(), courseItems3, this);
            recyclerView3.setAdapter(topCourseAdapter3);
        }
        return  rootView;
    }


    private void LoadFreeCourses() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getFreeCourse().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

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
                                    citem.setTotalVote(jo2.getInt("totalVote"));
                                    citem.setDiscount(jo.getInt("discount"));
                                    citem.setRanking(jo.getInt("ranking"));
                                    citem.setCreateAt(jo.getString("created_at"));
                                    citem.setID(jo.getString("_id"));
                                    citem.setTitle(jo.getString("name"));
                                    citem.setAuthorID(jo.getString("idUser"));
                                    citem.setUrl(jo.getString("image"));
                                    citem.setGoal(jo.getString("goal"));
                                    citem.setDesription(jo.getString("description"));
                                    citem.setCategoryID(jo.getString("category"));
                                    citem.setRating(jo2.getInt("EVGVote"));
                                    citem.setFee(jo.getString("price"));
                                    Log.v("name", citem.getTitle());
                                    courseItems2.add(citem);
                                    topCourseAdapter2.notifyDataSetChanged();
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
                        if (flag == true){
                            Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    boolean flag=false;
    boolean flag2=false;
    boolean flag3=false;
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
                                    citem.setTotalVote(jo2.getInt("totalVote"));
                                    citem.setDiscount(jo.getInt("discount"));
                                    citem.setRanking(jo.getInt("ranking"));
                                    citem.setCreateAt(jo.getString("created_at"));
                                    citem.setID(jo.getString("_id"));
                                    citem.setTitle(jo.getString("name"));
                                    citem.setAuthorID(jo.getString("idUser"));
                                    citem.setUrl(jo.getString("image"));
                                    citem.setGoal(jo.getString("goal"));
                                    citem.setDesription(jo.getString("description"));
                                    citem.setCategoryID(jo.getString("category"));
                                    citem.setRating(jo2.getInt("EVGVote"));
                                    citem.setFee(jo.getString("price"));
                                    Log.v("name", citem.getTitle());
                                    courseItems.add(citem);
                                    topCourseAdapter.notifyDataSetChanged();
                                }
                                flag2 = true;
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
                        if(flag2 == true){
                            Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }

    private void LoadTopCourse() {

            IMyService iMyService;
            AlertDialog alertDialog;
            Retrofit retrofitClient = RetrofitClient.getInstance();
            iMyService = retrofitClient.create(IMyService.class);
            alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();
            alertDialog.show();
            iMyService.getTopCourse().
                    subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

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
                                        citem.setTotalVote(jo2.getInt("totalVote"));
                                        citem.setDiscount(jo.getInt("discount"));
                                        citem.setRanking(jo.getInt("ranking"));
                                        citem.setCreateAt(jo.getString("created_at"));
                                        citem.setID(jo.getString("_id"));
                                        citem.setTitle(jo.getString("name"));
                                        citem.setAuthorID(jo.getString("idUser"));
                                        citem.setUrl(jo.getString("image"));
                                        citem.setGoal(jo.getString("goal"));
                                        citem.setDesription(jo.getString("description"));
                                        citem.setCategoryID(jo.getString("category"));
                                        citem.setRating(jo2.getInt("EVGVote"));
                                        citem.setFee(jo.getString("price"));

                                        Log.v("name", citem.getTitle());
                                        courseItems3.add(citem);
                                        topCourseAdapter3.notifyDataSetChanged();
                                    }
                                    flag3 = true;
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
                            if (flag3 == true){
                                Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    boolean flag_category=false;
    private void LoadAllCategory() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getAllCategory().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext( String s) {
                        if (s.contains("_id"))
                        {
                            try {
                                JSONArray parent = new JSONArray(s);

                                for (int i = 0; i < parent.length(); i++){
                                    JSONObject jo = parent.getJSONObject(i);
                                    category_item citem = new category_item(jo.getString("name"),
                                                                            jo.getString("_id"),
                                                                            jo.getString("image"));
                                    items.add(citem);
                                    adapter.notifyDataSetChanged();
                                }
                                flag_category = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            flag_category = false;
                        }
                    }

                    @Override
                    public void onError( Throwable e) {
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
                        if (flag_category == true){
                            Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toasty.success(getContext(), "Fail to upload Category", Toast.LENGTH_SHORT).show();
                        }
                    };
    });
}

    @Override
    public void onClick(ArrayList<courseItem> courseItems, int position) {
        Intent intent = new Intent(getContext(), CourseDetailActivity.class);
        courseItem item = courseItems.get(position);
        intent.putExtra("item", item);
        startActivity(intent);
    }

}

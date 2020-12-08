package Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import Retrofit.*;
import retrofit2.http.Url;

public class SearchFragment extends Fragment implements topCourseAdapter.ItemClickListener{
    SearchView searchView;
    RecyclerView search_courses_recyclerView;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    topCourseAdapter topCourseAdapter;

    RecyclerView search_category_recyclerView;
    ArrayList<category_item> categoryItems = new ArrayList<>();
    categoryRVAdapter categoryAdapter;

    public SearchFragment (){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_search, container, false);

        //TODO - Search Course RecyclerView
        searchView = getActivity().findViewById(R.id.searchView);
        search_courses_recyclerView = rootView.findViewById(R.id.search_items_recyclerView);
        search_courses_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        topCourseAdapter = new topCourseAdapter(getContext(), courseItems, this);
        search_courses_recyclerView.setAdapter(topCourseAdapter);

        //TODO - Search Categories RecyclerView
        search_category_recyclerView = rootView.findViewById(R.id.search_category_recyclerView);
        search_category_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new categoryRVAdapter(getContext(), categoryItems);
        search_category_recyclerView.setAdapter(categoryAdapter);
        LoadAllCategory();

        //TODO - Listener for searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit( String query) {
                if (query.isEmpty())
                    return false;

                rootView.findViewById(R.id.search_category_recyclerView).setVisibility(View.GONE);
                rootView.findViewById(R.id.search_keyword_recyclerView).setVisibility(View.GONE);
                rootView.findViewById(R.id.keyword_text).setVisibility(View.GONE);
                rootView.findViewById(R.id.category_text).setVisibility(View.GONE);
                rootView.findViewById(R.id.search_items_recyclerView).setVisibility(View.VISIBLE);
                QueryTextSubmit(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    rootView.findViewById(R.id.search_category_recyclerView).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.search_keyword_recyclerView).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.keyword_text).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.category_text).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.search_items_recyclerView).setVisibility(View.GONE);
                    courseItems.clear();

                    return true;
                }

                return false;
            }
        });

        return rootView;
    }

    boolean flag = false;
    private void QueryTextSubmit(String query) {
        String urlStr = "/course/search-course/";

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getSearchCourse(urlStr+query).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext( String s) {
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
                                flag = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            flag = false;
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
                        if (flag == true){
                            Toasty.success(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    boolean flag_category = false;
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
                                    categoryItems.add(citem);
                                    categoryAdapter.notifyDataSetChanged();
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

    }
}
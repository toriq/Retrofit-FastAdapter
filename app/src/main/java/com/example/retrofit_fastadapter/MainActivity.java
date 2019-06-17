package com.example.retrofit_fastadapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.retrofit_fastadapter.clients.APIClient;
import com.example.retrofit_fastadapter.models.PostModel;
import com.example.retrofit_fastadapter.services.APIServices;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        progressDialog = new ProgressDialog(context);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        getPosts();
    }

    private void getPosts() {
        final APIServices apiService = APIClient.getInstance().create(APIServices.class);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        apiService.requestForPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postModels -> {
                    //init fast adapter
                    FastItemAdapter<PostModel> fastAdapter = new FastItemAdapter<>();
                    fastAdapter.add(postModels);

                    //fill the recycler view
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(fastAdapter);
                }, throwable -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    System.out.println(throwable.getMessage());
                }, () -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                });
    }
}

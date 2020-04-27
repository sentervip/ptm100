package com.jxcy.smartsensor.view.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.adapter.NurserAdapter;
import com.jxcy.smartsensor.view.WebActivity;
import com.jxcy.smartsensor.view.unit.NurserKnow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NurserFragment extends BaseFragment implements NurserAdapter.NuerserItemListener {
    private View root;
    RecyclerView recyclerView;
    String httpUrl = "http://api.tianapi.com/health/?key=10a88dda465a3bd9c60a57d1899e2775";
    NurserAdapter moreAdapter;
    List<NurserKnow> knowList = new ArrayList<>();
    private int loadNum = 10;
    private boolean loading = false;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.nurser_fragment_layout, container, false);
        initView(root);
        isPrepared = true;
        lazyLoad();
        return root;
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }


    private void initView(View root) {
        recyclerView = root.findViewById(R.id.nurser_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        moreAdapter = new NurserAdapter(knowList);
        recyclerView.setAdapter(moreAdapter);
        moreAdapter.setItemListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && lastVisibleItem >= knowList.size()) {
                    NurserTask nurserTask = new NurserTask();
                    if (loadNum <= 100) {
                        loadNum += 10;
                    }
                    nurserTask.execute(httpUrl + "&num=" + loadNum);
                    loading = true;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        NurserTask nurserTask = new NurserTask();
        nurserTask.execute(httpUrl + "&num=" + loadNum);
    }

    private void retrofitRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.tianapi.com/").addConverterFactory(GsonConverterFactory.create()).build();
        LoreClassTest loreClass = retrofit.create(LoreClassTest.class);

        Call<String> call = loreClass.getLoreEntity("10a88dda465a3bd9c60a57d1899e2775", 10);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    String body = response.body().toString();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface LoreClassTest {
        @GET("health/")
        Call<String> getLoreEntity(@Query("key") String key, @Query("num") int num);
    }


    class NurserTask extends AsyncTask<String, Void, List<NurserKnow>> {
        @Override
        protected List<NurserKnow> doInBackground(String... strings) {
            List<NurserKnow> knowList = new ArrayList<>();
            try {
                StringBuffer buffer = new StringBuffer();
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("GET");
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    buffer.append(strRead);
                    buffer.append("\r\n");
                }
                reader.close();
                JSONObject jsonObject = new JSONObject(buffer.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("newslist");
                if (jsonArray != null) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject subObj = jsonArray.getJSONObject(j);
                        if (subObj != null) {
                            NurserKnow nurserKnow = new NurserKnow();
                            nurserKnow.setTitle(subObj.getString("title"));
                            nurserKnow.setCtime(subObj.getString("ctime"));
                            nurserKnow.setDescription(subObj.getString("description"));
                            nurserKnow.setPicUrl(subObj.getString("picUrl"));
                            nurserKnow.setUrl(subObj.getString("url"));
                            knowList.add(nurserKnow);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return knowList;
        }

        @Override
        protected void onPostExecute(List<NurserKnow> nurserKnows) {
            super.onPostExecute(nurserKnows);
            knowList = nurserKnows;
            moreAdapter.updateLoadState(moreAdapter.LOADING_COMPLETE);
            moreAdapter.updateLoadData(nurserKnows);
            loading = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            moreAdapter.updateLoadState(moreAdapter.LOADING);
        }
    }

    @Override
    public void OnItemListener(NurserKnow item) {
        Intent intent = new Intent(getContext(), WebActivity.class);
        intent.putExtra("ask_uri", item.getUrl());
        getSelfActivity().startActivity(intent);
    }

    @Override
    protected void lazyLoad() {
        if (mHasLoadedOnce || !isPrepared)
            return;
        mHasLoadedOnce = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHasLoadedOnce = false;
        isPrepared = false;
    }
}

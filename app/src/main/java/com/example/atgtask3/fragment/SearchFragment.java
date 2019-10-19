package com.example.atgtask3.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.atgtask3.Adapter;
import com.example.atgtask3.Api.ApiClient;
import com.example.atgtask3.Api.ApiInterface;
import com.example.atgtask3.ModelView;
import com.example.atgtask3.R;
import com.example.atgtask3.User.Foto;
import com.example.atgtask3.User.User;
import com.example.atgtask3.User.model;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static androidx.constraintlayout.widget.Constraints.TAG;


public class SearchFragment extends Fragment implements Serializable {

    RecyclerView recyclerView;
    List<User> userList;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    Adapter adapter;
    Context context;
    int p=1,count=0;
    boolean isscroll=false;
    boolean connected;
    int currentitems,totalitems,viewditems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_search, container, false);
       context=container.getContext();
       setHasOptionsMenu(true);
        recyclerView=view.findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        userList=new ArrayList<>();
        adapter=new Adapter(context,userList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progressBar=view.findViewById(R.id.progressbar);
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            Log.i("internwt","connected");
            LoadJson();
        }else{
            Log.i("internwt","not connected");
            connected=false;
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isscroll=true;
                }


            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentitems=layoutManager.getChildCount();
                totalitems=layoutManager.getItemCount();
                viewditems=layoutManager.findFirstVisibleItemPosition();
                if(isscroll&&(currentitems+viewditems==totalitems)){
                    isscroll=false;

                    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                        Snackbar.make(recyclerView," Internet Connection",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action",null).show();
                        LoadJson();
                        Log.i("internwt","connected");

                    }
                    else{
                        connected = false;
                        Snackbar.make(recyclerView,"No Internet Connection",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action",null).show();
                        Log.i("internwt","not connected");

                    }


                }


            }
        });
        LoadJson();

        return view;
    }

        @Override
        public void onCreateOptionsMenu ( @NonNull final Menu menu,
        @NonNull final MenuInflater inflater){
            super.onCreateOptionsMenu(menu, inflater);
            Log.i("main", "prftff");
            final CompositeDisposable disposables=new CompositeDisposable();
            inflater.inflate(R.menu.search, menu);
            MenuItem searchitem = menu.findItem(R.id.search);
            final SearchView searchView = (SearchView) searchitem.getActionView();
            searchView.setIconifiedByDefault(true);

            Observable<String> observable = Observable
                    .create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(final ObservableEmitter<String> emitter) throws Exception {


                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    if (!emitter.isDisposed()) {

                                        emitter.onNext(newText);
                                    }

                                    return false;
                                }
                            });


                        }
                    })
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                    disposables.add(d);
                }

                @Override
                public void onNext(String s) {
                    Log.i("main", "onNext: time  since last request: ");
                    Log.i("main", "onNext: search query: " + s);
                    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;


                        Log.i("internwt","connected");

                        progressBar.setVisibility(View.VISIBLE);
                        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                        String method = "flickr.photos.search", api_key = "6f102c62f41998d151e5a1b48713cf13";
                        String per_page = "20", page = p + "", format = "json", nojsoncallback = "1", extras = "url_s", text = s;
                        model model = new model();

                        Call<Foto> call;
                        if(s.equals(""))
                        call = apiInterface.getFoto(method, per_page, page, api_key, format, nojsoncallback, extras, "cat");
                        else
                            call = apiInterface.getFoto(method, per_page, page, api_key, format, nojsoncallback, extras, text);
                        call.enqueue(new Callback<Foto>() {
                            @Override
                            public void onResponse(Call<Foto> call, Response<Foto> response) {
                                if (response.isSuccessful() && response.body().getModel().getPhoto() != null) {

                                    userList = response.body().getModel().getPhoto();
                                    //userList.addAll(userList2);

                                    adapter = new Adapter(context, userList);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    Log.i("princejkk", userList.size() + " " + p);
                                    progressBar.setVisibility(View.GONE);

                                } else {
                                    Log.i("prince", "error");

                                }
                            }

                            @Override
                            public void onFailure(Call<Foto> call, Throwable t) {

                                Log.i("prince", t.getMessage());
                            }
                        });
                    }else {
                        Snackbar.make(recyclerView,"You are offline",Snackbar.LENGTH_SHORT)
                                .setAction("Action",null).show();
                        connected=false;
                    }

                    //adapter.getFilter().filter(s);


                }

                @Override
                public void onError(Throwable e) {
                    Log.i("throw error",e.getMessage());
                }

                @Override
                public void onComplete() {
                }
            });

        }




    public  void LoadJson(){
        // tash.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        String method="flickr.photos.search",api_key="6f102c62f41998d151e5a1b48713cf13";
        String per_page="20",page=p+"",format="json",nojsoncallback="1",extras="url_s",text="cat";
        model model=new model();

        Call<Foto> call;
        call = apiInterface.getFoto(method, per_page, page, api_key, format, nojsoncallback, extras,text);

        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful() && response.body().getModel().getPhoto() != null) {

                    userList = response.body().getModel().getPhoto();
                    //userList.addAll(userList2);

                    adapter=new Adapter(context,userList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.i("prince", userList.size() + " "+p);
                    //      tash.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.GONE);





                } else {
                    Log.i("prince", "error");
                    //   Toast.makeText(ModelRepocalss.this,"Loading rerro",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {

                Log.i("prince",t.getMessage());
            }
        });
    }
}

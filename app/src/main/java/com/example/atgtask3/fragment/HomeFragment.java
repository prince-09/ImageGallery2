package com.example.atgtask3.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.example.atgtask3.Adapter;
import com.example.atgtask3.Api.ApiClient;
import com.example.atgtask3.Api.ApiInterface;
import com.example.atgtask3.R;
import com.example.atgtask3.User.Foto;
import com.example.atgtask3.User.User;
import com.example.atgtask3.User.model;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    List<User> userList2;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    Adapter adapter;
    Database database;
    boolean connected;
    DrawerLayout drawerLayout;
    List<User> userList;
    int p=1;
    int count=0;
    ActionBarDrawerToggle mToggle;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isscroll=false;
    int currentitems,totalitems,viewditems;
    Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=container.getContext();
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        userList=new ArrayList<>();

    /*    database= Room.databaseBuilder(context,Database.class,"user")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
*/
        //List<User> users=database.userDao().getAllUsers();

        progressBar=view.findViewById(R.id.progressbar);
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            LoadJson();
        }else{
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
                        LoadJson();
                        if(count==1) {
                            count=0;
                            Snackbar.make(recyclerView, " Back online", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        Log.i("internwt","connected");
                    }
                    else{
                        connected = false;
                        Snackbar.make(recyclerView,"You are offline",Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();
                        Log.i("internwt","not connected");
                        count++;

                    }


                }


            }
        });
        return view;
    }
    public  void LoadJson(){
        // tash.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface= ApiClient.getApiClient().create(ApiInterface.class);
        String method="flickr.photos.getRecent",api_key="6f102c62f41998d151e5a1b48713cf13";
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



                    p++;

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

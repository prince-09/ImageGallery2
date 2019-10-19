package com.example.atgtask3;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.example.atgtask3.Api.ApiClient;
import com.example.atgtask3.Api.ApiInterface;
import com.example.atgtask3.User.Foto;
import com.example.atgtask3.User.User;
import com.example.atgtask3.User.model;
import com.example.atgtask3.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelView{
    List<User> userList=new ArrayList<>();
    int p=1;
    Context context;

    public ModelView(Context context) {
        this.context = context;
    }

    public ModelView(String s) {
        final MutableLiveData<List<User>> user=new MutableLiveData<>();


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String method = "flickr.photos.getRecent", api_key = "6f102c62f41998d151e5a1b48713cf13";
        final String per_page = "20", page = p + "", format = "json", nojsoncallback = "1", extras = "url_s", text = s;
        model model = new model();

        Call<Foto> call;
        call = apiInterface.getFoto(method, per_page, page, api_key, format, nojsoncallback, extras, text);

        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful() && response.body().getModel().getPhoto() != null) {

                    userList = response.body().getModel().getPhoto();
                    Log.i("princejkk", userList.size() + " " + p);
                    //userList.addAll(userList2);

                  /*  adapter = new Adapter(context, userList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.i("princejkk", userList.size() + " " + p);
                    progressBar.setVisibility(View.GONE);*/
                    user.setValue(userList);

                    Intent intent = new Intent(context, SearchFragment.class);
                    intent.putExtra("FILES_TO_SEND", (Parcelable) user);
                    context.startActivity(intent);


                } else {
                    Log.i("prince", "error");

                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {

                Log.i("prince", t.getMessage());
            }
        });


    }

    public MutableLiveData<List<User>> getdata(String s){
        final MutableLiveData<List<User>> user=new MutableLiveData<>();


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String method = "flickr.photos.getRecent", api_key = "6f102c62f41998d151e5a1b48713cf13";
        final String per_page = "20", page = p + "", format = "json", nojsoncallback = "1", extras = "url_s", text = s;
        model model = new model();

        Call<Foto> call;
        call = apiInterface.getFoto(method, per_page, page, api_key, format, nojsoncallback, extras, text);

        call.enqueue(new Callback<Foto>() {
            @Override
            public void onResponse(Call<Foto> call, Response<Foto> response) {
                if (response.isSuccessful() && response.body().getModel().getPhoto() != null) {

                    userList = response.body().getModel().getPhoto();
                    Log.i("princejkk", userList.size() + " " + p);
                    //userList.addAll(userList2);

                  /*  adapter = new Adapter(context, userList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.i("princejkk", userList.size() + " " + p);
                    progressBar.setVisibility(View.GONE);*/
                  user.setValue(userList);

                    Intent intent = new Intent(context, SearchFragment.class);
                    intent.putExtra("FILES_TO_SEND", (Parcelable) user);
                    context.startActivity(intent);


                } else {
                    Log.i("prince", "error");

                }
            }

            @Override
            public void onFailure(Call<Foto> call, Throwable t) {

                Log.i("prince", t.getMessage());
            }
        });
        Log.i("princej", userList.size() + " " + p);
        user.setValue(userList);
        return user;
    }

}

package com.example.atgtask3.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Foto {

    @SerializedName("photos")
    @Expose
    model model;

    public com.example.atgtask3.User.model getModel() {
        return model;
    }

    public void setModel(com.example.atgtask3.User.model model) {
        this.model = model;
    }
}

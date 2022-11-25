package com.example.testmap;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Route {
    @SerializedName("beg")
    public int beg;
    @SerializedName("end")
    public int end;
    @SerializedName("path")
    public List<Integer> path;
}

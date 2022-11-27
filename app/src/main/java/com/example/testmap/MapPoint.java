package com.example.testmap;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MapPoint {
    @SerializedName("name")
    public String name;
    @SerializedName("pos")
    public List<Integer> pos;
    @SerializedName("floor")
    public int floor;
    @SerializedName("spinnerlist")
    public boolean spinnerlist;
    @SerializedName("route")
    public List<Integer> route;
}

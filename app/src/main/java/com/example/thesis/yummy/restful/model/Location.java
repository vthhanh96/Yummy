package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.List;

public class Location extends Base{
    @Json(name = "coordinates") public List<Float> mCoordinates;
}

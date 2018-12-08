package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class Voucher extends Base {

    public static final String HOT_DEAL = "HotDeal";
    public static final String FOODY = "Foody";

    @Json(name = "title") public String mTitle;
    @Json(name = "image") public String mImage;
    @Json(name = "link") public String mLink;
    @Json(name = "host") public String mHost;
    @Json(name = "price") public String mPrice;
    @Json(name = "price_old") public String mPriceOld;
    @Json(name = "price_discount") public String mPriceDiscount;
    @Json(name = "store") public String mStore;
    @Json(name = "location") public String mLocation;
}

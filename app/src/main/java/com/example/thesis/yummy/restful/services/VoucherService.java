package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Voucher;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VoucherService {

    @GET("voucher/{pageNumber}")
    Call<RestResponse<List<Voucher>>> getVouchers(@Path("pageNumber") int pageNumber);

    @GET("voucher/list_voucher_near/{pageNumber}")
    Call<RestResponse<List<Voucher>>> getVouchersNearMe(@Path("pageNumber") int pageNumber);
}

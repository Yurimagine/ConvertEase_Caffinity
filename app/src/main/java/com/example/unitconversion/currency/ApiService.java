package com.example.unitconversion.currency;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    // Fetch all rates for a base currency
    @GET("fetch-all")
    Call<AllRatesResponse> getAllRates(
            @Header("X-API-Key") String apiKey,
            @Query("from") String base
    );
}

package com.example.fyp;

import com.example.fyp.Model.IGoogleAPIService;
import com.example.fyp.Model.RetrofitClient;

public class Common {

    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIServices(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}

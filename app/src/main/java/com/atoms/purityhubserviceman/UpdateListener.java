package com.atoms.purityhubserviceman;

public interface UpdateListener {

    public default void selectCategoryName(String categorySelectedValue, int id){

    }

    public default void selectBrandName(String brandName, int id){

    }

    public default void selectedServiceman(int id){

    }

    public default void openRequest(String requestId){

    }

    public default void closeRequest(String requestId){

    }

    public default void initiateApiCallOpen(String apiCall){

    }

    public default void initiateApiCallPending(String apiCall){

    }

    public default void initiateApiCallClosed(String apiCall){

    }



}

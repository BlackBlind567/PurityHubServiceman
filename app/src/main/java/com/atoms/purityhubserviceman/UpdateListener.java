package com.atoms.purityhubserviceman;

public interface UpdateListener {

    public default void selectCategoryName(String categorySelectedValue, int id){

    }

    public default void selectBrandName(String brandName, int id){

    }

    public default void selectedServiceman(int id){

    }

    public default void openRequest(String requestId, int position){

    }

    public default void closeRequest(String requestId, int position){

    }

   public default void generateBillData(String productId, String productName,
                                        String totalQuantity, String totalPrice){

   }



}

package com.atoms.purityhubserviceman;

public interface UpdateListener {

    public default void selectCategoryName(String categoryName, int id){

    }

    public default void selectBrandName(String brandName, int id){

    }

    public default void selectedServiceman(int id){

    }

}

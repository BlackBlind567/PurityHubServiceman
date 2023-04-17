package com.atoms.purityhubserviceman

import android.content.Context
import android.content.SharedPreferences
import com.atoms.purityhubserviceman.extra.Constants.type
import com.atoms.purityhubserviceman.model.GenerateBill
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by BlackBlind567@Github on 08/08/19.
 */
class Sharedpref {
    lateinit var mEditor: SharedPreferences.Editor

    /**
     * Put long value into sharedpreference
     */
    fun putLong(key: String?, value: Long) {
        try {
            mEditor = mPref!!.edit()
            mEditor.putLong(key, value)
            mEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get long value from sharedpreference
     */
    fun getLong(key: String?): Long {
        return try {
            val lvalue: Long
            lvalue = mPref!!.getLong(key, 0)
            lvalue
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Put int value into sharedpreference
     */
    fun putInt(key: String?, value: Int) {
        try {
            mEditor = mPref!!.edit()
            mEditor.putInt(key, value)
            mEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get int value from sharedpreference
     */
    fun getInt(key: String?): Int {
        return try {
            val lvalue: Int
            lvalue = mPref!!.getInt(key, 0)
            lvalue
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Put String value into sharedpreference
     */
    fun putString(key: String?, value: String?) {
        try {
            mEditor = mPref!!.edit()
            mEditor.putString(key, value)
            mEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get String value from sharedpreference
     */
    fun getString(key: String): String {
        return try {
            val lvalue: String = mPref!!.getString(key, "").toString()
            lvalue
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Put String value into sharedpreference
     */
    fun putBoolean(key: String?, value: Boolean?) {
        try {
            mEditor = mPref!!.edit()
            mEditor.putBoolean(key, value!!)
            mEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get String value from sharedpreference
     */
    fun getBoolean(key: String?): Boolean {
        return try {
            val lvalue: Boolean
            lvalue = mPref!!.getBoolean(key, false)
            lvalue
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearData() {
        try {
            mEditor = mPref!!.edit()
            mEditor.clear()
            mEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> saveArrayList(key: String?, list: ArrayList<T>?) {
        mEditor = mPref!!.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
//        sharedpref.putString(key, json)
        mEditor!!.putString(key, json)
        mEditor!!.apply()
    }

    fun  getArrayList(key: String?): String{
//        val gson = Gson()
//        val json: String = gson.toJson(generateBillArray)
        val json: String? = mPref!!.getString(key, null)

        return json!!
    }

    companion object {
         var mPref: SharedPreferences? = null
         var mRef: Sharedpref? = null

        /**
         * Singleton method return the instance
         */
        fun getInstance(context: Context): Sharedpref {
            if (mRef == null) {
                mRef = Sharedpref()
                mPref =
                    context.applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                return mRef as Sharedpref
            }
            return mRef as Sharedpref
        }
    }
}
package com.axun.bluetoothsendtest;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:dd");


    public static String getCurrentDateTime(){
        return format.format(new Date(System.currentTimeMillis()));
    }
}

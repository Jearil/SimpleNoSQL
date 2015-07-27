package com.rohit.sampleapp;

/**
 * Created by rohit on 26/7/15.
 */
public class Timer {
    static long start;
    static long end;
    public static void start(){
        start = System.nanoTime();
    }
    public static  void end(){
        end = System.nanoTime();
    }
    public static String getTotalTime(){
        String total = ((end-start)/1000000)+"ms";
        return  total;
    }
    public static void reset(){
        start = 0;
        end = 0;
    }
}

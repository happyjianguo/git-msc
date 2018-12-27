package com.shyl.util;
import java.util.Arrays;
import java.util.List;

public class Test {
    public Test() {
    }

    public static void main(String[] args) {
        Long[] longs = new Long[]{1l,2l,3l};
        List<Long> list =Arrays.asList(longs);
        for(final Long l : list){
            (new Thread(new Runnable() {
                public void run() {
                    Test.run1(l.intValue());
                }
            })).start();
        }
    }

    public static void run1(final int k) {
        for(int i = 0; i < 10; ++i) {
            System.out.println(i);
        }

    }
}

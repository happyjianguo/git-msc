package test;

import java.net.URLEncoder;

public class Test20181204 {
    public static void main(String[] args) throws Exception{
        String encode = URLEncoder.encode("中文test#$%^&", "UTF-8");
        System.out.println(encode);
    }
}

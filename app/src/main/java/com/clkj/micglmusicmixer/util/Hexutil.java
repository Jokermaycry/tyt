package com.clkj.micglmusicmixer.util;

import android.util.Log;

public class Hexutil {


    public static String get_hex(String string) {

        while (string.length() < 4) {
            string = "0" + string;
        }

        return string.toUpperCase();
    }

    //2进制转16进制
    public static String binary_to_hex(String string) {
        int temp = Integer.parseInt(string, 2);
        String temp_hex = Integer.toHexString(temp);
        while (temp_hex.length() < 4) {
            temp_hex = "0" + temp_hex;
        }
        return temp_hex;
    }

    //10进制转16进制固定格式
    public static String ten_to_hex(int i) {
        String string = Integer.toHexString(i);
        while (string.length() < 2) {
            string = "0" + string;
        }
        return string;
    }

    //string转byte[]数组;
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }
    public static String makeCheckSum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }

        //用256求余最大是255，即16进制的FF
        int mod = total % 256;
        if (mod == 0) {
            return "FF";
        } else {
            String hex = Integer.toHexString(mod).toUpperCase();

            Log.e("hexutil", "取反前校验位: " + hex);
            //十六进制数取反结果

            return hex;
        }
    }

}

package com.clkj.micglmusicmixer.util;

import android.util.Log;

  public class Hidutil {
    String TAG="Hidutil";
    static  final String START="FFAA80";
    String len;
    String checksum="00";//校验和
    static  final String FINISH="00000000";//8个字节的0

    /**
     *
     * @param TID 0-255
     * @param code 指令字
     * @param data 数据
     * @return
     */
    public byte[] getbytearray(int TID,String code,int data)
    {
        String TID_hex=Hexutil.ten_to_hex(TID);
        String data_hex=Hexutil.ten_to_hex(data).toUpperCase();
        len="0"+String.valueOf((TID_hex+code+data_hex+checksum).length()/2);

        Log.e(TAG,len);
        checksum=Hexutil.makeCheckSum(len+TID_hex+code+data_hex);
        String mix=START+len+TID_hex+code+data_hex+checksum+FINISH;
        Log.e(TAG,mix);
        return  Hexutil.hexStringToByteArray(mix);
    }



}

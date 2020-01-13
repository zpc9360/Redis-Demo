package com.asiainfo.redis.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TestMain {
    public static void main(String[] args){

       String orgId = "11";
        List<String> orgList = new ArrayList<String>();
        orgList.add(orgId);
//        //至少包含一个组织编号
//        String str1 = "[\"0\",\"12\"]";
//        System.out.println(str1);
//        orgList.add(str1);
//        System.out.println(orgList.toString());
        JSONObject mcrmParams = new JSONObject();
        /* 组织推送 */
        mcrmParams.put("pushType", "2");
        String pushType2 = mcrmParams.getString("pushType");
        if (pushType2 == "2") {
            // pushType=2至少包含一个组织编号
            String str1 = "12";
            orgList.add(str1);
        }
        mcrmParams.put("orgList", JSONArray.toJSONString(orgList));
        System.out.println(mcrmParams.getString("orgList"));

    }
}

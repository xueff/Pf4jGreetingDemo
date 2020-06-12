/*
 * Copyright 2012 Decebal Suiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pf4j.demo.welcome;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.demo.api.IMutiVersionConnectionPlugs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Decebal Suiu
 */
public class DB2APlugin extends Plugin {

    public DB2APlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("DB2APlugin.start()");
        // for testing the development mode
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
        	System.out.println(StringUtils.upperCase("DB2APlugin"));
        }
    }

    @Override
    public void stop() {
        System.out.println("DB2APlugin.stop()");
    }

    @Extension
    public static class DB2AConnection implements IMutiVersionConnectionPlugs {
        Connection conn = null;// 创建预编译语句对象，一般都是用这个而不用Statement
        PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
        ResultSet result = null;// 创建一个结果集对象
        final String DB_TYPE = "DB2";
        final String DB_VERSION = "A";

        @Override
        public String getDBType() {
            return DB_TYPE;
        }
        @Override
        public String getVersion() {
            return DB_VERSION;
        }

        @Override
        public List<String> getVersionSupport() {
            return new ArrayList<>();
        }

        @Override
        public Connection getConnection(JSONObject jsonObject) {
            System.out.println("DB2AConnection");

            JSONObject json = new JSONObject();
            json.put("url","jdbc:db2://192.168.200.155:50000/mingandb");
            json.put("password","12345678");
            json.put("username","db2inst1");
            try {
                Class.forName("com.ibm.db2.jcc.DB2Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //获取连接
//            Properties props = new Properties();
            String url = json.getString("url");
            String username = json.getString("username");
            String password = json.getString("password");

            try {
                conn = DriverManager.getConnection(url,username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  conn;
        }

        @Override
        public boolean close() {
    	    boolean isClose = false;
            if(result != null){
                try {
                    if(!result.isClosed()){
                        try {
                            result.close();
                            isClose = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(pre != null){
                try {
                    if(!pre.isClosed()){
                        try {
                            pre.close();
                            isClose = true;
                        } catch (Exception e) {
                            isClose = false;
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    isClose = false;
                    e.printStackTrace();
                }
            }
            if(conn != null){
                try {
                    if(!conn.isClosed()){
                        try {
                            conn.close();
                            isClose = true;
                        } catch (Exception e) {
                            isClose = false;
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    isClose = false;
                    e.printStackTrace();
                }
            }
            return isClose;
        }

        @Override
        public JSONObject getMetaData(JSONObject connectionInfo, Connection conn) {
            System.out.println("DB2A getMetaData");
            JSONObject jsonObject = new JSONObject();
            try {
                pre = conn.prepareStatement("SELECT  * from JCPTB.TEST1 FETCH FIRST 10 ROWS ONLY");
//
                result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        public JSONObject executeSql(JSONObject connectionInfo, Connection conn) {
            System.out.println("DB2A executeSql");
            JSONObject jsonObject = new JSONObject();

            try {
                pre = conn.prepareStatement("SELECT  * from JCPTB.TEST1 FETCH FIRST 10 ROWS ONLY");
//
                result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        public JSONObject fetchRecord(int count) {
            System.out.println("DB2A fetchRecord");
            try {
                if(count > 0) {
                    while (result.next()) {
                        // 当结果集不为空时result.nex
                        for (int i = 1; i <= 5; i++) {

                            try {

                            System.out.print(result.getString(i) + " ");
                            } catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        count --;
                        System.out.println(" ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}

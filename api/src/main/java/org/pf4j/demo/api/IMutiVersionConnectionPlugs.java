package org.pf4j.demo.api;

import com.alibaba.fastjson.JSONObject;
import org.pf4j.ExtensionPoint;

import java.sql.Connection;

public interface IMutiVersionConnectionPlugs extends ExtensionPoint {
    Connection getConnection(JSONObject connectionInfo);
    boolean close();
    JSONObject getMetaData(JSONObject connectionInfo, Connection conn);

    JSONObject executeSql(JSONObject connectionInfo, Connection conn);
    JSONObject fetchRecord(int count);
}
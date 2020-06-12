/*
 * Copyright (C) 2012-present the original author or authors.
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
package org.pf4j.demo;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.demo.api.IMutiVersionConnectionPlugs;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * A boot class that start the demo.
 *
 */
public class Boot {
    public static void main(String[] args) {
        try {

            // print logo
            printLogo();

            // create the plugin manager
            final PluginManager pluginManager = new DefaultPluginManager();
            //        final PluginManager pluginManager = new JarPluginManager();

            // load the plugins
            pluginManager.loadPlugins();

            // enable a disabled plugin
            //        pluginManager.enablePlugin("welcome-plugin");

            // start (active/resolved) the plugins
            pluginManager.startPlugins();

            // retrieves the extensions for Greeting extension point
            List<IMutiVersionConnectionPlugs> greetings = pluginManager.getExtensions(IMutiVersionConnectionPlugs.class);
            System.out.println(String.format("Found %d extensions for extension point '%s'", greetings.size(), IMutiVersionConnectionPlugs.class.getName()));
            System.out.println("获取连接");
            for (IMutiVersionConnectionPlugs greeting : greetings) {
                try {
                    System.out.println("----------获取连接"+greeting.getClass().getName());
                    Connection conn = greeting.getConnection(new JSONObject());
                    greeting.executeSql(new JSONObject(),conn);
                    greeting.fetchRecord(10);

                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        System.out.println("捕获Runtime 异常：" + e.getMessage());
                    } else {
                        System.out.println("捕获未知异常：" + e.getMessage());

                    }
                }
            }

            // print extensions from classpath (non plugin)
            System.out.println("Extensions added by classpath:");
            Set<String> extensionClassNames = pluginManager.getExtensionClassNames(null);
            for (String extension : extensionClassNames) {
                System.out.println("   " + extension);
            }

            // print extensions ids for each started plugin
            List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
            for (PluginWrapper plugin : startedPlugins) {
                String pluginId = plugin.getDescriptor().getPluginId();
                System.out.println(String.format("Extensions added by plugin '%s':", pluginId));
                extensionClassNames = pluginManager.getExtensionClassNames(pluginId);
                for (String extension : extensionClassNames) {
                    System.out.println("   " + extension);
                }
            }

            // print the extensions instances for Greeting extension point for each started plugin
            for (PluginWrapper plugin : startedPlugins) {
                String pluginId = plugin.getDescriptor().getPluginId();
                System.out.println(String.format("Extensions instances added by plugin '%s' for extension point '%s':", pluginId, IMutiVersionConnectionPlugs.class.getName()));
                List<IMutiVersionConnectionPlugs> extensions = pluginManager.getExtensions(IMutiVersionConnectionPlugs.class, pluginId);
                for (Object extension : extensions) {
                    System.out.println("   " + extension);
                }
            }

            // print extensions instances from classpath (non plugin)
            System.out.println("Extensions instances added by classpath:");
            List extensions = pluginManager.getExtensions((String) null);
            for (Object extension : extensions) {
                System.out.println("   " + extension);
            }

            // print extensions instances for each started plugin
            for (PluginWrapper plugin : startedPlugins) {
                String pluginId = plugin.getDescriptor().getPluginId();
                System.out.println(String.format("Extensions instances added by plugin '%s':", pluginId));
                extensions = pluginManager.getExtensions(pluginId);
                for (Object extension : extensions) {
                    System.out.println("   " + extension);
                }
            }

            // stop the plugins
            pluginManager.stopPlugins();
            /*
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    pluginManager.stopPlugins();
                }

            });
            */
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printLogo() {
        System.out.println(StringUtils.repeat("#", 40));
        System.out.println(StringUtils.center("PF4J-DEMO", 40));
        System.out.println(StringUtils.repeat("#", 40));
    }
}

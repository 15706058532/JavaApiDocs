package com.citrsw.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * 写点注释
 *
 * @author 李振峰
 * @version 1.0
 */
public class ApiUtils {
    private static final Map<String, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("Integer", "Int");
        TYPE_MAP.put("int", "Int");
        TYPE_MAP.put("Long", "Int");
        TYPE_MAP.put("long", "Int");
        TYPE_MAP.put("Boolean", "Bool");
        TYPE_MAP.put("boolean", "Bool");
        TYPE_MAP.put("Float", "Float");
        TYPE_MAP.put("float", "Float");
        TYPE_MAP.put("Double", "Double");
        TYPE_MAP.put("double", "Double");
        TYPE_MAP.put("String", "String");
        TYPE_MAP.put("Date", "Date");
        TYPE_MAP.put("LocalDateTime", "Date");
        TYPE_MAP.put("LocalDate", "Date");
        TYPE_MAP.put("LocalTime", "Date");
        TYPE_MAP.put("short", "short");
        TYPE_MAP.put("Object", "Any");
        TYPE_MAP.put("MultipartFile", "[Int]");
    }


    /**
     * java类型转Ios类型
     *
     * @param className
     * @return 返回类型名
     */
    public static String javaToIosType(String className) {
        return TYPE_MAP.get(className);
    }

    /**
     * 重新生成符合泛型形式的Type
     * 用于返回形式的泛型
     *
     * @param returnType 返回的Type
     * @return 符合泛型形式的Type
     */
    public static Type regenerateType(Type returnType) {
        if (returnType instanceof ParameterizedType) {
            // 强制类型转换
            ParameterizedType pType = (ParameterizedType) returnType;
            Type rawType = pType.getRawType();
            List<Type> types = new ArrayList<>();
            for (Type actualTypeArgument : pType.getActualTypeArguments()) {
                types.add(regenerateType(actualTypeArgument));
            }
            return ParameterizedTypeImpl.make((Class<?>) rawType, types.toArray(new Type[0]), null);
        } else {
            return returnType;
        }
    }

    /**
     * 获取内网IP
     *
     * @return 返回Ip列表
     */
    public static List<String> getLocalIps() throws Exception {
        //IP
        List<String> localIps = new ArrayList<>();
        Enumeration<NetworkInterface> netInterfaces;
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        // 是否找到外网IP
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    // 外网IP
                    localIps.add(ip.getHostAddress());
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    // 内网IP
                    localIps.add(ip.getHostAddress());
                }
            }
        }
        return localIps;
    }
}

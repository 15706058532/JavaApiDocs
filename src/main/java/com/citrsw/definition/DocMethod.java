package com.citrsw.definition;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 方法信息
 *
 * @author 李振峰
 * @version 1.0
 * @date 2020-09-23 23:12
 */
@Data
@Accessors(chain = true)
public class DocMethod implements Comparable<DocMethod> {

    /**
     * 描述
     */
    private String description;

    /**
     * 请求方式
     */
    @JsonProperty("mode_set")
    private Set<String> modeSet;

    /**
     * 请求地址
     */
    @JsonProperty("uri_set")
    private Set<String> uriSet;

    /**
     * 入参必传参数(key:全名称，value:字段属性)
     */
    @JsonIgnore
    private Map<String, DocProperty> paramRequireMap;

    /**
     * form-data入参
     */
    private Set<DocProperty> params;

    /**
     * json入参
     */
    @JsonProperty("param_json")
    private String paramJson;

    /**
     * json入参例子
     */
    @JsonProperty("param_example")
    private String paramExample;

    /**
     * json出参
     */
    @JsonProperty("return_json")
    private String returnJson;

    /**
     * 生成入参安卓实体类代码
     */
    @JsonProperty("param_android")
    public String paramAndroid;

    /**
     * 生成入参响应安卓实体类代码
     */
    @JsonProperty("return_android")
    public String returnAndroid;
    /**
     * 生成入参IOS实体类代码
     */
    @JsonProperty("param_ios")
    public String paramIos;

    /**
     * 生成响应IOS实体类代码
     */
    @JsonProperty("return_ios")
    public String returnIos;

    /**
     * 生成请求Vue代码
     */
    @JsonProperty("param_vue")
    public Map<String, Map<String, String>> paramVue;

    /**
     * 生成响应Vue代码
     */
    @JsonProperty("return_vue")
    public Map<String, Map<String, String>> returnVue;

    /**
     * 状态码
     */
    @JsonProperty("doc_codes")
    private Set<DocCode> docCodes = new TreeSet<>();


    @Override
    public int compareTo(DocMethod o) {
        if (StringUtils.isNotBlank(description) && (StringUtils.isNotBlank(o.description))) {
            return description.compareTo(o.description);
        }
        if (StringUtils.isNotBlank(description)) {
            return 1;
        }
        if (StringUtils.isNotBlank(o.description)) {
            return -1;
        }
        return 0;
    }
}

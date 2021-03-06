package com.citrsw.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 模型信息
 *
 * @author Zhenfeng Li
 * @version 1.0
 * @date 2020-09-23 23:14
 */
@Data
@Accessors(chain = true)
public class DocModel {
    /**
     * 描述
     */
    private String description;

    /**
     * 是否为数组类型
     */
    private String type;

    /**
     * 类名称
     */
    @JsonIgnore
    private String className;

    /**
     * 属性集合 包括只有get方法的虚拟属性
     */
    private Set<DocProperty> apiProperties = new TreeSet<>();

    /**
     * 形式  json,form-data
     */
    private String form;

    public Set<DocProperty> params() {
        if (StringUtils.isNotBlank(form) && "json".equals(form)) {
            return new LinkedHashSet<>();
        }
        if (StringUtils.isNotBlank(type) && !type.contains("[0]")) {
            return apiProperties;
        }
        Set<DocProperty> apiProperties = new LinkedHashSet<>();
        for (DocProperty docProperty : this.apiProperties) {
            DocProperty property = new DocProperty();
            property.setName(docProperty.getName());
            property.setDescription(docProperty.getDescription());
            property.setType(docProperty.getType());
            property.setFormat(docProperty.getFormat());
            property.setRequited(docProperty.getRequited());
            property.setDefaultValue(docProperty.getDefaultValue());
            property.setExample(docProperty.getExample());
            property.setDocModel(docProperty.getDocModel());
            apiProperties.addAll(docProperty.param(property));
        }
        return apiProperties;
    }

    public String paramJson() {
        if (StringUtils.isNotBlank(form) && "form-data".equals(form)) {
            return "";
        }
        if (StringUtils.isNotBlank(type) && !type.contains("[0]")) {
            return type;
        }
        StringBuilder builder = new StringBuilder();
        String tab = "";
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            tab = "    ";
            builder.append("[").append("\r\n");
        }
        builder.append(tab).append("{");
        for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
            DocProperty docProperty = it.next();
            String json = docProperty.getJson(new StringBuilder("    ").append(tab), true, false);
            if (!json.startsWith("[") && !it.hasNext()) {
                json = json.replaceFirst("\\,", "");
            }
            builder.append(tab).append("    ").append(json);
        }
        builder.append("\r\n").append(tab).append("}");
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            builder.append("\r\n").append("]");
        }
        return builder.toString();
    }

    public String paramExample() {
        if (StringUtils.isNotBlank(form) && "form-data".equals(form)) {
            return "";
        }
        if (StringUtils.isNotBlank(type) && !type.contains("[0]")) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String tab = "";
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            tab = "    ";
            builder.append("[").append("\r\n");
        }
        builder.append(tab).append("{");
        for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
            DocProperty docProperty = it.next();
            String json = docProperty.getJson(new StringBuilder("    ").append(tab), true, true);
            if (!json.startsWith("[") && !it.hasNext()) {
                json = json.replaceFirst("\\,", "");
            }
            builder.append(tab).append("    ").append(json);
        }
        builder.append("\r\n").append(tab).append("}");
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            builder.append("\r\n").append("]");
        }
        return builder.toString();
    }

    public String returnJson() {
        if (StringUtils.isNotBlank(type) && !type.contains("[0]")) {
            String json = apiProperties.iterator().next().getJson(new StringBuilder(), false, false);
            return json.substring(json.indexOf("//") + 2);
        }
        StringBuilder builder = new StringBuilder();
        String tab = "";
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            tab = "    ";
            builder.append("[").append("\r\n");
        }
        builder.append(tab).append("{");
        for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
            DocProperty docProperty = it.next();
            String json = docProperty.getJson(new StringBuilder("    ").append(tab), false, false);
            if (!json.startsWith("[") && !it.hasNext()) {
                json = json.replaceFirst("\\,", "");
            }
            builder.append(tab).append("    ").append(json);
        }
        builder.append("\r\n").append(tab).append("}");
        if (StringUtils.isNotBlank(type) && type.contains("[0]")) {
            builder.append("\r\n").append("]");
        }
        return builder.toString();
    }

    /**
     * 生成安卓实体类代码
     */
    public void android(Set<String> strings) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(className)) {
            if (StringUtils.isNotBlank(description)) {
                builder.append("/**\r\n * ").append(description).append("\r\n */\r\n");
            }
            builder.append("@Accessors(chain = true)\r\n").append("@Data\r\n");
            builder.append("public class ").append(className).append(" {");
        }
        for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
            DocProperty docProperty = it.next();
            String android = docProperty.android(strings);
            builder.append(android);
        }
        if (StringUtils.isNotBlank(className) && builder.length() > 0) {
            builder.append("\r\n}\r\n\r\n");
        }
        strings.add(builder.toString());
    }

    /**
     * 生成IOS实体类代码
     */
    public void ios(Set<String> strings) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(className)) {
            if (StringUtils.isNotBlank(description)) {
                builder.append("/**\r\n * ").append(description).append("\r\n */\r\n");
            }
            builder.append("class ").append(className).append(" {");
        }
        for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
            DocProperty docProperty = it.next();
            String android = docProperty.ios(strings);
            builder.append(android);
        }
        if (StringUtils.isNotBlank(className) && builder.length() > 0) {
            builder.append("\r\n}\r\n\r\n");
        }
        strings.add(builder.toString());
    }

    /**
     * 生成请求Vue代码
     */
    public String paramVue() {
        StringBuilder builder = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(className)) {
            builder.append("---------------html----------------------").append("\r\n");
            builder.append("<el-dialog title=\"").append("新增");
            if (StringUtils.isNotBlank(description)) {
                builder.append(description);
            }
            builder.append("\" center :visible.sync=\"visible\" width=\"30%\">").append("\r\n");
            builder.append("    ").append("<el-form :model=\"").append(StringUtils.uncapitalize(className)).append("\" :rules=\"rules\" ref=\"").append(StringUtils.uncapitalize(className)).append("\" ").append("\r\n");
            builder.append("              ").append("label-width=\"100px\" @keyup.enter.native=\"save").append(className).append("()\">").append("\r\n");
            stringBuilder.append("rules: {").append("\r\n");
            for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
                DocProperty docProperty = it.next();
                stringBuilder.append(docProperty.paramVue(builder, StringUtils.uncapitalize(className)));
            }
            builder.append("    ").append("</el-form>").append("\r\n");
            builder.append("    ").append("<span slot=\"footer\" class=\"dialog-footer\">").append("\r\n");
            builder.append("        ").append("<el-button type=\"primary\" @click=\"save").append(className).append("()\" size=\"mini\">提 交</el-button>").append("\r\n");

            builder.append("    ").append("</span>").append("\r\n");
            builder.append("</el-dialog>").append("\r\n").append("\r\n");
            ;

            builder.append("---------------data----------------------").append("\r\n");
            builder.append(StringUtils.uncapitalize(className)).append(": {},").append("\r\n");
            builder.append("visible: false,").append("\r\n");
            stringBuilder.deleteCharAt(stringBuilder.length() - 3);
            stringBuilder.append("},").append("\r\n");
            stringBuilder.append("---------------method----------------------").append("\r\n");
            stringBuilder.append("save").append(className).append("() {").append("\r\n");
            stringBuilder.append("},").append("\r\n");
            builder.append(stringBuilder);
        }
        return builder.toString();
    }

    /**
     * 生成响应Vue代码
     */
    public String returnVue() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(className)) {
            builder.append("---------------html----------------------").append("\r\n");
            builder.append("<el-table size=\"mini\" :data=\"").append(StringUtils.uncapitalize(className)).append("s\">").append("\r\n");
            for (Iterator<DocProperty> it = apiProperties.iterator(); it.hasNext(); ) {
                DocProperty docProperty = it.next();
                docProperty.returnVue(builder, StringUtils.uncapitalize(className));
            }
            builder.append("</el-table>").append("\r\n");
            builder.append("<el-pagination").append("\r\n");
            builder.append("    ").append("background").append("\r\n");
            builder.append("    ").append(":current-page=\"page.current*1\"").append("\r\n");
            builder.append("    ").append(":page-sizes=\"[10, 20, 50,100]\"").append("\r\n");
            builder.append("    ").append("layout=\"->,sizes,total,prev, pager, next,jumper\"").append("\r\n");
            builder.append("    ").append("@current-change=\"function(v) {").append("\r\n");
            builder.append("          ").append("page.current = v").append("\r\n");
            builder.append("          ").append("query").append(className).append("s();").append("\r\n");
            builder.append("        ").append("}\"").append("\r\n");
            builder.append("    ").append("@size-change=\"function(v) {").append("\r\n");
            builder.append("          ").append("page.size = v").append("\r\n");
            builder.append("          ").append("query").append(className).append("s();").append("\r\n");
            builder.append("        ").append("}\"").append("\r\n");
            builder.append("    ").append(":total=\"page.total*1\">").append("\r\n");
            builder.append("</el-pagination>").append("\r\n").append("\r\n");

            builder.append("---------------data----------------------").append("\r\n");
            builder.append(StringUtils.uncapitalize(className)).append("s: [],").append("\r\n");
            builder.append("page: {}");
        }
        return builder.toString();
    }

}

package com.citrsw.definition;

import com.citrsw.common.ApiUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 属性信息
 *
 * @author Zhenfeng Li
 * @version 1.0
 * @date 2020-09-23 23:13
 */
@Data
@Accessors(chain = true)
public class DocProperty implements Comparable<DocProperty> {

    /**
     * 名称
     */
    private String name;

    /**
     * 类名称
     */
    @JsonIgnore
    private String className;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private String type = "";

    /**
     * 格式
     */
    private String format;

    /**
     * 是否必须
     */
    private Boolean requited;

    /**
     * 默认值
     */
    @JsonProperty("default_value")
    private String defaultValue;

    /**
     * 示例
     */
    private String example;

    /**
     * 子属性
     */
    private DocModel docModel;

    /**
     * form-data形式
     */
    public Set<DocProperty> param(DocProperty parentDocProperty) {
        Set<DocProperty> apiProperties = new LinkedHashSet<>();
        if (docModel != null && !docModel.getApiProperties().isEmpty()) {
            for (DocProperty docProperty : docModel.getApiProperties()) {
                DocProperty property = new DocProperty();
                property.setName(docProperty.getName());
                property.setDescription(docProperty.getDescription());
                String typeName = docProperty.getType();
                if (docProperty.getDocModel() != null) {
                    typeName = typeName.replaceAll("\\[0\\]", "[]");
                }
                property.setType(typeName);
                property.setFormat(docProperty.getFormat());
                property.setRequited(docProperty.getRequited());
                property.setDefaultValue(docProperty.getDefaultValue());
                property.setExample(docProperty.getExample());
                property.setDocModel(docProperty.getDocModel());
                String newName = StringUtils.isNotBlank(property.getName()) ? "." + property.getName() : "";
                property.setName(parentDocProperty.getName() + parentDocProperty.getType().replaceAll(parentDocProperty.getType().replaceAll("\\[0\\]", ""), "") + newName);
                apiProperties.addAll(property.param(property));
            }
        } else {
            parentDocProperty.setType(parentDocProperty.getType().replaceAll("\\[0\\]", "[]"));
            apiProperties.add(parentDocProperty);
        }
        return apiProperties;
    }

    /**
     * json形式
     */
    public String getJson(StringBuilder tabs, boolean isParam, boolean isExample) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(name) && docModel == null && StringUtils.isBlank(type.replaceAll("\\[0\\]", ""))) {
            return "";
        }
        builder.append("\r\n").append(tabs).append(name).append(": ");
        if (docModel != null && !docModel.getApiProperties().isEmpty()) {
            if (type.contains("[0]")) {
                String[] split = type.split("0\\]");
                StringBuilder tabBuilder = new StringBuilder(tabs);
                for (int i = 0; i < split.length; i++) {
                    builder.append("[").append("\r\n").append(tabs);
                    for (int j = 0; j <= i; j++) {
                        builder.append("    ");
                        tabBuilder.append("    ");
                    }
                }
                builder.append("{");
                if (StringUtils.isNotBlank(description) && !isExample) {
                    builder.append(" //").append(description);
                }
                for (Iterator<DocProperty> it = docModel.getApiProperties().iterator(); it.hasNext(); ) {
                    DocProperty docProperty = it.next();
                    String json = docProperty.getJson(new StringBuilder(tabBuilder).append("    "), isParam, isExample);
                    if (!json.contains("[") && !json.contains("{") && !it.hasNext()) {
                        json = json.replaceFirst(",", "");
                    }
                    builder.append(json);
                }
                builder.append("\r\n").append(tabBuilder).append("}").append("\r\n");
                if (tabBuilder.length() > 0) {
                    tabBuilder.delete(tabBuilder.length() - 4, tabBuilder.length());
                }
                for (int i = split.length; i > 0; i--) {
                    for (int j = i - 1; j > 0; j--) {
                        builder.append("    ");
                    }
                    builder.append(tabs).append("]").append("\r\n");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            }
            builder.append("{");
            if (StringUtils.isNotBlank(description) && !isExample) {
                builder.append(" //").append(description);
            }
            for (Iterator<DocProperty> it = docModel.getApiProperties().iterator(); it.hasNext(); ) {
                DocProperty docProperty = it.next();
                String json = docProperty.getJson(new StringBuilder(tabs).append("    "), isParam, isExample);
                if (!json.contains("[") && !json.contains("{") && !it.hasNext()) {
                    json = json.replaceFirst(",", "");
                }
                builder.append(json);
            }


            builder.append("\r\n").append(tabs).append("}");
            return builder.toString();
        }
        if (isExample) {
            if (StringUtils.isBlank(type)) {
                builder.append("null,");
            } else {
                builder.append("\"");
                if (StringUtils.isNotBlank(defaultValue)) {
                    builder.append(defaultValue);
                }
                builder.append("\",");
            }
            return builder.toString();
        }

        if (type.contains("[0]")) {
            if (StringUtils.isBlank(type.replaceAll("\\[0\\]", ""))) {
                builder.append("null").append(", //");
            } else {
                String[] split = type.split("0\\]");
                for (int i = 0; i < split.length; i++) {
                    builder.append("[");
                }
                builder.append("\"");
                if (StringUtils.isNotBlank(defaultValue)) {
                    builder.append(defaultValue);
                }
                builder.append("\"");
                for (int i = 0; i < split.length; i++) {
                    builder.append("]");
                }
                builder.append(", //").append(type.replaceAll("\\[0\\]", "[]"));
                if (StringUtils.isNotBlank(description) || StringUtils.isNotBlank(format) || isParam) {
                    builder.append(" | ");
                }
            }
        } else if (StringUtils.isBlank(type)) {
            builder.append("null").append(", //");
        } else {
            builder.append("\"");
            if (StringUtils.isNotBlank(defaultValue)) {
                builder.append(defaultValue);
            }
            builder.append("\"");
            builder.append(", //").append(type);
            if (StringUtils.isNotBlank(description) || StringUtils.isNotBlank(format) || isParam) {
                builder.append(" | ");
            }
        }
        if (StringUtils.isNotBlank(description)) {
            builder.append(description);
            if (isParam) {
                builder.append(" | ");
            }
        }
        if (isParam) {
            builder.append(requited != null && requited ? "必传" : "非必传");
            if (StringUtils.isNotBlank(format)) {
                builder.append(" | ").append(format);
            }
            if (StringUtils.isNotBlank(example)) {
                builder.append(" | ").append(example);
            }
        } else {
            if (StringUtils.isNotBlank(format)) {
                builder.append(" | ").append(format);
            }
        }
        return builder.toString();
    }

    /**
     * 生成安卓实体类代码
     */
    public String android(Set<String> strings) {
        if (docModel != null && !docModel.getApiProperties().isEmpty()) {
            docModel.android(strings);
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(description)) {
                builder.append("\n\n    /**\r\n     * ").append(description).append("\r\n     */");
            }
            StringBuilder classNameBuilder;
            if (StringUtils.isBlank(this.className) || this.className.startsWith("Map")) {
                classNameBuilder = new StringBuilder("Map<String, ");
                classNameBuilder.append(docModel.getApiProperties().iterator().next().className);
                classNameBuilder.append(">");
            } else {
                classNameBuilder = new StringBuilder(this.className);
                if (type.contains("[0]")) {
                    classNameBuilder = new StringBuilder("List");
                    String[] split = type.split("0\\]");
                    for (int i = 0; i < split.length; i++) {
                        classNameBuilder.append("<");
                    }
                    classNameBuilder.append(this.className);
                    for (int i = 0; i < split.length; i++) {
                        classNameBuilder.append(">");
                    }
                }
            }
            builder.append("\r\n    private").append(" ").append(classNameBuilder).append(" ").append(name).append(";");
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(description)) {
                builder.append("\n\n    /**\r\n     * ").append(description).append("\r\n     */");
            }
            StringBuilder classNameBuilder = new StringBuilder(this.className);
            if (type.contains("[0]")) {
                classNameBuilder = new StringBuilder("List");
                String[] split = type.split("0\\]");
                for (int i = 0; i < split.length; i++) {
                    classNameBuilder.append("<");
                }
                classNameBuilder.append(this.className);
                for (int i = 0; i < split.length; i++) {
                    classNameBuilder.append(">");
                }
            }
            builder.append("\r\n    private").append(" ").append(classNameBuilder).append(" ").append(name).append(";");
            return builder.toString();
        }
    }

    /**
     * 生成IOS实体类代码
     *
     * @param strings
     */
    public String ios(Set<String> strings) {
        String iosType = ApiUtils.javaToIosType(this.className);
        iosType = StringUtils.isBlank(iosType) ? this.className : iosType;
        if (docModel != null && !docModel.getApiProperties().isEmpty()) {
            docModel.ios(strings);
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(description)) {
                builder.append("\n\n    /**\r\n     * ").append(description).append("\r\n     */");
            }
            StringBuilder classNameBuilder;
            if (StringUtils.isBlank(this.className) || this.className.startsWith("Map")) {
                classNameBuilder = new StringBuilder("[String: ");
                String className = ApiUtils.javaToIosType(docModel.getApiProperties().iterator().next().className);
                className = StringUtils.isBlank(className) ? this.className : className;
                classNameBuilder.append(className);
                classNameBuilder.append("]");
            } else {
                classNameBuilder = new StringBuilder(iosType);
                if (type.contains("[0]")) {
                    classNameBuilder = new StringBuilder();
                    String[] split = type.split("0\\]");
                    for (int i = 0; i < split.length; i++) {
                        classNameBuilder.append("[");
                    }
                    classNameBuilder.append(iosType);
                    for (int i = 0; i < split.length; i++) {
                        classNameBuilder.append("]");
                    }
                    classNameBuilder.append(" = []");
                }
            }
            builder.append("\r\n    var").append(" ").append(name).append(": ").append(classNameBuilder);
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            if (StringUtils.isNotBlank(description)) {
                builder.append("\n\n    /**\r\n     * ").append(description).append("\r\n     */");
            }
            StringBuilder classNameBuilder = new StringBuilder(iosType);
            if (type.contains("[0]")) {
                classNameBuilder = new StringBuilder();
                String[] split = type.split("0\\]");
                for (int i = 0; i < split.length; i++) {
                    classNameBuilder.append("[");
                }
                classNameBuilder.append(iosType);
                for (int i = 0; i < split.length; i++) {
                    classNameBuilder.append("]");
                }
                classNameBuilder.append(" = []");
            }
            builder.append("\r\n    var").append(" ").append(name).append(": ").append(classNameBuilder);
            return builder.toString();
        }
    }

    @Override
    public int compareTo(DocProperty o) {
        if (StringUtils.isNotBlank(o.name) && StringUtils.isNotBlank(name)) {
            return name.compareTo(o.name);
        }
        if (StringUtils.isNotBlank(name)) {
            return 1;
        }
        if (StringUtils.isNotBlank(o.name)) {
            return -1;
        }
        return 0;
    }

    public String paramVue(StringBuilder builder, String className) {
        if (docModel == null) {
            if (!type.contains("[0]")) {
                builder.append("        ").append("<el-form-item");
                if (StringUtils.isNotBlank(description)) {
                    builder.append(" label=\"").append(description).append("\"");
                }
                builder.append(" prop=\"").append(name).append("\">").append("\r\n");
                builder.append("            ").append("<el-input v-model=\"").append(className).append(".").append(name).append("\"");
                if (StringUtils.isNotBlank(description)) {
                    builder.append(" placeholder=\"").append(description).append("\"");
                }
                builder.append("></el-input>").append("\r\n");

                builder.append("        ").append("</el-form-item>").append("\r\n");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("    ").append(name).append(": [").append("\r\n");
                stringBuilder.append("        ").append("{ required: true, message: '请输入");
                if (StringUtils.isNotBlank(description)) {
                    stringBuilder.append(description);
                }
                stringBuilder.append("', trigger: 'blur' }").append("\r\n");
                stringBuilder.append("    ").append("],").append("\r\n");
                return stringBuilder.toString();
            }
        }
        return "";
    }

    public void returnVue(StringBuilder builder, String className) {
        if (docModel == null) {
            if (!type.contains("[0]")) {
                builder.append("    ").append("<el-table-column").append("\r\n");
                if (StringUtils.isNotBlank(description)) {
                    builder.append("        ").append("label=\"").append(description).append("\"").append("\r\n");
                }
                builder.append("        ").append("prop=\"").append(name).append("\">").append("\r\n");
                builder.append("    ").append("</el-table-column>").append("\r\n");
            }
        }
    }
}

package com.sff.rbacdemo.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sff.rbacdemo.common.model.BaseEntity;
import lombok.Data;

/**
 * @author Frankie Fan
 * @date 2022-04-14 10:16
 * 字典管理表
 */

@Data
@TableName("t_dict")
public class Dict extends BaseEntity {

    private static final long serialVersionUID = 7780820231535870010L;

    @TableId(value = "DICT_ID", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dictId;

    @TableField("DICT_CODE")
    private String dictCode;

    @TableField("DICT_NAME")
    private String dictName;

    @TableField("DESCRIPTION")
    private String description;

}
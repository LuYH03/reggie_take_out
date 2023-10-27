package org.example.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("员工")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("员工账户")
    private String username;
    @ApiModelProperty("员工名称")
    private String name;
    @ApiModelProperty("员工密码")
    private String password;
    @ApiModelProperty("员工手机号")
    private String phone;
    @ApiModelProperty("员工性别")
    private String sex;
    @ApiModelProperty("身份证号码")
    private String idNumber;   // 身份证号码
    @ApiModelProperty("状态")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)   // 插入时自动填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)   // 插入时自动填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时填充字段
    private Long updateUser;

}

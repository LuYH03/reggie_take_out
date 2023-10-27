package org.example.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
/**
 * 用户信息
 */
@Data
@ApiModel("用户")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("姓名")
    //姓名
    private String name;

    @ApiModelProperty("手机号")
    //手机号
    private String phone;

    @ApiModelProperty("性别")
    //性别 0 女 1 男
    private String sex;

    @ApiModelProperty("身份证号")
    //身份证号
    private String idNumber;

    @ApiModelProperty("头像")
    //头像
    private String avatar;

    @ApiModelProperty("状态")
    //状态 0:禁用，1:正常
    private Integer status;
}

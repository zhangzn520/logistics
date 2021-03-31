package com.egao.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangfan on 2018-12-24 16:10
 */
@ApiModel(description = "登录日志")
@TableName("sys_login_record")
public class LoginRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int TYPE_LOGIN = 0;  // 登录
    public static final int TYPE_ERROR = 1;  // 登录失败
    public static final int TYPE_LOGOUT = 2;  // 退出登录
    public static final int TYPE_REFRESH = 3;  // 刷新token

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户账号")
    private String username;

    @ApiModelProperty("操作系统")
    private String os;

    @ApiModelProperty("设备名")
    private String device;

    @ApiModelProperty("浏览器类型")
    private String browser;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("操作类型:0登录,1退出登录,2刷新token,3登录失败")
    private Integer operType;

    @ApiModelProperty("备注")
    private String comments;

    @ApiModelProperty("操作时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("用户id")
    @TableField(exist = false)
    private Integer userId;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getOperType() {
        return operType;
    }

    public void setOperType(Integer operType) {
        this.operType = operType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "LoginRecord{" +
                ", id=" + id +
                ", userId=" + userId +
                ", os=" + os +
                ", device=" + device +
                ", browser=" + browser +
                ", ip=" + ip +
                ", operType=" + operType +
                ", comments=" + comments +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", username=" + username +
                ", nickname=" + nickname +
                "}";
    }
}

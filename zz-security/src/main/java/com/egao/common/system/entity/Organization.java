package com.egao.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by AutoGenerator on 2020-03-14 11:29:04
 */
@ApiModel(description = "组织机构")
@TableName("sys_organization")
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机构id")
    @TableId(value = "organization_id", type = IdType.AUTO)
    private Integer organizationId;

    @ApiModelProperty(value = "上级id,0是顶级")
    private Integer parentId;

    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    @ApiModelProperty(value = "机构全称")
    private String organizationFullName;

    @ApiModelProperty(value = "机构代码")
    private String organizationCode;

    @ApiModelProperty(value = "机构类型")
    private Integer organizationType;

    @ApiModelProperty(value = "负责人id")
    private Integer leaderId;

    @ApiModelProperty(value = "排序号")
    private Integer sortNumber;

    @ApiModelProperty(value = "备注")
    private String comments;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除,0否,1是")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "负责人姓名")
    @TableField(exist = false)
    private String leaderName;

    @ApiModelProperty(value = "负责人账号")
    @TableField(exist = false)
    private String leaderAccount;

    @ApiModelProperty(value = "上级名称")
    @TableField(exist = false)
    private String parentName;

    @ApiModelProperty(value = "机构类型名称")
    @TableField(exist = false)
    private String organizationTypeName;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationFullName() {
        return organizationFullName;
    }

    public void setOrganizationFullName(String organizationFullName) {
        this.organizationFullName = organizationFullName;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderAccount() {
        return leaderAccount;
    }

    public void setLeaderAccount(String leaderAccount) {
        this.leaderAccount = leaderAccount;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getOrganizationTypeName() {
        return organizationTypeName;
    }

    public void setOrganizationTypeName(String organizationTypeName) {
        this.organizationTypeName = organizationTypeName;
    }

    @Override
    public String toString() {
        return "Organization{" +
                ", organizationId=" + organizationId +
                ", parentId=" + parentId +
                ", organizationName=" + organizationName +
                ", organizationFullName=" + organizationFullName +
                ", organizationCode=" + organizationCode +
                ", organizationType=" + organizationType +
                ", leaderId=" + leaderId +
                ", sortNumber=" + sortNumber +
                ", comments=" + comments +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleted=" + deleted +
                ", leaderName=" + leaderName +
                ", parentName=" + parentName +
                ", organizationTypeName=" + organizationTypeName +
                "}";
    }

}

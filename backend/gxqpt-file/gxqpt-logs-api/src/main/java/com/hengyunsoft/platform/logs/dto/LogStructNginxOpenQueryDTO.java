package com.hengyunsoft.platform.logs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 按查询条件显示
 */
@Data
@ApiModel(value = "LogStructNginxOpenQuery", description = "(对外开放)Nginx结构化日志查询参数对象")
public class LogStructNginxOpenQueryDTO {
    /**
     *日志记录（响应）条件：开始时间
     */
    @ApiModelProperty(value = "记录日志时间（条件开始） yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date localTimeStart;
    /**
     *日志记录（响应）条件：结束时间
     */
    @ApiModelProperty(value = "记录日志时间（条件结束）yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date localTimeEnd;
    /**
     *用户所请求的URL路径
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "用户所请求的URL路径")
    private String requestUrl;
    /**
     *为响应请求而耗费的开始时间，以秒计
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "为响应请求而耗费的开始时间，以秒计")
    private Long timeConsumStart;
    /**
     *为响应请求而耗费的结束时间，以秒计
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "为响应请求而耗费的结束时间，以秒计")
    private Long timeConsumEnd;
    /**
     *远程IP地址(客户端ip)
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "客户端ip")
    private String remoteAddr;
    /**
     *状态(多个以，号分隔)
     *
     * @mbggenerated
     */
    @ApiModelProperty(value = "状态 比如：200|300|404|500等，多个以，号分隔")
    private String status;
}

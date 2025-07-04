package com.typer.subject.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.typer.subject.common.Enum.RespCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.typer.subject.common.Enum.RespCode.FAILED;
import static com.typer.subject.common.Enum.RespCode.OK;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resp<T> {
    @ApiModelProperty(value = "返回码. 0: 成功; 其他: 失败。在失败时一般data为空")
    private int code;

    @ApiModelProperty(value = "返回信息. 例如: ok; failed", example = "ok")
    private String msg;


    @ApiModelProperty(value = "实际的数据内容，可能为空")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static Resp<Void> ok() {
        return Resp.ok(null);
    }

    public static <T> Resp<T> ok(T data) {
        return new Resp(OK.code, OK.msg, data);
    }

    public static Resp<Void> failed(String msg) {
        return failed(msg, null);
    }

    public static <T> Resp<T> failed(String msg, T data) {
        return new Resp(FAILED.code, msg,data);
    }

    public static Resp<Void> of(RespCode respCode) {
        return of(respCode, respCode.msg);
    }

    public static Resp<Void> of(RespCode respCode, String msg) {
        return of(respCode, msg, null);
    }

    public static <T> Resp<T> of(RespCode respCode, String msg, T data) {
        return new Resp<>(respCode.code, msg, data);
    }
}

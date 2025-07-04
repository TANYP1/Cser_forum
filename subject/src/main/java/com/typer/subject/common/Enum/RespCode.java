package com.typer.subject.common.Enum;

public enum RespCode {
    OK(0, "ok"),
    FAILED(1, "failed");

    public final int code;
    public final String msg;

    RespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

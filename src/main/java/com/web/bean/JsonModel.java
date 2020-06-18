package com.web.bean;

import java.io.Serializable;

public class JsonModel<T> implements Serializable {

	private static final long serialVersionUID = 5407940216386940476L;
	
	private Integer code;
	private String errorMsg;
	private T t;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public T getT() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	@Override
	public String toString() {
		return "JsonModel [code=" + code + ", errorMsg=" + errorMsg + ", t=" + t + "]";
	}
	
}	

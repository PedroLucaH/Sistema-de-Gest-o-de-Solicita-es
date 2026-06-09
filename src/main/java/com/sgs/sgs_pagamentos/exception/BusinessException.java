package com.sgs.sgs_pagamentos.exception;



public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
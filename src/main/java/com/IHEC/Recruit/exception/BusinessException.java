package com.IHEC.Recruit.exception;

/** Erreur métier : entrée invalide, état incohérent, etc. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

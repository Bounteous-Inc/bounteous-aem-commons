/*
 * ***********************************************************************
 * BOUNTEOUS CONFIDENTIAL
 * ___________________
 *
 * Copyright 2019 Bounteous
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property
 * of Bounteous and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Bounteous
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Bounteous.
 * ***********************************************************************
 */
package com.bounteous.aem.compgenerator.exceptions;

public class GeneratorException extends RuntimeException {

    private static final long serialVersionUID = -548600394249617384L;

    public GeneratorException(String errorMessage) {
        super(errorMessage);
    }

}
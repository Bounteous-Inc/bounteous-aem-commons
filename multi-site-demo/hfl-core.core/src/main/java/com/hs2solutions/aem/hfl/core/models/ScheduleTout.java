package com.hs2solutions.aem.hfl.core.models;

import aQute.bnd.annotation.ConsumerType;

import java.math.BigDecimal;

@ConsumerType
public interface ScheduleTout {
    String getLogoSrc();

    String getMessage();

    String getCtaUrl();

    String getCtaLabel();
}

package com.hs2solutions.aem.hfl.core.models;

import aQute.bnd.annotation.ConsumerType;

@ConsumerType
public interface Header {
    String getHomePageUrl();

    String getLogoSrc();

    String getSlogan();
}

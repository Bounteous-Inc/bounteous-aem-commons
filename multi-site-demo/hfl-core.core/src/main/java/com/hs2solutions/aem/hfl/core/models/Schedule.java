package com.hs2solutions.aem.hfl.core.models;

import aQute.bnd.annotation.ConsumerType;

@ConsumerType
public interface Schedule {
    boolean getAllowFilter();

    String getCopyFilterAll();

    String getCopyFilterHome();

    String getCopyFilterAway();

    String getCopyLabelHome();

    String getCopyLabelAway();
}

package org.sma.platform.common.datamodel;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
public class ApiBaseModel implements Serializable {

    private static final long SerialVersionUID = 11l;

}


package org.sma.platform.common.datamodel.app;

import org.sma.platform.common.datamodel.ApiBaseModel;

public class ResultStatus extends ApiBaseModel {
    public static final String RESULT_STATUS_PROPERTY_NAME = "ResultStatus";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String ERROR_CODE_PROPERTY = "ErrorCode";
    public static final String ERROR_MSG_PROPERTY = "ErrorMessage";

    private Status status;
    private String exceptionType;
    private String errorCode;
    private String errorMessage;

    public ResultStatus() {
        this.status = Status.SUCCESS;
    }

    /**
     * @param status
     */
    public ResultStatus(Status status) {
        this.status = status;
    }

    /**
     * @param status
     * @param errorCode
     * @param errorMessage
     */
    public ResultStatus(Status status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

}

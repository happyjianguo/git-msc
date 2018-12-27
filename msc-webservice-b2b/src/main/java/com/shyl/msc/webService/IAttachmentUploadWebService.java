package com.shyl.msc.webService;

public interface IAttachmentUploadWebService {
    /**
     * 单据附件上传
     * @param sign
     * @param dataType
     * @param data
     * @return
     */
    public String send(String sign, String dataType, String data);
}

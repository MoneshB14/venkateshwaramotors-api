package com.monesh.venkateswaramotors.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailFileUploadResponse {
    
    private boolean success;
    private String message;
    private String messageId;
    private Integer filesCount;
    private List<FileInfo> filesInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private String name;
        private Long size;
        private String type;
    }
}

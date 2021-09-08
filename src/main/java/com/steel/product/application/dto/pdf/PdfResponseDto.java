package com.steel.product.application.dto.pdf;

public class PdfResponseDto {
    private String encodedBase64String;

    public PdfResponseDto() {
    }

    public PdfResponseDto(String encodedBase64String) {
        this.encodedBase64String = encodedBase64String;
    }

    public String getEncodedBase64String() {
        return encodedBase64String;
    }

    public void setEncodedBase64String(String encodedBase64String) {
        this.encodedBase64String = encodedBase64String;
    }
}

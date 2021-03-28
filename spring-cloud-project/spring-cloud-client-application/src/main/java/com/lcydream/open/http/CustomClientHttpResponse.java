package com.lcydream.open.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class CustomClientHttpResponse implements ClientHttpResponse, Serializable {

    private HttpStatus httpStatus;

    private InputStream inputStream;

    private HttpHeaders httpHeaders;

    private int rawStatusCode;

    @Override
    public HttpStatus getStatusCode()  {
        return this.httpStatus;
    }

    @Override
    public int getRawStatusCode()  {
        return 0;
    }

    @Override
    public String getStatusText()  {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getBody()  {
        return this.inputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }
}
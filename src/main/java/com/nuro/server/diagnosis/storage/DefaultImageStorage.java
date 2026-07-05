package com.nuro.server.diagnosis.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

//@Component
public class DefaultImageStorage implements ImageStorage {

    @Override
    public String store(byte[] bytes, MimeType mimeType) {
        return "mock://image";
    }
}
package com.yoann.pay_my_buddy.service;

import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Service for managing file playback
 */
@Service
public class ReaderService {

    public BufferedReader createFileBufferedReader(String path) throws FileNotFoundException {
        FileReader fileReader = new FileReader(path);

        return new BufferedReader(fileReader);
    }
}

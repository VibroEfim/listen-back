package com.tcz.listen.services;

import org.springframework.stereotype.Service;

@Service
public class CodeService {
    public String random() {
        int startCode = 65; //A
        int endCode = 90; //Z
        int codeSize = 6;

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < codeSize; i++) {
            char sym = (char) ((Math.random() * (endCode - startCode)) + startCode);
            code.append(sym);
        }

        return code.toString();
    }
}

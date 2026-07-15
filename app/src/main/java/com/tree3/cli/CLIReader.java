package com.tree3.cli;

import com.tree3.util.ThreadUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLIReader {
    public void start() {
        ThreadUtil.runTaskAsync(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, "cli-reader");
    }
}

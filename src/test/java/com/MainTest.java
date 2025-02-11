package com;

import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    void testDelete() {
        Main main = new Main();
        String result = main.delete("abbaca");
        System.out.println("results:" + result);
    }
}

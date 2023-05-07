package com.dzhatdoev.eurekaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class EurekaClientApplication {

    static {
        Solution solution = new Solution();
        System.out.println(solution.isValid("()"));
        System.out.println(solution.isValid("()[]{}"));
        System.out.println(solution.isValid("(]"));
    }

    static class Solution {
        public boolean isValid(String s) {
            boolean isValid = true;
            char [] array = s.toCharArray();
            if (array.length%2==1) return false;
            if (array.length == 2 && array[0] == array[1]) return true;

            for(int i = 0; i<s.length()-1; i++) {

                if (Objects.equals(array[i], '(')) {
                    if (Objects.equals(array[i + 1], ')')) continue;
                } else if (Objects.equals(array[i], '[')) {
                    if (Objects.equals(array[i + 1], ']')) continue;
                } else if (Objects.equals(array[i], '{')) {
                    if (Objects.equals(array[i + 1], '}')) continue;
                }
                isValid = false;
            }
            return isValid;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}

package com.demidovich.helpers;

import androidx.annotation.NonNull;

import java.util.Random;

public class CreatePass {

    public String createRandomPassword() {
        Random random = new Random();
        int randomNumber = random.ints(8, 16)
                .findFirst()
                .getAsInt();
        return createRandomStrongForPassword(randomNumber);
    }

    @NonNull
    public String createRandomStrongForPassword(int randomNumber) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789<>/\\!@$%^&*()_+=-{}|"
                .toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < randomNumber; i++) {
            char c = chars[rand.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}

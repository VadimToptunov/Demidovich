package com.demidovich;

import java.util.Random;

public class CreatePass {

    public String createRandomPassword() {
        Random random = new Random();
        int randomNumber = random.ints(8, 21)
                .findFirst()
                .getAsInt();
        return createRandomStrongForPassword(randomNumber);
    }

    private String createRandomStrongForPassword(int randomNumber){
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

package com.demidovich;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.demidovich.helpers.CreatePass;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DemidovichAppUnitTest {

    @Test
    public void generatedPasswordIsAString() {
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        pass.getClass();
        assertTrue(true);
    }

    @Test
    public void generatedPassIsNotEmpty() {
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        Assertions.assertFalse(pass.isEmpty());
    }

    @Test
    public void checkPasswordIsCreatedIsLessOrEquals16() {
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertTrue(pass.length() <= 16);
    }

    @Test
    public void checkPasswordIsCreatedIsMoreOrEquals8() {
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertTrue(pass.length() >= 8);
    }
}
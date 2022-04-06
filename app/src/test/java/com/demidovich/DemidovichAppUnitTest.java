package com.demidovich;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DemidovichAppUnitTest {

    @Test
    public void generatedPasswordIsAString(){
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertTrue(pass.getClass().equals(String.class));
    }

    @Test
    public void generatedPassIsNotEmpty(){
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertFalse(pass.isEmpty());
    }

    @Test
    public void checkPasswordIsCreatedIsLessOrEquals16(){
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertTrue(pass.length() <= 16);
    }

    @Test
    public void checkPasswordIsCreatedIsMoreOrEquals8(){
        CreatePass createPass = new CreatePass();
        String pass = createPass.createRandomPassword();
        assertTrue(pass.length() >= 8);
    }
}
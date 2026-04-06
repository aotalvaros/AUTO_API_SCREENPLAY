package com.cyberguard.automation.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class ApiHooks {

    private static final String BASE_URL = "http://127.0.0.1:3000";

    @Before
    public void setTheStage() {
        OnStage.setTheStage(Cast.whereEveryoneCan(CallAnApi.at(BASE_URL)));
    }

    @After
    public void drawTheCurtain() {
        OnStage.drawTheCurtain();
    }
}

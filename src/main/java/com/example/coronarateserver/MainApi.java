package com.example.coronarateserver;

import com.example.coronarateserver.basicAuth.LoginForm;
import com.example.coronarateserver.basicAuth.VerifyUser;
import com.example.coronarateserver.utils.MainUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainApi implements ErrorController {

    @Autowired
    MainUtils utils;

    @Autowired
    VerifyUser verifyUser;

    @RequestMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public String AllData (@RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, false))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSON("https://corona.lmao.ninja/all", false).toString();
    }

    @RequestMapping("/StatesOfUSA")
    public String StatesOfUSACountry (@RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, false))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSON("https://corona.lmao.ninja/states", true).toString();
    }

    @RequestMapping(value = "/historical/{country}", method = RequestMethod.POST)
    public String Historical (@PathVariable String country, @RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, false))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSON("https://corona.lmao.ninja/v2/historical/" + country, false).toString();
    }

    @RequestMapping(value = "/country/{country}", method = RequestMethod.POST)
    public String SpecificCountry (@PathVariable String country, @RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, false))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSON("https://corona.lmao.ninja/countries/" + country, false).toString();
    }

    @RequestMapping("/CurrencyLiverate/EUR")
    public String CurrencyLiverateEUR (@RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, true))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSONForRates("https://openexchangerates.org/api/latest.json?app_id=de9cf5b18dd040daa63940e2dfedcb42&base=EUR", "EUR").toString();
    }

    @RequestMapping("/CurrencyLiverate/USD")
    public String CurrencyLiverateUSD (@RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, true))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSONForRates("https://openexchangerates.org/api/latest.json?app_id=de9cf5b18dd040daa63940e2dfedcb42&base=USD", "USD").toString();
    }

    @RequestMapping("/CurrencyLiverate/BTC")
    public String CurrencyLiverateBTC (@RequestBody LoginForm loginForm){
        if (!verifyUser.verifyUserAndPassword(loginForm, true))
            return "{\"response\":\"incorrect user and\\or password\"}";
        return utils.getJSONForRates("https://openexchangerates.org/api/latest.json?app_id=de9cf5b18dd040daa63940e2dfedcb42&base=BTC", "BTC").toString();
    }

    @GetMapping("/error")
    public String ErrorGET (){
        return "{\"response\":\"incorrect user and\\or password\"}";
    }

    @PostMapping("/error")
    public String ErrorPost (){
        return "{\"response\":\"incorrect user and\\or password\"}";
    }

    @Override
    public String getErrorPath() {
        return "{\"response\":\"incorrect user and\\or password\"}";
    }
}

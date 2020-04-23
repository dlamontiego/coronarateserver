package com.example.coronarateserver.utils;

import com.example.coronarateserver.entity.Country;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static sun.util.logging.LoggingSupport.log;

@Service
public class MainUtils {
    @Autowired
    private SubUtils subUtils;

    private static HostnameVerifier getHostnameVerifier() {
        return (hostname, session) -> true;
    }

    public JSONObject getJSON(String sURL, boolean isAmerica){

        try {
            // Make a URL to the web page
            URL url = new URL(sURL);

            // Get the input stream through URL Connection
            URLConnection con = url.openConnection();

            //later to validate secure connections HTTPS
//            if ("https".equals(url.getProtocol())) {
//                try {
//                    ((HttpsURLConnection) con).setHostnameVerifier(getHostnameVerifier()) ;
//                } catch (Exception e) {
//
//                }
//            }

            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36");
            con.connect();
            InputStream is =con.getInputStream();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

            if (isAmerica){
                JSONArray jsonArray = new JSONArray(response.toString());
                return new JSONObject().put("states", jsonArray);
            } else {
                return new JSONObject(response.toString());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getJSONForRates(String sURL, String base){
        try {
            // Make a URL to the web page
            URL url = new URL(sURL);

            // Get the input stream through URL Connection
            URLConnection con = url.openConnection();

            //later to validate secure connections HTTPS
//            if ("https".equals(url.getProtocol())) {
//                try {
//                    ((HttpsURLConnection) con).setHostnameVerifier(getHostnameVerifier()) ;
//                } catch (Exception e) {
//
//                }
//            }

            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36");
            con.connect();
            InputStream is =con.getInputStream();

            //Get Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

            JSONObject jsonTemp = new JSONObject(response.toString());

            JSONObject finalJSON = new JSONObject();
            finalJSON.put("base", base);
            JSONArray jsonRates = fixRate(jsonTemp.getJSONObject("rates"), base);
            finalJSON.put("openexchangerates.org", jsonRates);

            JSONObject JSONKraken = new JSONObject();
            JSONObject JSONKrakenRates = ExtraSource(base, "KRAKEN");
            JSONKraken.put("rates", JSONKrakenRates);
            finalJSON.put("KRAKEN", JSONKraken);

            JSONObject JSONBINANCE = new JSONObject();
            JSONObject JSONBINANCERates = ExtraSource(base, "BINANCE");
            JSONBINANCE.put("rates", JSONBINANCERates);
            finalJSON.put("BINANCE", JSONBINANCE);

            JSONObject JSONCOINBASE = new JSONObject();
            JSONObject JSONCOINBASERates = ExtraSource(base, "COINBASE");
            JSONCOINBASE.put("rates", JSONCOINBASERates);
            finalJSON.put("COINBASE", JSONCOINBASE);

            JSONObject JSONBITFINEX = new JSONObject();
            JSONObject JSONBITFINEXRates = ExtraSource(base, "BITFINEX");
            JSONBITFINEX.put("rates", JSONBITFINEXRates);
            finalJSON.put("BITFINEX", JSONBITFINEX);

            return new JSONObject(finalJSON.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private JSONArray fixRate(JSONObject rates, String base) {
        JSONArray replace = new JSONArray();
        try {
            Document doc = Jsoup.connect("http://mesghal.com/").get();
            Element IRRValueElement = doc.select("#Mesghal div#Prices table tbody tr:nth-child(2) td:nth-child(3)").first();

//            String temp = IRRValueElement.html();
            String IRRValue = IRRValueElement.text();

            switch (base) {
                case "USD":
                case "EUR":
                case "BTC":
                    double IRR = Double.parseDouble(IRRValue) * rates.getDouble("USD");
                    rates.put("IRR", IRR);
                    rates.put("IRT", IRR * 10);
                    rates.put("GPK", rates.getDouble("XAU") * 32.15);

                    for (Currency currency :  java.util.Currency.getAvailableCurrencies()) {
                        JSONArray details = new JSONArray();
                        details.put(currency.getCurrencyCode() + " - " + currency.getDisplayName());
                        try {
                            details.put(new JSONObject().put("value", rates.get(currency.getCurrencyCode())));
                        } catch (JSONException e) {
                            details.put(new JSONObject().put("value", " - "));
                        }
                        replace.put(new JSONObject().put(currency.getCurrencyCode(), details));
                    }
                    JSONArray details = new JSONArray();
                    details.put("IRT - Iranian Toman");
                    details.put(new JSONObject().put("value", rates.get("IRT")));
                    replace.put(new JSONObject().put("IRT", details));

                    details = new JSONArray();
                    details.put("GPK - Gold per Kilogram");
                    details.put(new JSONObject().put("value", rates.get("GPK")));
                    replace.put(new JSONObject().put("GPK", details));

                    break;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return replace;
    }

    private void getListOfCountries(){
        // Create a collection of all available countries
        List<Country> countries = new ArrayList<>();

        // Map ISO countries to custom country object
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes){

            Locale locale = new Locale("", countryCode);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            countries.add(new Country(iso, code, name));
        }

        // Sort countries
        Collections.sort(countries);
    }

    private JSONObject ExtraSource(String base, String source) {
        JSONArray array = new JSONArray();
        String krakerURL;
        String jsonAnswer;
        JSONObject readytoAPI = new JSONObject();
        try {
            switch (source) {
                case "KRAKEN":
                    switch (base) {
                        case "EUR":
                            // GETTING URL FOR KRAKEN EUR to every available currency
                            krakerURL = subUtils.getKrakenCurrenciesURL("EUR", subUtils.krakenEURCurrencies);
                            jsonAnswer = subUtils.jsonGetRequest(krakerURL);
                            readytoAPI = subUtils.processKrakenAnswer(jsonAnswer);
                            break;
                        case "USD":
                            // GETTING URL FOR KRAKEN USD to every available currency
                            krakerURL = subUtils.getKrakenCurrenciesURL("USD", subUtils.krakenUSDCurrencies);
                            jsonAnswer = subUtils.jsonGetRequest(krakerURL);
                            readytoAPI = subUtils.processKrakenAnswer(jsonAnswer);
                            break;
                        case "BTC":
                            // GETTING URL FOR KRAKEN USD to every available currency
                            krakerURL = subUtils.getKrakenCurrenciesURL("XBT", subUtils.krakenXBTtoCurrencies);
                            jsonAnswer = subUtils.jsonGetRequest(krakerURL);
                            readytoAPI = subUtils.processKrakenAnswer(jsonAnswer);
                            break;
                    }
                    break;
                case "COINBASE":
                    switch (base) {
                        case "EUR":
                            JSONObject EURUSD = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/EUR-USD/spot"));
                            array.put(EURUSD);
    //                        JSONObject EURBTC = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/EUR-BTC/spot"));/it does not exist
    //                        array.put(EURBTC);
                            break;
                        case "USD":
                            JSONObject USDEUR = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/USD-EUR/spot"));
                            array.put(USDEUR);
    //                        JSONObject USDBTC = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/USD-BTC/spot"));/it does not exist
    //                        array.put(USDBTC);
                            break;
                        case "BTC":
                            JSONObject BTCUSD = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/BTC-USD/spot"));
                            array.put(BTCUSD);
                            JSONObject BTCEUR = new JSONObject(subUtils.jsonGetRequest("https://api.coinbase.com/v2/prices/BTC-EUR/spot"));
                            array.put(BTCEUR);
                            break;
                    }
                    readytoAPI.put("values", array);
                    break;
                case "BINANCE":
                    switch (base) {
                        case "EUR":
                            //binance so far it doesn't support convertion from EUR
                            break;
                        case "USD":
                            //binance so far it doesn't support convertion from USD
                            break;
                        case "BTC":
                            JSONObject BTCEUR = new JSONObject(subUtils.jsonGetRequest("https://api.binance.com/api/v3/avgPrice?symbol=BTCEUR"));
                            BTCEUR.put("currency", "EUR");
                            BTCEUR.put("base", base);
                            array.put(BTCEUR);
                            break;
                    }
                    readytoAPI.put("values", array);
                    break;
                    case "BITFINEX":
                    switch (base) {
                        case "EUR":
                            //binance so far it doesn't support convertion from EUR
                            break;
                        case "USD":
                            //binance so far it doesn't support convertion from USD
                            break;
                        case "BTC":
                            //it return an array that inside of each element contais by position: price, count, amount
                            JSONArray BTCEURArray = new JSONArray(subUtils.jsonGetRequest("https://api-pub.bitfinex.com/v2/book/tBTCEUR/P0?len=1"));
                            JSONObject BTCEUR = new JSONObject();
                            BTCEUR.put("amount", ((JSONArray) BTCEURArray.get(0)).get(0));
                            BTCEUR.put("currency", "EUR");
                            BTCEUR.put("base", base);

                            JSONArray BTCUSDArray = new JSONArray(subUtils.jsonGetRequest("https://api-pub.bitfinex.com/v2/book/tBTCUSD/P0?len=1"));
                            JSONObject BTCUSD = new JSONObject();
                            BTCUSD.put("amount", ((JSONArray) BTCUSDArray.get(0)).get(0));
                            BTCUSD.put("currency", "USD");
                            BTCUSD.put("base", base);

                            array.put(BTCEUR);
                            array.put(BTCUSD);
                            break;
                    }
                    readytoAPI.put("values", array);
                    break;
            }
            return readytoAPI;
        } catch (JSONException e) {
            e.printStackTrace();
            return readytoAPI;
        }
    }
}

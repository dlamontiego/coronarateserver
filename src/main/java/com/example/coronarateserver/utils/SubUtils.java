package com.example.coronarateserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class SubUtils {
    // creating Arrays of String type
    String[] sourceCurrencies = new String[] { "EUR", "USD", "XBT" };
    String[] tarjetCurrencies = new String[] { "EUR", "CHF", "USD", "GBP", "JPY" };

    // ALL PAIRS AVAILABLE IN KRAKEN
    // https://api.kraken.com/0/public/AssetPairs
    String[] krakenEURCurrencies = new String[] { "CAD", "JPY", "CHF", "GBP" };

    // https://api.kraken.com/0/public/AssetPairs
    String[] krakenUSDCurrencies = new String[] { "CHF" };

    String[] krakenXBTtoCurrencies = new String[] { "EUR", "USD",
            // "BCH", #Not valid tested
            // "ADA", #Not valid tested
            // "LINK", #Not valid tested
//			 "ATOM",
            // "DASH", #NOT VALID TESTED
//			 "XDG",
//			"EOS",
//			 "ETH",
//			 "ETC",
//			 "GNO",
//			 "ICX",
//			 "LSK",
//			 "LTC",
//			 "MLN",
//			 "OMG",
//			 "PAXG",
//			 "XMR",
//			 "NANO",
//			 "QTUN",
//			"XRP","SC" ,"XLM","XTZ","TRX","WAVES","ZEC"
    };
    public List<String> sourceCurrenciesList = Arrays.asList(sourceCurrencies);

    public List<String> currenciesList = Arrays.asList(tarjetCurrencies);

    private static String streamToString(InputStream inputStream) {
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }

    public String getKrakenCurrenciesURL(String sourceCurrency, String[] tarjetCurrencies) {
        String krakenURL = "https://api.kraken.com/0/public/Ticker?&count=1&pair=";
        List<String> pairsList = new ArrayList<>();
        String pairsStr;

        for (String tarjetcurrency : tarjetCurrencies) {
            if (!sourceCurrency.equals(tarjetcurrency)) {
                pairsList.add(sourceCurrency + tarjetcurrency);

            }
        }

        pairsStr = String.join(",", pairsList);
        return krakenURL + pairsStr;
    }

    public JSONObject processKrakenAnswer(String contents) throws JSONException {
//		<pair_name> = nombre del par
//	    a = array de ofertas(<precio>, <lote completo de volumen>, <lote del volumen>),
//	    b = array de demandas(<precio>, <lote completo de volumen>, <lote del volumen>),
//	    c = array de últimas operaciones(trades) cerradas(<precio>, <lote del volumen>),
//	    v = array de volumen(<hoy>, <últimas 24 horas>),
//	    p = array del precio promedio ponderado por volumen(<hoy>, <últimas 24 horas>),
//	    t = array de número de operaciones(<hoy>, <últimas 24 horas>),
//	    l = array de mínimos(<hoy>, <últimas 24 horas>),
//	    h = array de máximos(<hoy>, <últimas 24 horas>),
//	    o = precio de apertura para hoy

        JSONObject jsonObject = new JSONObject(contents.trim());


        //System.out.println(.toString());

        JSONObject result=(JSONObject) jsonObject.get("result");


        JSONObject answer=new JSONObject();

        for(Iterator iterator = result.keys(); iterator.hasNext();) {
            String key = (String) iterator.next();
            JSONObject currency= (JSONObject)result.get(key);
            String value= ((JSONArray)currency.get("a")).getString(0);
            answer.put(key, value);
        }

        //System.out.println(answer);

        return answer;

    }


    public String jsonGetRequest(String urlQueryString) {
        String json = null;
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/80.0.3987.162 Safari/537.36");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public void main() throws JSONException {

        SubUtils app = new SubUtils();
        String krakerURL;
        String jsonAnswer;
        JSONObject readytoAPI;
        // GETTING URL FOR KRAKEN EUR to every available currency
        System.out.println("THis is the Query for EUR");
        krakerURL = app.getKrakenCurrenciesURL("EUR", app.krakenEURCurrencies);
        jsonAnswer = app.jsonGetRequest(krakerURL);
        readytoAPI =app.processKrakenAnswer(jsonAnswer);
        System.out.println("\tThis is the URL to query kraken");
        System.out.println("\t " + krakerURL);

        System.out.println("\tThis is the JSON returned from kraken");
        System.out.println("\t " + jsonAnswer);

        System.out.println("\tTHis is the JSON for our API");
        System.out.println("\t " + readytoAPI  );



        System.out.println("\n\nTHis is the Query for USD");
        // GETTING URL FOR KRAKEN USD to every available currency
        krakerURL = app.getKrakenCurrenciesURL("USD", app.krakenUSDCurrencies);
        jsonAnswer = app.jsonGetRequest(krakerURL);
        readytoAPI =app.processKrakenAnswer(jsonAnswer);
        System.out.println("\tThis is the URL to query kraken");
        System.out.println("\t " + krakerURL);

        System.out.println("\tThis is the JSON returned from kraken");
        System.out.println("\t " + jsonAnswer);

        System.out.println("\tTHis is the JSON for our API");
        System.out.println("\t " + readytoAPI  );


        System.out.println("\n\nTHis is the Query for XBT");
        // GETTING URL FOR KRAKEN USD to every available currency
        krakerURL = app.getKrakenCurrenciesURL("XBT", app.krakenXBTtoCurrencies);
        System.out.println("\tThis is the URL to query kraken");
        System.out.println("\t " + krakerURL);
        jsonAnswer = app.jsonGetRequest(krakerURL);
        readytoAPI =app.processKrakenAnswer(jsonAnswer);

        System.out.println("\tThis is the JSON returned from kraken");
        System.out.println("\t " + jsonAnswer);

        System.out.println("\tTHis is the JSON for our API");
        System.out.println("\t " + readytoAPI  );
    }
}

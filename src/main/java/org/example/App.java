package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class App 
{

    private static final String FETCH_URL = "https://api.thingspeak.com/channels/1529099/feeds.json?results=2";

    private static void parseJsonData(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray feedsArray = jsonObject.getAsJsonArray("feeds");
        for (int i = 0; i < feedsArray.size(); i++) {
            JsonObject feed = feedsArray.get(i).getAsJsonObject();
            int field1 = feed.get("field1").getAsInt();
            int field2 = feed.get("field2").getAsInt();
            System.out.println("Temperature: " + field1 + ", Humidity: " + field2 );
        }
    }

    public static void fetchData() throws IOException {
        URL url = new URL(FETCH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        parseJsonData(content.toString());
    }

    private static final String API_KEY = "T7H40F0X82VGW7L5";

    public static void sendDataViaUrl(int field1, int field2) throws IOException {
        String urlString = "https://api.thingspeak.com/update?api_key=" + API_KEY
                + "&field1=" + field1 + "&field2=" + field2;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response: " + response.toString());
        } else {
            System.out.println("Failed to send data.");
        }

        connection.disconnect();
    }

    public static void sendDataViaJson(int field1, int field2) throws IOException {
        String urlString = "https://api.thingspeak.com/update?api_key=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{\"field1\":" + field1 + ", \"field2\":" + field2 + "}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Đọc phản hồi từ server
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // In kết quả trả về
            System.out.println("Response: " + response.toString());
        } else {
            System.out.println("Failed to send data.");
        }

        connection.disconnect();
    }



    public static void main( String[] args )
    {
        try {
            fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try{
//            sendDataViaUrl(20, 33);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        try{
//            sendDataViaJson(20, 33);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}

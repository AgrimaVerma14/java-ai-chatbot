package com.chatbot;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiTest {

    public static void main(String[] args) {

        try {

            Client client = new Client();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-3.8-flash",
                            "Say hello to me in one short sentence.",
                            null
                    );

            System.out.println(
                    "Gemini: " + response.text()
            );

        } catch (Exception e) {

            System.out.println(
                    "Gemini connection failed:"
            );

            e.printStackTrace();
        }
    }
}
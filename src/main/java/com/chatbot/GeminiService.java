package com.chatbot;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiService {

    private static final String MODEL = "gemini-3.8-flash";

    private final Client client;

    public GeminiService() {
        client = new Client();
    }

    public String ask(String prompt) {

        try {

            GenerateContentResponse response =
                    client.models.generateContent(
                            MODEL,
                            prompt,
                            null
                    );

            String text = response.text();

            if (text == null || text.isBlank()) {

                return "I didn't receive a response from Gemini.";
            }

            return text;

        } catch (Exception e) {

            return "Gemini error:\n" + e.getMessage();
        }
    }
}
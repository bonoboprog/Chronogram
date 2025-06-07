package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LLMAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LLMAction.class);

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = System.getenv("LLM_API_KEY");

    // Default model if none is provided or input is invalid
    private static final String DEFAULT_MODEL = "deepseek/deepseek-chat-v3-0324:free";

    private String prompt;
    private String llmResponse;
    private String model = DEFAULT_MODEL;

    // Setters for Struts2
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setModel(String model) {
        if (model != null && model.matches("^[a-zA-Z0-9/_\\-:]+$")) {
            this.model = model;
        } else {
            logger.warn("Invalid model received: {} — default will be used.", model);
        }
    }

    // Getters for JSON serialization
    public String getLlmResponse() {
        return llmResponse;
    }

    public String execute() {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            logger.error("LLM API key is missing. Make sure LLM_API_KEY is set in the environment.");
            llmResponse = "{\"error\": \"API key not configured\"}";
            return SUCCESS;
        }

        try {
            String requestBody = buildRequestBody(prompt);

            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            logger.debug("LLM API response code: {}", responseCode);

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // 🎯 Estrai solo il contenuto utile
            JSONObject full = new JSONObject(response.toString());
            JSONArray choices = full.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");

            llmResponse = content;
        } catch (IOException e) {
            logger.error("Error communicating with LLM API", e);
            llmResponse = "{\"error\": \"Communication failed: " + e.getMessage() + "\"}";
        } catch (Exception e) {
            logger.error("Error parsing LLM response", e);
            llmResponse = "{\"error\": \"Failed to parse LLM response\"}";
        }

        return SUCCESS;
    }

    private String buildRequestBody(String prompt) {
        return "{"
                + "\"model\": \"" + model + "\","
                + "\"messages\": ["
                + "  {\"role\": \"system\", \"content\": \"You are a helpful assistant.\"},"
                + "  {\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}"
                + "]"
                + "}";
    }

    private String escapeJson(String text) {
        return text == null ? "" : text.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}

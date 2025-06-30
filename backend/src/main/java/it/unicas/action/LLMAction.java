package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dto.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

public class LLMAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LLMAction.class);

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = System.getenv("LLM_API_KEY");
    private static final String DEFAULT_MODEL = "deepseek/deepseek-chat-v3-0324:free";

    private String prompt;
    private String model = DEFAULT_MODEL;
    private ActivityDTO data;

    public void setPrompt(String prompt) {
        System.out.println("✅ Prompt ricevuto dal frontend: " + prompt);
        this.prompt = prompt;
    }

    public void setModel(String model) {
        if (model != null && model.matches("^[a-zA-Z0-9/_\\-:]+$")) {
            this.model = model;
        } else {
            logger.warn("Invalid model received: {} — default will be used.", model);
        }
    }

    public ActivityDTO getData() {
        return data;
    }

    @Override
    public String execute() {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            logger.error("LLM API key is missing. Make sure LLM_API_KEY is set in the environment.");
            return SUCCESS;
        }

        try {
            String requestBody = buildStructuredRequestBody(prompt);
            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            System.out.println(this.prompt);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            logger.debug("LLM API response code: {}", responseCode);

            JSONArray choices = getChoicesFromResponse(responseCode, conn);
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String rawContent = message.getString("content");
            logger.info("🔥 Raw LLM response:\n{}", rawContent);

            String cleanJson = extractJson(rawContent);
            logger.debug("🧹 Extracted clean JSON:\n{}", cleanJson);

            this.data = parseJsonToDTO(new JSONObject(cleanJson));

        } catch (IOException e) {
            logger.error("Error communicating with LLM API", e);
        } catch (Exception e) {
            logger.error("Error parsing LLM response: {}", e.getMessage());
        }

        return SUCCESS;
    }

    private JSONArray getChoicesFromResponse(int responseCode, HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        return new JSONObject(response.toString()).getJSONArray("choices");
    }

    private String buildStructuredRequestBody(String userPrompt) {
        String systemPrompt = "You are an expert data extractor for a time-tracking app called Chronogram. "
                + "Analyze the user's input and extract the following fields as a flat JSON object: "
                + "name (string), duration (integer in minutes), details (string), enjoyment (integer from -3 to +3), "
                + "category (string), type ('instrumental' or 'final'), recurrence ('R' or 'E'), "
                + "cost (number), and location (string). "
                + "If a value is not present, omit the key. "
                + "Respond with a single valid JSON object only. Do NOT include any explanations, comments, or markdown.";

        return "{"
                + "\"model\": \"" + model + "\","
                + "\"messages\": ["
                + "  {\"role\": \"system\", \"content\": \"" + escapeJson(systemPrompt) + "\"},"
                + "  {\"role\": \"user\", \"content\": \"" + escapeJson(userPrompt) + "\"}"
                + "]"
                + "}";
    }

    private String extractJson(String response) {
        int firstBrace = response.indexOf('{');
        int lastBrace = response.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return response.substring(firstBrace, lastBrace + 1);
        }
        logger.warn("⚠️ Could not find valid JSON in LLM response. Raw:\n{}", response);
        return "{}";
    }

    private String escapeJson(String text) {
        return text == null ? "" : text.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private ActivityDTO parseJsonToDTO(JSONObject json) {
        ActivityDTO dto = new ActivityDTO();

        if (json.has("activityId")) {
            dto.setActivityId(json.optInt("activityId"));
        }

        if (json.has("activityDate")) {
            dto.setActivityDate(Date.valueOf(json.optString("activityDate")));
        }

        if (json.has("duration")) {
            dto.setDurationMins(json.optInt("duration"));
        }

        if (json.has("enjoyment")) {
            dto.setPleasantness(json.optInt("enjoyment"));
        }

        if (json.has("location")) {
            dto.setLocation(json.optString("location"));
        }

        if (json.has("cost")) {
            dto.setCostEuro(String.valueOf(json.optDouble("cost")));
        }

        if (json.has("userId")) {
            dto.setUserId(json.optInt("userId"));
        }

        if (json.has("activityTypeId")) {
            dto.setActivityTypeId(json.optInt("activityTypeId"));
        }

        // Optional: also set activity type info if provided
        if (json.has("activityTypeName")) {
            dto.setActivityTypeName(json.optString("activityTypeName"));
        }

        if (json.has("activityTypeDescription")) {
            dto.setActivityTypeDescription(json.optString("activityTypeDescription"));
        }

        if (json.has("isInstrumental")) {
            dto.setIsInstrumental(json.optBoolean("isInstrumental"));
        }

        if (json.has("isRoutinary")) {
            dto.setIsRoutinary(json.optBoolean("isRoutinary"));
        }

        return dto;
    }

}

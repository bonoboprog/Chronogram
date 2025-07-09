package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.ActivityDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.llmDTO;
import it.unicas.dao.ActivityTypeDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Map;

public class LLMAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(LLMAction.class);

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = System.getenv("LLM_API_KEY");
    private static final String DEFAULT_MODEL = "deepseek/deepseek-chat-v3-0324:free";

    private String prompt;
    private String model = DEFAULT_MODEL;
    private llmDTO data;

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

    public llmDTO getData() {
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
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
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
                + "name (string), durationMins (integer in minutes), details (string), pleasantness (integer from -3 to +3), "
                + "activityTypeName (string), recurrence ('R' or 'E'), costEuro (string), and location (string). "
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

    private llmDTO parseJsonToDTO(JSONObject json) {
        llmDTO dto = new llmDTO();
        dto.setName(json.optString("name", null));
        dto.setDurationMins(json.has("durationMins") ? json.optInt("durationMins") : 0);
        dto.setDetails(json.optString("details", null));
        dto.setPleasantness(json.has("pleasantness") ? json.optInt("pleasantness") : 0);
        dto.setRecurrence(json.optString("recurrence", null));
        dto.setCostEuro(json.optString("costEuro", ""));
        dto.setLocation(json.optString("location", null));

        String categoryName = json.optString("activityTypeName", null);
        Integer id = resolveActivityTypeId(categoryName);
        dto.setActivityTypeId(id);

        return dto;
    }

    /**
     * 🔄 Lookup dinamico nel DB per convertire il nome della categoria nel suo ID.
     */
    private Integer resolveActivityTypeId(String name) {
        if (name == null || name.isEmpty()) return null;

        try (Connection conn = DBUtil.getConnection()) {
            ActivityDAO dao = new ActivityDAO();
            Map<String, Integer> map = dao.getActivityTypeNameToIdMap(conn);
            return map.getOrDefault(name.trim().toLowerCase(), null);
        } catch (Exception e) {
            logger.warn("❌ Failed to resolve activityTypeId for name '{}'", name, e);
            return null;
        }
    }


    private String escapeJson(String text) {
        return text == null ? "" : text.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}

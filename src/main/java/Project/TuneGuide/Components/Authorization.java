package Project.TuneGuide.Components;

import Project.TuneGuide.TuneGuideApplication;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Component
public class Authorization {

    @Value("${tuneguide.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${tuneguide.client-id}")
    private String CLIENT_ID;

    @Value("${tuneguide.client-secret}")
    private String CLIENT_SECRET;

    private String ACCESS_TOKEN = "";
    private String AUTH_CODE = "";
    private boolean isAuthorized = false;


    public String generateAccessCodeURL() {
        return TuneGuideApplication.SERVER_PATH + "/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=code";

    }

    public void setAuthorizationCode(String authorizationCode) {
        this.AUTH_CODE = authorizationCode;
    }

    public void exchangeAuthorizationCodeForToken() throws IOException {
        System.out.println("creating http request to receive token...");

        OkHttpClient client = new OkHttpClient();
        FormBody requestBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", AUTH_CODE)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("redirect_uri", REDIRECT_URI)
                .build();

        Request request = new Request.Builder()
                .url(TuneGuideApplication.SERVER_PATH + "/api/token")
                .post(requestBody)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                extractAccessToken(response.body().string());
                isAuthorized = true;
                redirectToSuccessPage();
                System.out.println("Authentication was successful!");
            } else {
                System.out.println("Error response");
            }
        } catch (IOException e) {
            System.out.println("request error");
        }
    }

    private void redirectToSuccessPage() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        if (response != null) {
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", "/oauth-callback");
        }
    }

    private void extractAccessToken(String responses) {
        JsonObject obj = JsonParser.parseString(responses).getAsJsonObject();
        ACCESS_TOKEN = obj.get("access_token").getAsString();
    }

    public String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}



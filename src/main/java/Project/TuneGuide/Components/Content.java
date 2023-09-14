package Project.TuneGuide.Components;


import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class Content {

    @Value("${tuneguide.spotify.api-base-url}")
    private String SPOTIFY_API_BASE_URL;

    private final Authorization authorization;

    @Autowired
    public Content(Authorization authorization) {
        this.authorization = authorization;
    }

    public String getRequest(String path) {
        String fullUrl = SPOTIFY_API_BASE_URL + path;

        try {
            String accessToken = authorization.getAccessToken();
            if (accessToken == null) {
                return "Please provide access for the application.";
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Error response: " + response.statusCode());
                return "Error response";
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Request error: " + e.getMessage());
            return "Error response";
        }
    }
     public String getNewReleases() throws IOException {
         String response = getRequest("/v1/browse/new-releases");
         JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
         JsonObject albumsObject = jsonResponse.getAsJsonObject("albums");
         JsonArray albumsArray = albumsObject.getAsJsonArray("items");

         List<String> albumsInfo = extractAlbumInfo(albumsArray);

         return String.join("\n", albumsInfo);
     }

     private List<String> extractAlbumInfo (JsonArray albumsArray) {
         List<String> albumsInfo = new ArrayList<>();
         for (JsonElement albumElement : albumsArray) {
             JsonObject album = albumElement.getAsJsonObject();
             String albumName = album.get("name").getAsString();
             String artist = extractArtistName(album);
             String albumUrl = album.getAsJsonObject("external_urls").get("spotify").getAsString();
             albumsInfo.add(formatAlbumInfo(albumName, artist, albumUrl));
         }
         return albumsInfo;
     }

    private String extractArtistName(JsonObject album) {
         JsonArray artistsArray = album.getAsJsonArray("artists");
            if (artistsArray.size() > 0) {
                return artistsArray.get(0).getAsJsonObject().get("name").getAsString();
            }
            return "Unknown Artist";
    }

    private String formatAlbumInfo(String albumName, String artist, String albumUrl) {
         return albumName + "\n" + artist + "\n" + albumUrl + "\n";
    }

    public String getCategories() {
        String response = getRequest("/v1/browse/categories");
        List<String> categories = extractCategoryNames(response);
        return String.join("\n", categories);
    }

    private List<String> extractCategoryNames(String response) {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject categoriesObject = jsonObject.getAsJsonObject("categories");
        JsonArray categoriesArray = categoriesObject.getAsJsonArray("items");

        List<String> categories = new ArrayList<>();
        for (JsonElement item : categoriesArray) {
            String categoryName = extractCategoryName(item);
            categories.add(categoryName);
        }
        return categories;
    }

    private String extractCategoryName(JsonElement item) {
        return item.getAsJsonObject().get("name").getAsString();
    }

    public String getFeaturedPlaylists() {
        String response = getRequest("/v1/browse/featured-playlists");
        List<String> playlists = extractFeaturedPlaylists(response);
        return String.join("\n", playlists);
    }

    private List<String> extractFeaturedPlaylists(String response) {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject playlistsObject = jsonObject.getAsJsonObject("playlists");
        JsonArray playlistsArray = playlistsObject.getAsJsonArray("items");

        List<String> playlists = new ArrayList<>();
        for (JsonElement item : playlistsArray) {
            String playlistInfo = extractPlaylistInfo(item);
            playlists.add(playlistInfo);
        }
        return playlists;
    }

    private String extractPlaylistInfo(JsonElement item) {
        String playlistName = item.getAsJsonObject().get("name").getAsString();
        String playlistUrl = item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString();
        return playlistName + "\n" + playlistUrl + "\n";
    }


    public String getPlaylist(String categoryName) throws IOException {
        try {
            // Validate the category name
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return "Please provide a valid category name.";
            }

            // Encode the category name in the URL
            String encodedCategoryName = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);

            // Make the API request
            String response = getRequest("/v1/browse/categories/" + encodedCategoryName + "/playlists");
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonObject playlistsObject = jsonObject.getAsJsonObject("playlists");
            JsonArray playlistsArray = playlistsObject.getAsJsonArray("items");

            List<String> playlists = new ArrayList<>();
            for (JsonElement item : playlistsArray) {
                String playlistName = item.getAsJsonObject().get("name").getAsString();
                String playlistUrl = item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString();
                playlists.add(playlistName + "\n" + playlistUrl + "\n");
            }
            return String.join("\n", playlists);
        } catch (Exception e) {
            // Handle other unexpected errors
            System.err.println("Unexpected error: " + e.getMessage());
            return "An unexpected error occurred. Please try again later.";
        }
    }

}



package Project.TuneGuide.Components;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Request {
    private final Content content;
    private final Authorization authorization;

    public Request(Content content, Authorization authorization) {
        this.content = content;
        this.authorization = authorization;
    }

    public String getRelease() throws IOException {
        if (authorization.isAuthorized()) {
            return content.getNewReleases();
        } else {
            return "Please provide access for the application.";
        }
    }

    public String getFeatured() throws IOException {
        if (authorization.isAuthorized()) {
            return content.getFeaturedPlaylists();
        } else {
            return "Please provide access for the application.";
        }
    }

    public String getCategories() throws IOException {
        if (authorization.isAuthorized()) {
            return content.getCategories();
        } else {
            return "Please provide access for the application.";
        }
    }

    public String getPlaylists(String category) throws IOException {
        if (authorization.isAuthorized()) {
            return content.getPlaylist(category);
        } else {
            return "Please provide access for the application.";
        }
    }
}

package Project.TuneGuide.Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

@Component
public class StartApp {

    private final Request request;
    private final Authorization authorization;

    @Autowired
    public StartApp(Request request, Authorization authorization) {
        this.request = request;
        this.authorization = authorization;
    }

    public void start() {
        System.out.print("Enter 'guide' to continue to the authentication process: ");

        Scanner scanner = new Scanner(System.in);
        String[] query = scanner.nextLine().split(" ");

        while (!query[0].equals("exit")) {
            switch (query[0]) {
                case ("guide"):
                    String accessCodeURL = authorization.generateAccessCodeURL();
                    System.out.println("Access code URL: " + accessCodeURL);
                    break;
                case ("new"):
                    try {
                        System.out.println(request.getRelease());
                    } catch (IOException e) {
                        System.err.println("Error fetching new releases: " + e.getMessage());
                    }
                    break;
                case ("featured"):
                    try {
                        System.out.println(request.getFeatured());
                    } catch (IOException e) {
                        System.err.println("Error fetching featured playlists: " + e.getMessage());
                    }
                    break;
                case ("categories"):
                    try {
                        System.out.println(request.getCategories());
                    } catch (IOException e) {
                        System.err.println("Error fetching categories: " + e.getMessage());
                    }
                    break;
                case ("playlists"):
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < query.length; i++) {
                        sb.append(query[i]).append(" ");
                    }
                    try {
                        System.out.println(request.getPlaylists(sb.toString().trim()));
                    } catch (IOException e) {
                        System.err.println("Error fetching playlists: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + query[0]);
                    break;
            }
            query = scanner.nextLine().split(" ");
        }
    }
}



package org.example.gmaildemoproject.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@FieldDefaults(level = PRIVATE)
@Slf4j
public class GmailAuthConfig {

        // Path to your credentials.json file
        @Value("${gmail.credentials.path:credentials.json}")
        String credentialsPath;

        // Directory to store user's authorization tokens
        @Value("${gmail.tokens.directory:tokens}")
        String tokensDirectory;

        // Port for the local server receiver during authorization
        @Value("${gmail.auth.port:8887}")
        int authPort;

        // Application name for Gmail API requests
        @Value("${gmail.application.name:Gmail Mini Project}")
        String applicationName;

        private static final GsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();

        /**
         * Provides the Google Credential object for Gmail API authentication.
         * This method handles the OAuth 2.0 flow:
         * 1. Loads client secrets from credentials.json.
         * 2. Sets up a data store to save/load user tokens.
         * 3. Configures OAuth scopes (permissions).
         * 4. Authorizes the application, potentially opening a browser for user consent.
         *
         * @return Credential object for Gmail API.
         * @throws IOException If credentials.json is not found or I/O error occurs.
         * @throws GeneralSecurityException If a security error occurs.
         */
        @Bean
        public Credential gmailCredential() throws IOException, GeneralSecurityException {
            // Load client secrets from credentials.json file
            GoogleClientSecrets clientSecrets;
            try (InputStreamReader reader = new InputStreamReader(
                    Files.newInputStream(Paths.get(credentialsPath)))) {
                clientSecrets = GoogleClientSecrets.load(GSON_FACTORY, reader);
            } catch (FileNotFoundException e) {
                log.error("Credentials file not found at: {}. Please ensure credentials.json is in the project root.", credentialsPath);
                throw e;
            }

            // Set up authorization code flow
            // Define the scopes (permissions) your application needs
            // Using the broad access scope for simplicity, similar to your main project
            List<String> scopes = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
            // For more granular control, use specific scopes like:
            // List.of(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_LABELS);

            GoogleAuthorizationCodeFlow flow = getGoogleAuthorizationCodeFlow(clientSecrets, scopes);

            // Authorize the application
            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(authPort)
                    .build();

            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Removed .getAccountUser() as it's not directly available on Credential object
            log.info("Gmail API authorization successful.");
            return credential;
        }

    /**
     * Provides the NetHttpTransport instance for HTTP requests.
     *
     * @return NetHttpTransport instance.
     * @throws GeneralSecurityException If a security error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Bean
    public NetHttpTransport googleNetHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    /**
     * Provides the GsonFactory instance for JSON parsing.
     *
     * @return GsonFactory instance.
     */
    @Bean
    public GsonFactory gsonFactory() {
        return GSON_FACTORY; // Reusing the static final instance
    }

    /**
     * Configures and provides the Gmail service client.
     * This bean depends on the Credential, NetHttpTransport, and GsonFactory beans.
     *
     * @param credential The authenticated Google Credential.
     * @param httpTransport The HTTP transport for API calls.
     * @param jsonFactory The JSON factory for data parsing.
     * @return An initialized Gmail service client.
     */
    @Bean
    public Gmail gmailServiceClient(Credential credential, NetHttpTransport httpTransport, GsonFactory jsonFactory) {
        log.info("Configuring Gmail service client with application name: {}", applicationName);
        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(GoogleClientSecrets clientSecrets, List<String> scopes) throws IOException, GeneralSecurityException {
        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectory)))
                .setAccessType("offline") // Request a refresh token
                .build();
    }
}

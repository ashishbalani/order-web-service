package com.interview.round1.ap124558.orderwebservice.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Objects;

@TestConfiguration
@TestPropertySource
public class IntegrationConfig {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .defaultHeader("content-type", "application/json");
    }

    @Bean
    public OAuth2AccessToken oAuth2AccessToken(AuthorizedClientServiceOAuth2AuthorizedClientManager  authorizedClientServiceOAuth2AuthorizedClientManager) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("okta")
                .principal("Integration Test")
                .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientServiceOAuth2AuthorizedClientManager.authorize(authorizeRequest);

        // Get the token from the authorized client object
        return Objects.requireNonNull(authorizedClient).getAccessToken();
    }

    @Bean
    ClientRegistration oktaClientRegistration(
            @Value("${spring.security.oauth2.client.provider.okta.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.okta.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.okta.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.okta.scope}") String scope,
            @Value("${spring.security.oauth2.client.registration.okta.authorization-grant-type}") String authorizationGrantType
    ) {
        return ClientRegistration
                .withRegistrationId("okta")
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .scope(scope)
                .issuerUri("https://dev-vq4efl2xqzkt2vp4.us.auth0.com")
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
    }

    // Create the client registration repository
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration oktaClientRegistration) {
        return new InMemoryClientRegistrationRepository(oktaClientRegistration);
    }

    // Create the authorized client service
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    // Create the authorized client manager and service manager using the
    // beans created and configured above
    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

}

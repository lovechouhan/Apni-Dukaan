package com.eCommerce.Ecommerce.Config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.eCommerce.Ecommerce.Entities.Provider;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.helper.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler{


    private  UserRepo userRepo;
    private final Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);


    public OAuthenticationSuccessHandler(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    /// identify the authentication provider (GOOGLE, FACEBOOK, SELF)

    // user ka data authentication  se mil jayega
    var oAuth2Token = (OAuth2AuthenticationToken) authentication;

    // provider  mil jayega
    String provider = oAuth2Token.getAuthorizedClientRegistrationId();

    var oauth2User = (DefaultOAuth2User) oAuth2Token.getPrincipal();


    User user = new User();

    user.setEnabled(true);


    if(provider.equalsIgnoreCase("GOOGLE")) {
 
        user.setProvider(Provider.GOOGLE);
        user.setProviderId(oauth2User.getName());
        user.setName(oauth2User.getAttribute("name"));
        user.setEmail(oauth2User.getAttribute("email"));    
        user.setRole(AppConstants.ROLE_USER);
        user.setProfileImageUrl(oauth2User.getAttribute("picture"));
        
    }

    else {
           logger.warn("Unknown authentication provider: " + provider);
        }


         // check if the user already exists in the database
          User user2 = userRepo.findByEmail(user.getEmail());
        if (user2 == null) {
            // if the user does not exist, save the new user
            userRepo.save(user);
            logger.info("User saved successfully: " + user.getEmail());
        } 



        // Redirect to the home page or any other page after successful authentication

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/main");

        

        
    }

}

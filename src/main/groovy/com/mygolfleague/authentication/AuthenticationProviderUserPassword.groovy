package com.mygolfleague.authentication

import com.mygolfleague.bean.Config
import com.mygolfleague.dto.LoginDto
import com.mygolfleague.repository.UserRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    private final UserRepository userRepository
    private Config config;

    AuthenticationProviderUserPassword(UserRepository userRepository, Config config ){
        this.userRepository = userRepository
        this.config = config
    }
    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.<AuthenticationResponse>create(emitter -> {
            String pwd = authenticationRequest.getSecret() + config.hashSalt;
            String pwd512 = pwd.digest( 'SHA-512' );
            String email = authenticationRequest.getIdentity()
            try{
                LoginDto user= userRepository.getByEmailAddressAndPassword( email, pwd512 )
                if (user) {
                    Collection<String> roles = [ 'ROLE_ADMIN' ]
                    emitter.success(AuthenticationResponse.success( user.id, roles ) );
                } else {
                    emitter.error(AuthenticationResponse.exception());
                }
            }
            catch( e ){
                emitter.error(AuthenticationResponse.exception());
            }


        });
    }
}
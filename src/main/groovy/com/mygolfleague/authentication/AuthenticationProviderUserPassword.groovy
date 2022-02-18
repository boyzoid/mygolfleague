package com.mygolfleague.authentication

import com.mygolfleague.dto.LoginDto
import com.mygolfleague.repository.UserRepository
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

    AuthenticationProviderUserPassword(UserRepository userRepository ){
        this.userRepository = userRepository
    }
    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.<AuthenticationResponse>create(emitter -> {
            String pwd = authenticationRequest.getSecret() + 'g0lfRulze';
            String pwd512 = pwd.digest( 'SHA-512' );
            String email = authenticationRequest.getIdentity()
            LoginDto user= userRepository.getByEmailAddressAndPassword( email, pwd512 )
            if (user) {
                Collection<String> roles = [ 'ROLE_ADMIN' ]
                emitter.success(AuthenticationResponse.success( user.id, roles ) );
            } else {
                emitter.error(AuthenticationResponse.exception());
            }
        });
    }
}
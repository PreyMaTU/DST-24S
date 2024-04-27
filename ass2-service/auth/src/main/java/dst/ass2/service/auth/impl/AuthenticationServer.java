package dst.ass2.service.auth.impl;


import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.IAuthenticationService;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

@ManagedBean
public class AuthenticationServer extends AuthServiceGrpc.AuthServiceImplBase {
    @Inject
    private IAuthenticationService authenticationService;

    @Override
    public void authenticate(AuthenticationRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            final var token = authenticationService.authenticate(request.getEmail(), request.getPassword());
            final var response= AuthenticationResponse.newBuilder()
                    .setToken(token)
                    .setIsAuthenticated( true )
                    .build();

            responseObserver.onNext( response );
            responseObserver.onCompleted();

        } catch (NoSuchUserException e) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription( e.getMessage() ).asException()
            );
        } catch (AuthenticationException e) {
            responseObserver.onError(
                    Status.UNAUTHENTICATED.withDescription( e.getMessage() ).asException()
            );
        }
    }

    @Override
    public void validateToken(TokenValidationRequest request, StreamObserver<TokenValidationResponse> responseObserver) {
        final var isValid = authenticationService.isValid(request.getToken());
        final var response = TokenValidationResponse.newBuilder()
                .setIsValid(isValid)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

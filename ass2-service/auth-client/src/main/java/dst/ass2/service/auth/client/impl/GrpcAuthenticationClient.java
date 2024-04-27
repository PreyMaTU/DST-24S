package dst.ass2.service.auth.client.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.proto.AuthServiceGrpc;
import dst.ass2.service.api.auth.proto.AuthenticationRequest;
import dst.ass2.service.api.auth.proto.TokenValidationRequest;
import dst.ass2.service.auth.client.AuthenticationClientProperties;
import dst.ass2.service.auth.client.IAuthenticationClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class GrpcAuthenticationClient implements IAuthenticationClient {

    private final ManagedChannel channel;
    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public GrpcAuthenticationClient(AuthenticationClientProperties properties) {
        channel= ManagedChannelBuilder
                .forAddress( properties.getHost(), properties.getPort() )
                .usePlaintext()
                .build();

        blockingStub = AuthServiceGrpc.newBlockingStub( channel );
    }

    @Override
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        final var request = AuthenticationRequest.newBuilder()
                .setEmail( email )
                .setPassword( password )
                .build();

        try {
            return blockingStub.authenticate(request).getToken();
        } catch (StatusRuntimeException e) {
            final var status = Status.fromThrowable(e);
            switch( status.getCode() ) {
                case NOT_FOUND: throw new NoSuchUserException( status.getDescription() );
                case UNAUTHENTICATED: throw new AuthenticationException( status.getDescription() );
                default: throw new RuntimeException( e );
            }
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        final var request= TokenValidationRequest.newBuilder()
                .setToken(token)
                .build();

        return blockingStub.validateToken( request ).getIsValid();
    }

    @Override
    public void close() {
        channel.shutdown();
    }
}

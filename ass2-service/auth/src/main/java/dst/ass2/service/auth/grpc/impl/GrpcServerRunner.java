package dst.ass2.service.auth.grpc.impl;

import dst.ass2.service.auth.grpc.GrpcServerProperties;
import dst.ass2.service.auth.grpc.IGrpcServerRunner;
import dst.ass2.service.auth.impl.AuthenticationServer;
import io.grpc.ServerBuilder;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.io.IOException;

@ManagedBean
public class GrpcServerRunner implements IGrpcServerRunner {
    @Inject
    GrpcServerProperties grpcServerProperties;

    @Inject
    AuthenticationServer authenticationServer;

    @Override
    public void run() throws IOException {
        ServerBuilder
            .forPort( grpcServerProperties.getPort() )
            .addService( authenticationServer )
            .build()
            .start();
    }
}

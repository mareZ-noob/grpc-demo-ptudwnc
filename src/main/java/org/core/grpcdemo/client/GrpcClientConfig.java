package org.core.grpcdemo.client;

import org.core.grpcdemo.proto.ProductServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceStub(GrpcChannelFactory channelFactory) {
        return ProductServiceGrpc.newBlockingStub(channelFactory.createChannel("product-service"));
    }
}
package grpc.server.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
// import grpc.server.proto.*;
import grpc.server.proto.*;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    // In-memory storage (simulates database)
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void createUser(CreateUserRequest req, StreamObserver<CreateUserResponse> responseObserver) {

        // TODO: Add status for validation
        // if(req.getName() == null || req.getEmail() == null) {
        //     responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Name and email are required").asException());
        //     return;
        // }
        System.out.println("Creating user: " + req.getName());

        User user = User.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setName(req.getName())
            .setEmail(req.getEmail())
            .build();

        users.put(user.getId(),user);

        CreateUserResponse res= CreateUserResponse.newBuilder()
            .setUser(user)
            .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}

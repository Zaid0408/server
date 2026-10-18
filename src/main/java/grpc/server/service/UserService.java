package grpc.server.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.stub.StreamObserver;
import io.grpc.Status;
import net.devh.boot.grpc.server.service.GrpcService;
import grpc.server.proto.*;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    // In-memory storage (simulates database)
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void createUser(CreateUserRequest req, StreamObserver<CreateUserResponse> responseObserver) {

        if(req.getName() == null || req.getName().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Name is required").asException());
            return;
        }
        if(req.getEmail() == null || req.getEmail().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Email is required").asException());
            return;
        }
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

    @Override
    public void getUser(GetUserRequest req, StreamObserver<GetUserResponse> responseObserver) {
        if(req.getId() == null || req.getId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("User ID is required").asException());
            return;
        }
        String userId = req.getId();
        User user= users.get(userId);

        if(user==null){
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asException());
            return;
        }

        GetUserResponse res= GetUserResponse.newBuilder().setUser(user).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(ListUsersRequest req, StreamObserver<ListUsersResponse> responseObserver) {
        if(users.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("No users found").asException());
            return;
        }
        ListUsersResponse res= ListUsersResponse.newBuilder().addAllUsers(users.values()).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UpdateUserRequest req, StreamObserver<UpdateUserResponse> responseObserver){
        if(req.getId() == null || req.getId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("User ID is required").asException());
            return;
        }
        String userId = req.getId();
        User user= users.get(userId);
        if(user==null){
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asException());
            return;
        }
        if(req.getName() != null && !req.getName().isEmpty()) {
            user.toBuilder().setName(req.getName()).build();
            
        }
        if(req.getEmail() != null && !req.getEmail().isEmpty()) {
            user.toBuilder().setEmail(req.getEmail()).build();
        }
        users.put(userId,user);

        UpdateUserResponse res= UpdateUserResponse.newBuilder().setUser(user).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}

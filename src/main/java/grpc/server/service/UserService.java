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
        System.out.println("CreateUser RPC hit for name: " + req.getName());

        User user = User.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setName(req.getName())
            .setEmail(req.getEmail())
            .build();

        users.put(user.getId(),user);
        System.out.println("CreateUser created user id: " + user.getId());

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
        System.out.println("GetUser RPC hit for user id: " + userId);
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
        System.out.println("ListUsers RPC hit, user count: " + users.size());
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
        System.out.println("UpdateUser RPC hit for user id: " + userId);
        User user= users.get(userId);
        if(user==null){
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asException());
            return;
        }

        User.Builder userBuilder = user.toBuilder();
        if(req.getName() != null && !req.getName().isEmpty()) {
            userBuilder.setName(req.getName());
            
        }
        if(req.getEmail() != null && !req.getEmail().isEmpty()) {
            userBuilder.setEmail(req.getEmail());
        }
        User updatedUser= userBuilder.build();
        users.put(userId,updatedUser);

        UpdateUserResponse res= UpdateUserResponse.newBuilder().setUser(user).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest req, StreamObserver<DeleteUserResponse> responseObserver){
        if(req.getId() == null || req.getId().isEmpty()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("User ID is required").asException());
            return;
        }
        String userId = req.getId();
        System.out.println("DeleteUser RPC hit for user id: " + userId);
        User user= users.get(userId);
        if(user==null){
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asException());
            return;
        }

        boolean success = users.remove(userId) != null;
        DeleteUserResponse res= DeleteUserResponse.newBuilder().setSuccess(success).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}

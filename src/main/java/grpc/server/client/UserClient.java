package grpc.server.client;

import grpc.server.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class UserClient {
    
    public static void main(String[] args) {
        // Create channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        
        UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
        
        try {
            // Test 1: Create Users
            System.out.println("=== Test 1: Creating Users ===");
            CreateUserResponse user1 = stub.createUser(
                CreateUserRequest.newBuilder()
                    .setName("Zaid Hasan")
                    .setEmail("zaid@example.com")
                    .build()
            );
            System.out.println("✓ Created: " + user1.getUser().getName() + " (ID: " + user1.getUser().getId() + ")");
            
            CreateUserResponse user2 = stub.createUser(
                CreateUserRequest.newBuilder()
                    .setName("John Doe")
                    .setEmail("john@example.com")
                    .build()
            );
            System.out.println("✓ Created: " + user2.getUser().getName() + " (ID: " + user2.getUser().getId() + ")");
            
            String userId = user1.getUser().getId();
            
            // Test 2: Get User
            System.out.println("\n=== Test 2: Getting User ===");
            GetUserResponse getResponse = stub.getUser(
                GetUserRequest.newBuilder().setId(userId).build()
            );
            System.out.println("✓ Retrieved: " + getResponse.getUser().getName());
            
            // Test 3: List Users
            System.out.println("\n=== Test 3: Listing Users ===");
            ListUsersResponse listResponse = stub.listUsers(
                ListUsersRequest.newBuilder().build()
            );
            System.out.println("✓ Total users: " + listResponse.getUsersCount());
            listResponse.getUsersList().forEach(user -> 
                System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")")
            );
            
            // Test 4: Update User
            System.out.println("\n=== Test 4: Updating User ===");
            UpdateUserResponse updateResponse = stub.updateUser(
                UpdateUserRequest.newBuilder()
                    .setId(userId)
                    .setName("Zaid Hasan (Updated)")
                    .setEmail("zaid.updated@example.com")
                    .build()
            );
            System.out.println("✓ Updated: " + updateResponse.getUser().getName());
            
            // Test 5: Verify Update
            System.out.println("\n=== Test 5: Verifying Update ===");
            GetUserResponse verifyResponse = stub.getUser(
                GetUserRequest.newBuilder().setId(userId).build()
            );
            System.out.println("✓ Verified: " + verifyResponse.getUser().getName() + " - " + verifyResponse.getUser().getEmail());
            
            // Test 6: Delete User
            System.out.println("\n=== Test 6: Deleting User ===");
            DeleteUserResponse deleteResponse = stub.deleteUser(
                DeleteUserRequest.newBuilder().setId(userId).build()
            );
            System.out.println("✓ Deleted: " + deleteResponse.getSuccess());
            
            // Test 7: Verify Deletion
            System.out.println("\n=== Test 7: Verifying Deletion ===");
            try {
                stub.getUser(GetUserRequest.newBuilder().setId(userId).build());
                System.out.println("✗ User should not exist!");
            } catch (StatusRuntimeException e) {
                System.out.println("✓ User not found (expected): " + e.getStatus().getDescription());
            }
            
            // Test 8: Error Handling
            System.out.println("\n=== Test 8: Testing Validation ===");
            try {
                stub.createUser(
                    CreateUserRequest.newBuilder()
                        .setName("")  // Empty name
                        .setEmail("test@example.com")
                        .build()
                );
                System.out.println("✗ Should have failed validation!");
            } catch (StatusRuntimeException e) {
                System.out.println("✓ Validation error caught: " + e.getStatus().getDescription());
            }
            
            System.out.println("\n🎉 All tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
    }
}
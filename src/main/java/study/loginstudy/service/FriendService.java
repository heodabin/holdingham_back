package study.loginstudy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.entity.FriendRequest;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.FriendRequestRepository;
import study.loginstudy.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendFriendRequest(String senderLoginId, String receiverNickname) {
        Optional<User> senderOpt = userRepository.findByLoginId(senderLoginId);
        Optional<User> receiverOpt = userRepository.findByNickname(receiverNickname);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);
            friendRequest.setStatus(FriendRequest.Status.PENDING);

            friendRequestRepository.save(friendRequest);
        } else {
            System.out.println("Either sender or receiver not found. Sender loginId: " + senderLoginId + ", Receiver nickname: " + receiverNickname);
        }
    }

    public void respondToFriendRequest(String senderNickname, String receiverLoginId, boolean accepted) {
        Optional<User> senderOpt = userRepository.findByNickname(senderNickname);
        Optional<User> receiverOpt = userRepository.findByLoginId(receiverLoginId);

        if (senderOpt.isPresent() && receiverOpt.isPresent()) {
            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            Optional<FriendRequest> requestOpt = friendRequestRepository.findBySenderAndReceiver(sender, receiver);

            requestOpt.ifPresent(friendRequest -> {
                if (accepted) {
                    friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
                } else {
                    friendRequest.setStatus(FriendRequest.Status.REJECTED);
                }

                friendRequestRepository.save(friendRequest);
            });
        } else {
            System.out.println("Either sender or receiver not found. Sender nickname: " + senderNickname + ", Receiver loginId: " + receiverLoginId);
        }
    }

    public List<FriendRequest> getPendingRequests(String receiverLoginId) {
        Optional<User> receiverOpt = userRepository.findByLoginId(receiverLoginId);

        if (receiverOpt.isPresent()) {
            return friendRequestRepository.findByReceiverAndStatus(receiverOpt.get(), FriendRequest.Status.PENDING);
        }

        System.out.println("Receiver not found: " + receiverLoginId);
        return List.of();
    }

    public List<FriendRequest> getFriends(String loginId) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<FriendRequest> sentRequests = friendRequestRepository.findBySenderAndStatus(user, FriendRequest.Status.ACCEPTED);
            List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.Status.ACCEPTED);

            sentRequests.addAll(receivedRequests);
            return sentRequests;
        }

        System.out.println("User not found: " + loginId);
        return List.of();
    }
}

package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.FriendRequest;
import study.loginstudy.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.Status status);
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.Status status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
}

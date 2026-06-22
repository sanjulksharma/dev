package lld.practice.splitswise.group;

import lld.practice.splitswise.enums.StatusType;
import lld.practice.splitswise.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A group is a stable container of users that share expenses
 * (Trip, Home, Couple, Other).
 *
 * <p>The Group enforces membership invariants ({@code addUser}, {@code removeUser})
 * — clients never mutate the member list directly. This keeps the model in a
 * valid state at all times and lets the rest of the system rely on
 * {@code isMember} as the authorization primitive.
 */
public class Group {
    private final String groupId;
    private String name;
    private User owner;
    private final List<User> users;
    private StatusType status;

    public Group(String name, User owner, List<User> users, StatusType status) {
        this.groupId = UUID.randomUUID().toString();
        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>(users);
        this.status = status;
        if (!this.users.contains(owner)) {
            this.users.add(owner);
        }
    }

    public String getGroupId() { return groupId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<User> getUsers() { return new ArrayList<>(users); }
    public StatusType getStatus() { return status; }
    public void setStatus(StatusType status) { this.status = status; }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean isMember(User user) {
        return users.contains(user);
    }

    public boolean isMember(String userId) {
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }
}

package lld.practice.splitswise.services;

import lld.practice.splitswise.enums.StatusType;
import lld.practice.splitswise.exception.EntityNotFoundException;
import lld.practice.splitswise.group.Group;
import lld.practice.splitswise.repository.Repository;
import lld.practice.splitswise.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Group lifecycle service. Membership checks are exposed for callers that
 * need to authorize an action against a group.
 */
public class GroupService {

    private final Repository<String, Group> groupRepository;

    public GroupService(Repository<String, Group> groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group createGroup(String name, User owner, List<User> users, StatusType status) {
        Group group = new Group(name, owner, users, status);
        return groupRepository.save(group.getGroupId(), group);
    }

    public Group getGroup(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group", groupId));
    }

    public void addMember(String groupId, User user) {
        Group group = getGroup(groupId);
        group.addUser(user);
        groupRepository.save(groupId, group);
    }

    public void removeMember(String groupId, User user) {
        Group group = getGroup(groupId);
        group.removeUser(user);
        groupRepository.save(groupId, group);
    }

    public void deleteGroup(String groupId) {
        groupRepository.deleteById(groupId);
    }

    public Collection<Group> getAllGroups() {
        return groupRepository.findAll();
    }
}

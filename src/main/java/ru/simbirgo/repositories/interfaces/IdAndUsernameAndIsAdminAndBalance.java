package ru.simbirgo.repositories.interfaces;

public interface IdAndUsernameAndIsAdminAndBalance {
    Long getId();
    String getUsername();
    Boolean getIsAdmin();
    Double getBalance();
}

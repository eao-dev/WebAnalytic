package com.webAnalytic.Entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class User {

    private long id;

    @NotNull
    @NotEmpty
    @Pattern(regexp="^[a-zA-Z][a-zA-Z0-9-_.]{2,20}$", message = "Логин может содержать только латинские буквы и цифры.")
    private String login;

    @NotNull
    @NotEmpty
    @Pattern(regexp="^[а-яА-ЯёЁa-zA-Z]+$", message = "Имя должно содеражть только латиницу и кирилицу.")
    private String name;

    @NotNull
    @NotEmpty
    @Size(min=6, message = "Длина пароля должна составлять более 6 символов.")
    @Pattern(regexp="(?=^.{5,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$",message = "Пароль должен " +
            " содержать строчные и прописные латинские буквы, цифры, спецсимволы.")
    private String passwordString;


    private byte[] password;

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private boolean isAdmin;

    private long userAdminId;

    /**
     * Set password (password hash)
     * @param password is password hash
     * */
    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void setPasswordString(String passwordString) {
        this.passwordString = passwordString;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public User(long id) {
        this.id = id;
    }

    public User(String login, byte[] password) {
        this.login = login;
        this.password = password;
    }

    public User(long id, String login, byte[] password, String name, Boolean isAdmin, long userAdminId) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
        this.userAdminId = userAdminId;
    }

    public User(String login) {
        this.login = login;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getPasswordString() {
        return passwordString;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdminState(boolean admin) {
        isAdmin = admin;
    }

    public long getUserAdminId() {
        return userAdminId;
    }

    public void setUserAdminId(long userAdminId) {
        this.userAdminId = userAdminId;
    }

}

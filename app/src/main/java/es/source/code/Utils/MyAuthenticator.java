package es.source.code.Utils;

import javax.mail.Authenticator;

public class MyAuthenticator extends Authenticator {
    String username = null;
    String password = null;

    public MyAuthenticator() {}

    public MyAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(username, password);
    }
}

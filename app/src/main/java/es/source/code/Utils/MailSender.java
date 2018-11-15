package es.source.code.Utils;

import java.util.Properties;

public class MailSender {
    private String mailServerHost;  //发送邮件的服务器IP
    private String mailServerPort; //发送邮件的服务器端口
    private String username;    //邮件服务器用户名
    private String password;    //邮件服务器密码
    private String fromAddress; //发送者地址
    private String toAddress;   //接收者地址
    private boolean validate = false;   //是否需要身份认证
    private String subject; //邮件主题
    private String content; //邮件内容
    private String[] attachFileNames;   //附件

    public MailSender(String mailServerHost, String mailServerPort, String username, String password,
                      String fromAddress, String toAddress, boolean validate,
                      String subject, String content, String[] attachFileNames) {
        this.mailServerHost = mailServerHost;
        this.mailServerPort = mailServerPort;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.validate = validate;
        this.subject = subject;
        this.content = content;
        this.attachFileNames = attachFileNames;
    }

    /**
     * 获取邮件相关配置
     * @return Properties
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }
    public String getMailServerHost() {
        return mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public boolean isValidate() {
        return validate;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }
}

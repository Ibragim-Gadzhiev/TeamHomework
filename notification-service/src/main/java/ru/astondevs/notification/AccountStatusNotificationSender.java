package ru.astondevs.notification;

public class AccountStatusNotificationSender implements NotificationSendler{

    public static void createNotifyAboutCreatingAccount(String email){
        System.out.println("account creation message was sent to the address " + email);
    }
    public static void createNotifyAboutDeletingAccount(String email){
        System.out.println("the account deletion message was sent to the address " + email);
    }
}

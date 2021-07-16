package com.julianomengue.services;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.julianomengue.classes.User;

public class JavaEmail {

	private static String email = "audittool2020@gmail.com";
	private static String password = "vchjvqkbbcdynval";

	public static String getJavaMailSender(User user) {
		String passwordUser = "";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
		try {
			String subject = "User Password";
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("NOREPLY"));
			message.setSubject(subject);
			passwordUser = getSaltString();
			String x = "<p><strong>Password: " + passwordUser + "</p>";
			message.setContent(x, "text/html; charset=utf-8");
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return passwordUser;
	}

	public static String emailRepeatPassword(User user) {
		String passwordUser = "";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
		try {
			String subject = "User Password";
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("NOREPLY"));
			message.setSubject(subject);
			passwordUser = getSaltString();
			String x = "<p><strong>Password: " + passwordUser + "</p>";
			message.setContent(x, "text/html; charset=utf-8");
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return passwordUser;
	}

	public static boolean getNewPassword(User user) {
		boolean ok = false;
		String passwordUser = "";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});
		try {
			String subject = "User Password";
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("NOREPLY"));
			message.setSubject(subject);
			passwordUser = user.getPassword();
			String x = "<p><strong>Password: " + passwordUser + "</p>";
			message.setContent(x, "text/html; charset=utf-8");
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			Transport.send(message);
			ok = true;
		} catch (MessagingException e) {
			ok = false;
			throw new RuntimeException(e);
		}
		return ok;
	}

	public static String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}

}

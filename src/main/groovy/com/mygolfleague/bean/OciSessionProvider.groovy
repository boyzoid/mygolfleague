package com.mygolfleague.bean

import io.micronaut.context.annotation.Property
import io.micronaut.core.annotation.NonNull
import io.micronaut.email.javamail.sender.MailPropertiesProvider
import io.micronaut.email.javamail.sender.SessionProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.mail.Session
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.inject.Singleton

@Singleton
public class OciSessionProvider implements SessionProvider {
	private static final Logger LOG = LoggerFactory.getLogger(OciSessionProvider.class);
	private final Properties properties;
	private final String user;
	private final String password;
	
	public OciSessionProvider(
			MailPropertiesProvider properties,
			@Property(name = "mygolfleague.smtp.user") String user,
			@Property(name = "mygolfleague.smtp.password") String password
	) {
		this.properties = properties.mailProperties();
		this.user = user;
		this.password = password;
	}
	@Override
	@NonNull
	public Session session() {
		return Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
	}
	
	
}

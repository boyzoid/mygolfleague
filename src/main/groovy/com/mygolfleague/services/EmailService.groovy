package com.mygolfleague.services

import io.micronaut.core.util.CollectionUtils
import io.micronaut.email.BodyType
import io.micronaut.email.Email
import io.micronaut.email.EmailSender
import io.micronaut.email.template.TemplateBody
import io.micronaut.views.ModelAndView

import javax.inject.Singleton

@Singleton
public class EmailService {
	private final EmailSender emailSender;
	
	EmailService(EmailSender emailSender) {
		this.emailSender = emailSender;
	}
	public sendTestEmail(){
		Email.Builder emailBuilder = Email.builder()
				.to("scott.stroz@gmail.com")
				.subject("Basic Micronaut Email Test: " + new Date().toString() )
				.body("This is an email");
		emailSender.send(emailBuilder);
	}
	
	public sendTestTemplateEmail(){
		Map model = CollectionUtils.mapOf( "name", "Testy Mctestface", "moo", "blarg")
		Email.Builder emailBuilder = Email.builder()
				.to("scott.stroz@gmail.com")
				.subject("Template Micronaut Email Test: " + new Date().toString() )
				.body( new TemplateBody<>( BodyType.HTML, new ModelAndView<>("testEmail", model ) ) );
		emailSender.send(emailBuilder);
	}
	
}

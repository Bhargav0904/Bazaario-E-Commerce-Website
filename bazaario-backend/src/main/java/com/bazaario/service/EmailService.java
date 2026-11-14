package com.bazaario.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.util.HtmlUtils;
 
import com.bazaario.entity.Order;
import com.bazaario.entity.OrderItem;
import com.bazaario.entity.Payment;
import com.bazaario.entity.PaymentMethod;
import com.bazaario.entity.PaymentStatus;
import com.bazaario.entity.User;
 
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.mail.internet.MimeMessage;
 
/**
 * Sends the HTML emails: welcome, login alert, order placed, payment receipt.
 *
 * Design notes (good points to mention in a viva):
 *  - Emails are sent on a background thread, so the API response is not slowed down
 *    by the SMTP server, and a mail failure never breaks register/login/order/payment.
 *  - The email text is built on the calling thread (inside the transaction, so lazy
 *    data like order items can still be read) and is only SENT after the transaction
 *    commits - so nobody gets an "order placed" mail for an order that was rolled back.
 *  - If email is switched off, or the SMTP username is still the placeholder, nothing
 *    is sent and the app keeps working normally.
 */
@Service
public class EmailService {
 
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
	private static final String PLACEHOLDER_USER = "your-email@gmail.com";
 
	private static final String LAYOUT = """
			<!DOCTYPE html>
			<html>
			<body style="margin:0;padding:0;background:#f4f1ec;font-family:Arial,Helvetica,sans-serif;color:#2b2b2b;">
			<table width="100%" cellpadding="0" cellspacing="0" style="background:#f4f1ec;padding:24px 0;">
			<tr><td align="center">
			<table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;">
			<tr><td style="background:#214a40;color:#ffffff;padding:20px 28px;font-size:22px;font-weight:bold;">Bazaario</td></tr>
			<tr><td style="padding:28px;font-size:15px;line-height:1.6;">
			<h2 style="margin:0 0 16px;color:#214a40;">{{TITLE}}</h2>
			{{CONTENT}}
			</td></tr>
			<tr><td style="background:#faf8f5;padding:16px 28px;font-size:12px;color:#777777;">
			This is an automated message from Bazaario. Please do not reply to this email.
			</td></tr>
			</table>
			</td></tr>
			</table>
			</body>
			</html>
			""";
 
	private final ObjectProvider<JavaMailSender> mailSenderProvider;
	private final boolean enabled;
	private final boolean loginAlertEnabled;
	private final String smtpUser;
	private final String fromAddress;
	private final String fromName;
	private final String frontendUrl;
	private final ExecutorService executor = Executors.newFixedThreadPool(2);
 
	public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
			@Value("${app.mail.enabled:true}") boolean enabled,
			@Value("${app.mail.login-alert-enabled:true}") boolean loginAlertEnabled,
			@Value("${spring.mail.username:}") String smtpUser,
			@Value("${app.mail.from:}") String fromAddress,
			@Value("${app.mail.from-name:Bazaario}") String fromName,
			@Value("${app.mail.frontend-url:http://localhost:4200}") String frontendUrl) {
		this.mailSenderProvider = mailSenderProvider;
		this.enabled = enabled;
		this.loginAlertEnabled = loginAlertEnabled;
		this.smtpUser = smtpUser;
		this.fromAddress = (fromAddress == null || fromAddress.isBlank()) ? smtpUser : fromAddress;
		this.fromName = fromName;
		this.frontendUrl = frontendUrl;
	}
 
	@PostConstruct
	void logStatus() {
		if (ready()) {
			log.info("Email notifications are ON (sending as {})", fromAddress);
		} else {
			log.warn("Email notifications are OFF - set spring.mail.username / spring.mail.password "
					+ "(Gmail App Password) in application.properties to turn them on");
		}
	}
 
	@PreDestroy
	void shutdown() {
		executor.shutdown();
	}
 
	private boolean ready() {
		return enabled && smtpUser != null && !smtpUser.isBlank() && !PLACEHOLDER_USER.equals(smtpUser);
	}
 
	// ------------------------------------------------------------------ public API
 
	/** After a new account is registered. */
	public void sendWelcomeEmail(User user) {
		if (!ready()) {
			return;
		}
		String content = "<p>Hi " + esc(user.getFullName()) + ",</p>"
				+ "<p>Welcome to Bazaario! Your account has been created with <b>" + esc(user.getEmail())
				+ "</b>.</p>"
				+ "<p>You can now browse products, add them to your cart and place orders.</p>"
				+ button("Start shopping", frontendUrl)
				+ "<p style=\"color:#777777;font-size:13px;\">If you did not create this account, please ignore this email.</p>";
		queue(user.getEmail(), "Welcome to Bazaario!", layout("Welcome to Bazaario", content));
	}
 
	/** After every successful login. */
	public void sendLoginAlert(User user) {
		if (!ready() || !loginAlertEnabled) {
			return;
		}
		String content = "<p>Hi " + esc(user.getFullName()) + ",</p>"
				+ "<p>You just signed in to your Bazaario account on <b>"
				+ LocalDateTime.now().format(DATE_FORMAT) + "</b>.</p>"
				+ "<p>If this was you, no action is needed. If it was not you, please change your password "
				+ "immediately from your profile page.</p>";
		queue(user.getEmail(), "New login to your Bazaario account", layout("New sign-in detected", content));
	}
 
	/** After an order is placed from the cart (payment still to be done). */
	public void sendOrderPlacedEmail(Order order) {
		if (!ready()) {
			return;
		}
		User user = order.getUser();
		String content = "<p>Hi " + esc(user.getFullName()) + ",</p>"
				+ "<p>Thank you for your order! We have received it and it is waiting for payment.</p>"
				+ details(
						row("Order number", "#" + order.getOrderId()),
						row("Order date", order.getOrderDate().format(DATE_FORMAT)),
						row("Status", order.getStatus().name()),
						row("Shipping address", multiline(order.getShippingAddress())))
				+ itemsTable(order)
				+ "<p>Please complete the payment to confirm your order.</p>"
				+ button("View my orders", frontendUrl + "/orders");
		queue(user.getEmail(), "Order #" + order.getOrderId() + " placed successfully",
				layout("Order placed", content));
	}
 
	/** After a payment is recorded (also confirms the order). */
	public void sendPaymentReceipt(Payment payment) {
		if (!ready()) {
			return;
		}
		Order order = payment.getOrder();
		User user = order.getUser();
		boolean paid = payment.getPaymentStatus() == PaymentStatus.SUCCESS;
 
		String message = paid
				? "<p>We have received your payment. <b>Your order is confirmed</b> and will be processed soon.</p>"
				: "<p><b>Your order is confirmed.</b> Please pay " + money(payment.getAmount())
						+ " in cash when the order is delivered.</p>";
 
		String transaction = (payment.getTransactionId() == null || payment.getTransactionId().isBlank())
				? "-" : esc(payment.getTransactionId());
 
		String content = "<p>Hi " + esc(user.getFullName()) + ",</p>"
				+ message
				+ details(
						row("Receipt number", "PAY-" + payment.getPaymentId()),
						row("Order number", "#" + order.getOrderId()),
						row("Payment method", methodLabel(payment.getPaymentMethod())),
						row("Payment status", payment.getPaymentStatus().name()),
						row("Transaction ID", transaction),
						row("Amount", "<b>" + money(payment.getAmount()) + "</b>"),
						row("Payment date", payment.getPaymentDate().format(DATE_FORMAT)))
				+ itemsTable(order)
				+ button("View my orders", frontendUrl + "/orders");
		queue(user.getEmail(), "Payment receipt for Order #" + order.getOrderId(),
				layout(paid ? "Payment receipt" : "Order confirmed", content));
	}
 
	// ------------------------------------------------------------------ sending
 
	private void queue(String to, String subject, String html) {
		afterCommit(() -> {
			try {
				executor.submit(() -> send(to, subject, html));
			} catch (Exception e) {
				log.error("Could not queue email '{}' to {}: {}", subject, to, e.getMessage());
			}
		});
	}
 
	/** Runs the task once the surrounding transaction has committed (or right away if none). */
	private void afterCommit(Runnable task) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					task.run();
				}
			});
		} else {
			task.run();
		}
	}
 
	private void send(String to, String subject, String html) {
		try {
			JavaMailSender sender = mailSenderProvider.getIfAvailable();
			if (sender == null) {
				log.warn("No mail sender configured - skipped email '{}' to {}", subject, to);
				return;
			}
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(fromAddress, fromName);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(html, true);
			sender.send(message);
			log.info("Email '{}' sent to {}", subject, to);
		} catch (Exception e) {
			// Never let a mail problem break the app - just log it.
			log.error("Could not send email '{}' to {}: {}", subject, to, e.getMessage());
		}
	}
 
	// ------------------------------------------------------------------ HTML helpers
 
	private String layout(String title, String content) {
		return LAYOUT.replace("{{TITLE}}", esc(title)).replace("{{CONTENT}}", content);
	}
 
	private String button(String text, String url) {
		return "<p style=\"margin:24px 0;\"><a href=\"" + esc(url) + "\" style=\"background:#e8a33d;color:#1d1d1d;"
				+ "text-decoration:none;padding:12px 22px;border-radius:6px;font-weight:bold;display:inline-block;\">"
				+ esc(text) + "</a></p>";
	}
 
	private String row(String label, String valueHtml) {
		return "<tr><td style=\"padding:6px 0;color:#777777;width:40%;vertical-align:top;\">" + esc(label)
				+ "</td><td style=\"padding:6px 0;\">" + valueHtml + "</td></tr>";
	}
 
	private String details(String... rows) {
		return "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:12px 0 20px;\">"
				+ String.join("", rows) + "</table>";
	}
 
	private String itemsTable(Order order) {
		String cell = "padding:8px;border-bottom:1px solid #eeeeee;";
		StringBuilder rows = new StringBuilder();
		for (OrderItem item : order.getItems()) {
			rows.append("<tr><td style=\"").append(cell).append("\">")
					.append(esc(item.getProduct().getProductName()))
					.append("</td><td style=\"").append(cell).append("text-align:center;\">")
					.append(item.getQuantity())
					.append("</td><td style=\"").append(cell).append("text-align:right;\">")
					.append(money(item.getPrice()))
					.append("</td><td style=\"").append(cell).append("text-align:right;\">")
					.append(money(item.getSubtotal()))
					.append("</td></tr>");
		}
		String head = "padding:8px;background:#faf8f5;font-size:13px;color:#555555;";
		return "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:8px 0 20px;font-size:14px;\">"
				+ "<tr><td style=\"" + head + "\">Item</td>"
				+ "<td style=\"" + head + "text-align:center;\">Qty</td>"
				+ "<td style=\"" + head + "text-align:right;\">Price</td>"
				+ "<td style=\"" + head + "text-align:right;\">Total</td></tr>"
				+ rows
				+ "<tr><td colspan=\"3\" style=\"padding:10px 8px;text-align:right;font-weight:bold;\">Order total</td>"
				+ "<td style=\"padding:10px 8px;text-align:right;font-weight:bold;\">" + money(order.getTotalAmount())
				+ "</td></tr></table>";
	}
 
	private String methodLabel(PaymentMethod method) {
		return switch (method) {
			case UPI -> "UPI";
			case CARD -> "Credit / Debit Card";
			case NET_BANKING -> "Net Banking";
			case COD -> "Cash on Delivery";
		};
	}
 
	private String money(BigDecimal amount) {
		BigDecimal value = amount == null ? BigDecimal.ZERO : amount;
		return "&#8377;" + String.format(Locale.ENGLISH, "%,.2f", value);
	}
 
	private String multiline(String text) {
		return esc(text).replace("\r\n", "<br>").replace("\n", "<br>");
	}
 
	/** Escapes user-provided text so names/addresses can't break or inject into the HTML. */
	private String esc(String text) {
		return text == null ? "" : HtmlUtils.htmlEscape(text);
	}
}
package co.uniquindio.crud.service.emailService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.jboss.logging.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class EmailService {

    @ConfigProperty(name = "mail.smtp.user")
    String remitente;

    @ConfigProperty(name = "mail.smtp.password")
    String claveApp;

    private Session session;

    private static final Logger LOGGER = Logger.getLogger(EmailService.class);


    @PostConstruct
    void init() {
        Properties props = new Properties();
        // STARTTLS sobre el puerto 587
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // **<— AÑADE ESTA LÍNEA: hostname válido para HELO/EHLO**
        props.put("mail.smtp.localhost", "localhost");

        // timeouts y debug
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.debug", "true");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, claveApp);
            }
        });
    }

    public void enviarCorreo(String destinatario, String mensaje) {
        try {
            String htmlBody = String.format("""
                <!DOCTYPE html>
                <html lang="es">
                  <head><meta charset="UTF-8"/><title>Notificación Académica</title></head>
                  <body style="margin:0;padding:0;background:#f2f2f2;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                      <tr><td align="center" style="padding:40px 0">
                        <table width="600" style="background:#fff;border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,0.1);">
                          <tr>
                            <td align="center" style="background:#006400;padding:30px 20px;color:#fff;font-family:Arial,sans-serif;">
                              <h1 style="margin:0">🎓 Universidad del Quindío</h1>
                              <p style="margin:5px 0 0">Plataforma Académica de Calificación</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:30px 40px;font-family:Arial,sans-serif;color:#333;">
                              <h2 style="color:#006400">¡Hola!</h2>
                              <p>Te informamos que: <strong>%s</strong></p>
                              <p>Este es un mensaje automático. Si tienes dudas, contacta al docente o al soporte técnico.</p>
                              <p style="text-align:center;margin-top:30px;">
                                <a href="https://mapsphere.app"
                                   style="background:#006400;color:#fff;padding:12px 25px;text-decoration:none;
                                          border-radius:5px;font-weight:bold;display:inline-block;">
                                  Ingresar a la Plataforma
                                </a>
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f0f0f0;text-align:center;padding:20px;
                                       font-family:Arial,sans-serif;font-size:12px;color:#888;">
                              © 2025 Universidad del Quindío<br/>mapsphereapp@gmail.com
                            </td>
                          </tr>
                        </table>
                      </td></tr>
                    </table>
                  </body>
                </html>
            """, mensaje);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(remitente, "Universidad del Quindío"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject("📢 Notificación de la Universidad del Quindío");
            msg.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(msg);
            LOGGER.info("✅ Correo enviado a " + destinatario);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("❌ Error al enviar correo: " + e.getMessage());
        }
    }
}

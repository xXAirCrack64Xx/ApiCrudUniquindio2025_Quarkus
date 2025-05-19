package co.uniquindio.crud.service.emailService;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    /**
     * Envía un correo HTML con un mensaje dinámico para la Universidad del Quindío.
     *
     * @param destinatario la dirección de correo del receptor
     * @param mensaje      el texto de notificación ("su programa ha sido comentado", etc.)
     */
    public void enviarCorreo(String destinatario, String mensaje) {
        // Plantilla HTML con marcador %s para el mensaje y %% para literales de porcentaje
        String htmlBody = String.format("""
                <!DOCTYPE html>
                <html lang=\"es\">        
                <head>
                  <meta charset=\"UTF-8\" />
                  <title>Notificación Académica</title>
                </head>
                <body style=\"margin: 0; padding: 0; background-color: #f2f2f2;\">        
                  <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%%\">  
                    <tr>
                      <td align=\"center\" style=\"padding: 40px 0;\">
                        <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); overflow: hidden;\">
                          <tr>
                            <td align=\"center\" style=\"background-color: #006400; padding: 30px 20px;\">
                              <h1 style=\"color: #ffffff; font-family: Arial, sans-serif; margin: 0;\">🎓 Universidad del Quindío</h1>
                              <p style=\"color: #ffffff; font-family: Arial, sans-serif; margin-top: 10px;\">Plataforma Académica de Calificación</p>
                            </td>
                          </tr>
                          <tr>
                            <td style=\"padding: 30px 40px; font-family: Arial, sans-serif; color: #333;\">
                              <h2 style=\"color: #006400;\">¡Hola!</h2>
                              <p style=\"font-size: 16px; line-height: 1.5;\">
                                Te informamos que: <strong>%s</strong>
                              </p>
                              <p style=\"font-size: 16px; line-height: 1.5;\">
                                Este es un mensaje automático generado por nuestra plataforma académica. Si tienes alguna inquietud, por favor comunícate con el docente encargado o el soporte técnico.
                              </p>
                              <div style=\"text-align: center; margin-top: 30px;\">
                                <a href=\"https://mapsphere.app\" target=\"_blank\" style=\"background-color: #006400; color: #fff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;\">
                                  Ingresar a la Plataforma
                                </a>
                              </div>
                            </td>
                          </tr>
                          <tr>
                            <td style=\"background-color: #f0f0f0; text-align: center; padding: 20px; font-family: Arial, sans-serif; font-size: 12px; color: #888;\">
                              © 2025 Universidad del Quindío - Todos los derechos reservados<br/>
                              mapsphereapp@gmail.com
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>               
                </body>
                </html>
                """, mensaje);

        // Envío del correo
        mailer.send(Mail.withHtml(
                destinatario,
                "📢 Notificación de la Universidad del Quindío",
                htmlBody
        ));
    }
}

package com.sidework.common.mail.component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailHelper {
    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String id;

    @Async("emailTaskExecutor")
    public void processEmailCodeSend(String email) {
        String validationCode = createKey();
        redisTemplate.opsForValue().set(
                "email:verify:" + email,
                validationCode,
                Duration.ofMinutes(5)
        );
        try {
            MimeMessage message = createValidationMessage(email, validationCode);
            sendMessage(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String EMAIL_PREFIX = "email:verify:";

    public boolean processVerify(String email, String inputCode) {
        boolean res = verify(email, inputCode);
        if(res) {
            redisTemplate.delete(EMAIL_PREFIX + email);
        }
        return res;
    }

    public boolean verify(String email, String inputCode) {
        String code = redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
        return Objects.equals(code, inputCode);
    }

    public MimeMessage createValidationMessage(String to, String code) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("[GROWP] 회원가입 인증 코드"); //메일 제목

        String msg = "<div style=\"width: 100%; font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; text-align: center;\">"
                + "<h1 style=\"color: #333; font-size: 24px;\">이메일 주소 확인</h1>"
                + "<p style=\"font-size: 16px; color: #555;\">아래 인증 코드를 회원가입 화면에서 입력해주세요.</p>"
                + "<div style=\"margin: 20px 0; padding: 15px; background-color: #F4F4F4; border-radius: 10px; display: inline-block;\">"
                + "<span style=\"font-size: 32px; font-weight: bold; color: #333; letter-spacing: 4px;\">" + code + "</span>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #888;\">이 코드는 5분 동안 유효합니다.</p>"
                + "</div>"
                + "</div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"GROWP")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    public void sendMessage(MimeMessage message) {
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new IllegalStateException("이메일 전송에 실패했습니다.", e);
        }
    }

    private String createKey() {
        StringBuilder key = new StringBuilder();
        SecureRandom rnd = new SecureRandom();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }
}

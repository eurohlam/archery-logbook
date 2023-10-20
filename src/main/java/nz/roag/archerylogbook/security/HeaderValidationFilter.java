package nz.roag.archerylogbook.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.roag.archerylogbook.db.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;


@Component
@Order(2)
public class HeaderValidationFilter implements Filter {

    @Autowired
    private SubscriberRepository subscriberRepository;

    private final Logger logger = LoggerFactory.getLogger(HeaderValidationFilter.class);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        var key = req.getHeader("key");
        var nonce = req.getHeader("nonce");
        var signature = req.getHeader("signature");
        var timestamp = req.getHeader("timestamp");

        if (!StringUtils.hasText(key)) {
            logger.warn("Mandatory HTTP header is missed: key");
            res.sendError(HttpStatus.BAD_REQUEST.value(), "Mandatory HTTP header is missed: key");
        } else if (!StringUtils.hasText(nonce)) {
            logger.warn("Mandatory HTTP header is missed: nonce");
            res.sendError(HttpStatus.BAD_REQUEST.value(), "Mandatory HTTP header is missed: nonce");
        } else if (!StringUtils.hasText(timestamp)) {
            logger.warn("Mandatory HTTP header is missed: timestamp");
            res.sendError(HttpStatus.BAD_REQUEST.value(), "Mandatory HTTP header is missed: timestamp");
        } else if (!StringUtils.hasText(signature)) {
            logger.warn("Mandatory HTTP header is missed: signature");
            res.sendError(HttpStatus.BAD_REQUEST.value(), "Mandatory HTTP header is missed: signature");
        } else {
            var secret = subscriberRepository.findById(key);
            if (secret.isEmpty()) {
                logger.warn("Invalid key: {}", key);
                res.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid key");
            } else {
                var hmac = new HmacUtil();
                var path = req.getRequestURI();
                if (StringUtils.hasText(req.getQueryString())) {
                    path = path + "?" + req.getQueryString();
                }
                var signatureVerification = hmac.calculateHMAC(secret.get().getSecretKey(), path + key + nonce + timestamp);

                logger.debug("Verifying incoming signature: {} against server signature: {}", signature, signatureVerification);
                if (signatureVerification.equals(signature)) {
                    filterChain.doFilter(request, response);
                } else {
                    logger.warn("Invalid signature: {}", signature);
                    res.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid signature");
                }
            }
        }
    }
}

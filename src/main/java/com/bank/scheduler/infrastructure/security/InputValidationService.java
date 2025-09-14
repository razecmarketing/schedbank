package com.bank.scheduler.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputValidationService {

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{4}-\\d{6}-\\d{1}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d{1,10}(\\.\\d{1,2})?$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    public void validateTransferRequest(String sourceAccount, String targetAccount, 
                                      String amount, String scheduledDate, String description) {
        validateAccountNumber(sourceAccount, "sourceAccount");
        validateAccountNumber(targetAccount, "targetAccount");
        validateAmount(amount);
        validateScheduledDate(scheduledDate);
        validateDescription(description);
        
        if (sourceAccount.equals(targetAccount)) {
            throw new SecurityException("Source and target accounts cannot be the same");
        }
    }

    private void validateAccountNumber(String account, String fieldName) {
        if (account == null || account.trim().isEmpty()) {
            throw new SecurityException(fieldName + " is required");
        }
        
        if (!ACCOUNT_NUMBER_PATTERN.matcher(account.trim()).matches()) {
            throw new SecurityException(fieldName + " format is invalid");
        }
    }

    private void validateAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new SecurityException("Amount is required");
        }
        
        if (!AMOUNT_PATTERN.matcher(amount.trim()).matches()) {
            throw new SecurityException("Amount format is invalid");
        }
        
        double value = Double.parseDouble(amount.trim());
        if (value <= 0) {
            throw new SecurityException("Amount must be greater than zero");
        }
        
        if (value > 1000000) {
            throw new SecurityException("Amount exceeds maximum limit");
        }
    }

    private void validateScheduledDate(String date) {
        if (date != null && !date.trim().isEmpty()) {
            if (!DATE_PATTERN.matcher(date.trim()).matches()) {
                throw new SecurityException("Scheduled date format is invalid");
            }
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new SecurityException("Description exceeds maximum length");
        }
        
        if (description != null && containsSuspiciousContent(description)) {
            throw new SecurityException("Description contains invalid content");
        }
    }

    private boolean containsSuspiciousContent(String content) {
        String lowerContent = content.toLowerCase();
        return lowerContent.contains("<script>") || 
               lowerContent.contains("javascript:") || 
               lowerContent.contains("vbscript:") ||
               lowerContent.contains("data:text/html");
    }

    public String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("[<>\"'&]", "")
                   .replaceAll("\\p{Cntrl}", "");
    }

    public void validateRequestHeaders(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String contentType = request.getHeader("Content-Type");
        
        if (userAgent != null && userAgent.length() > 1000) {
            throw new SecurityException("Invalid User-Agent header");
        }
        
        if (contentType != null && !contentType.startsWith("application/json")) {
            throw new SecurityException("Invalid Content-Type header");
        }
    }
}
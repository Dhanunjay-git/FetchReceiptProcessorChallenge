package receiptprocessor.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Receipt not found")
public class ReceiptNotFoundException extends RuntimeException {}

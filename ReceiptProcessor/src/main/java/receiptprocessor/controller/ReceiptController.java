package receiptprocessor.controller;


import receiptprocessor.exception.ReceiptNotFoundException;
import receiptprocessor.model.Item;
import receiptprocessor.model.Receipt;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    private final Map<String, Integer> receipts = new HashMap<>();

    @PostMapping("/process")
    public Map<String, String> processReceipt(@RequestBody Receipt receipt) {
        String receiptId = UUID.randomUUID().toString();
        int points = calculatePoints(receipt);
        receipts.put(receiptId, points);
        return Collections.singletonMap("id", receiptId);
    }

    @GetMapping("/{id}/points")
    public Map<String, Integer> getPoints(@PathVariable String id) {
        Integer points = receipts.get(id);
        if (points == null) {
            throw new ReceiptNotFoundException();
        }
        return Collections.singletonMap("points", points);
    }

    private int calculatePoints(Receipt receipt) {
        int points = 0;

        Pattern alphanumericPattern = Pattern.compile("[a-zA-Z0-9]");
        Matcher matcher = alphanumericPattern.matcher(receipt.getRetailer());
        while (matcher.find()) {
            points++;
        }

        double total = Double.parseDouble(receipt.getTotal());
        if (total == Math.floor(total)) {
            points += 50;
        }

        if (total % 0.25 == 0) {
            points += 25;
        }

        points += (receipt.getItems().size() / 2) * 5;

        for (Item item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            double price = Double.parseDouble(item.getPrice());
            if (description.length() % 3 == 0) {
                points += Math.ceil(price * 0.2);
            }
        }

        LocalDate purchaseDate = LocalDate.parse(receipt.getPurchaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (purchaseDate.getDayOfMonth() % 2 != 0) {
            points += 6;
        }

        LocalTime purchaseTime = LocalTime.parse(receipt.getPurchaseTime(), DateTimeFormatter.ofPattern("HH:mm"));
        if (!purchaseTime.isBefore(LocalTime.of(14, 0)) && !purchaseTime.isAfter(LocalTime.of(16, 0))) {
            points += 10;
        }

        return points;
    }
}

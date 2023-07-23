package com.taidt9.springbootlibrary.controller;

import com.stripe.model.PaymentIntent;
import com.taidt9.springbootlibrary.Utils.ExtractJWT;
import com.taidt9.springbootlibrary.reqmodels.PaymentInfoRequest;
import com.taidt9.springbootlibrary.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/payment/secure")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest) throws Exception {
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentString = paymentIntent.toJson();

        return new ResponseEntity<>(paymentString, HttpStatus.OK);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestHeader(value = "Authorization") String token) throws Exception {
        String useEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (useEmail == null) throw new Exception("User email is missing");
        return paymentService.stripePayment(useEmail);
    }
}

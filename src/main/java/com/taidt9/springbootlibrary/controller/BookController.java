package com.taidt9.springbootlibrary.controller;

import com.taidt9.springbootlibrary.Utils.ExtractJWT;
import com.taidt9.springbootlibrary.entity.Book;
import com.taidt9.springbootlibrary.resmodels.ShelfCurrentLoansResponse;
import com.taidt9.springbootlibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/secure/current-loans")
    public ResponseEntity<List<ShelfCurrentLoansResponse>> currentLoans(@RequestHeader(value = "Authorization") String token) throws Exception {
        String useEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return new ResponseEntity<>(bookService.currentLoans(useEmail), HttpStatus.OK);
    }

    @GetMapping("/secure/current-loans/count")
    public ResponseEntity<Integer> currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return new ResponseEntity<>(bookService.currentLoansCount(userEmail), HttpStatus.OK);
    }

    @GetMapping("/secure/checked-out/by-user")
    public ResponseEntity<Boolean> checkoutBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        Boolean checkedOut = bookService.checkoutBookByUser(userEmail, bookId);
        return new ResponseEntity<>(checkedOut, HttpStatus.OK);
    }

    @PutMapping("/secure/checkout")
    public ResponseEntity<Book> checkOutBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        Book book = bookService.checkoutBook(userEmail, bookId);
        return (new ResponseEntity<>(book, HttpStatus.OK));
    }

    @PutMapping("/secure/return")
    public ResponseEntity returnBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        try {
            bookService.returnBook(userEmail, bookId);
            return new ResponseEntity(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(null, HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @PutMapping("/secure/renew-loan")
    public ResponseEntity renewLoan(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        try {
            bookService.renewLoan(userEmail, bookId);
            return new ResponseEntity(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(null, HttpStatus.FAILED_DEPENDENCY);
        }
    }
}

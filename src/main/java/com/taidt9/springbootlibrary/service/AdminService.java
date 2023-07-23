package com.taidt9.springbootlibrary.service;

import com.taidt9.springbootlibrary.dao.BookRepository;
import com.taidt9.springbootlibrary.dao.CheckoutRepository;
import com.taidt9.springbootlibrary.dao.ReviewRepository;
import com.taidt9.springbootlibrary.entity.Book;
import com.taidt9.springbootlibrary.reqmodels.AddBookRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AdminService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final CheckoutRepository checkoutRepository;

    @Autowired
    public AdminService(BookRepository bookRepository, ReviewRepository reviewRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.reviewRepository = reviewRepository;
    }

    public void increaseBookQuantity(Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent()) throw new Exception("Book not found");

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        book.get().setCopies(book.get().getCopies() + 1);
        bookRepository.save(book.get());
    }

    public void decreaseBookQuantity(Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent() || book.get().getCopies() <= 0 || book.get().getCopiesAvailable() <= 0)
            throw new Exception("Book not found");

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        book.get().setCopies(book.get().getCopies() - 1);
        bookRepository.save(book.get());
    }

    public void postBook(AddBookRequest addBookRequest) {

        Book book = new Book();

        book.setTitle(addBookRequest.getTitle());
        book.setAuthor(addBookRequest.getAuthor());
        book.setDescription(addBookRequest.getDescription());
        book.setCopiesAvailable(addBookRequest.getCopies());
        book.setCopies(addBookRequest.getCopies());
        book.setCategory(addBookRequest.getCategory());
        book.setImg(addBookRequest.getImg());

        bookRepository.save(book);
    }

    public void deleteBook(Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent()) throw new Exception("Book not found");
        bookRepository.delete(book.get());
        reviewRepository.deleteAllByBookId(bookId);
        checkoutRepository.deleteAllByBookId(bookId);
    }
}

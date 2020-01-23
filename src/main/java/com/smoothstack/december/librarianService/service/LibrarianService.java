package com.smoothstack.december.librarianService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smoothstack.december.librarianService.dao.AuthorDAO;
import com.smoothstack.december.librarianService.dao.BookCopyDAO;
import com.smoothstack.december.librarianService.dao.BookDAO;
import com.smoothstack.december.librarianService.dao.GenreDAO;
import com.smoothstack.december.librarianService.dao.LibraryBranchDAO;
import com.smoothstack.december.librarianService.entity.Author;
import com.smoothstack.december.librarianService.entity.Book;
import com.smoothstack.december.librarianService.entity.BookCopy;
import com.smoothstack.december.librarianService.entity.Genre;
import com.smoothstack.december.librarianService.entity.LibraryBranch;
import com.smoothstack.december.librarianService.exception.ArgumentMissingException;
import com.smoothstack.december.librarianService.exception.IllegalRelationReferenceException;

@Service
@Transactional
public class LibrarianService {

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private AuthorDAO authorDAO;

    @Autowired
    private GenreDAO genreDAO;

    @Autowired
    private BookCopyDAO bookCopyDAO;

    @Autowired
    private LibraryBranchDAO libraryBranchDAO;

    public BookCopy createBookCopy(BookCopy bookCopy) {
        if (bookCopy.getId() == null) {
            throw new ArgumentMissingException("Missing 'id'");
        }
        if (bookCopy.getId().getBook().getId() == null) {
            throw new ArgumentMissingException("Missing 'book: {id}'");
        }
        if (bookCopy.getId().getBranch().getId() == null) {
            throw new ArgumentMissingException("Missing 'branch: {id}'");
        }
        if (bookCopy.getAmount() == null) {
            throw new ArgumentMissingException("Missing 'amount'");
        }
        if (!this.bookDAO.existsById(bookCopy.getId().getBook().getId())) {
            throw new IllegalRelationReferenceException("Book does not exist");
        }
        if (!this.libraryBranchDAO.existsById(bookCopy.getId().getBranch().getId())) {
            throw new IllegalRelationReferenceException("Library branch does not exist");
        }

        return this.bookCopyDAO.save(bookCopy);
    }

    public Book createBook(Book book) {
        if (book.getId() == null) {
            throw new ArgumentMissingException("Missing 'id'");
        }
        if (book.getTitle() == null) {
            throw new ArgumentMissingException("Missing 'title'");
        }
        if (book.getPublisher().getId() == null) {
            throw new ArgumentMissingException("Missing 'publisher: {id}'");
        }
        if (book.getAuthors().size() == 0) {
            throw new ArgumentMissingException("Missing 'authors: [{id}]'");
        }
        if (book.getGenres().size() == 0) {
            throw new ArgumentMissingException("Missing 'genres: [{id}]'");
        }
        for (Author author : book.getAuthors()) {
            if (author.getId() == null) {
                throw new ArgumentMissingException("Missing 'authors [{id}]");
            }
            if (!this.authorDAO.existsById(author.getId())) {
                throw new IllegalRelationReferenceException(
                        "The author with id of " + author.getId() + " does not exist");
            }
        }
        for (Genre genre : book.getGenres()) {
            if (genre.getId() == null) {
                throw new ArgumentMissingException("Missing 'genres [{id}]");
            }
            if (!this.genreDAO.existsById(genre.getId())) {
                throw new IllegalRelationReferenceException(
                        "The genre with id of " + genre.getId() + " does not exist");
            }
        }

        return this.bookDAO.save(book);
    }

    public List<Book> getBooks() {
        return this.bookDAO.findAll();
    }

    public List<LibraryBranch> getLibraryBranches() {
        return this.libraryBranchDAO.findAll();
    }

    public List<BookCopy> getBookCopiesById(Long branchId) {
        if (!this.libraryBranchDAO.existsById(branchId)) {
            throw new IllegalRelationReferenceException("Library branch does not exist");
        }

        return this.bookCopyDAO.findBookCopiesById(branchId);
    }

    public BookCopy updateBookCopy(BookCopy bookCopy) {
        if (bookCopy.getId() == null) {
            throw new ArgumentMissingException("Missing book copy 'id'");
        }
        if (bookCopy.getId().getBook().getId() == null) {
            throw new ArgumentMissingException("Missing 'book: {id}'");
        }
        if (bookCopy.getId().getBranch().getId() == null) {
            throw new ArgumentMissingException("Missing 'branch: {id}'");
        }
        if (bookCopy.getAmount() == null) {
            throw new ArgumentMissingException("Missing 'amount'");
        }
        if (!this.bookDAO.existsById(bookCopy.getId().getBook().getId())) {
            throw new IllegalRelationReferenceException("Book does not exist");
        }
        if (!this.libraryBranchDAO.existsById(bookCopy.getId().getBranch().getId())) {
            throw new IllegalRelationReferenceException("Library branch does not exist");
        }

        return this.bookCopyDAO.save(bookCopy);
    }

    public LibraryBranch updateLibraryBranch(LibraryBranch branch) {
        if (branch.getId() == null) {
            throw new ArgumentMissingException("Missing 'id'");
        }
        if (branch.getName() == null) {
            throw new ArgumentMissingException("Missing 'name'");
        }
        if (branch.getAddress() == null) {
            throw new ArgumentMissingException("Missing 'address'");
        }

        return this.libraryBranchDAO.save(branch);
    }

}

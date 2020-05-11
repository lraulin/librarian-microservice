package com.smoothstack.lms.librarianservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.smoothstack.lms.librarianservice.dao.AuthorDAO;
import com.smoothstack.lms.librarianservice.dao.BookCopyDAO;
import com.smoothstack.lms.librarianservice.dao.BookDAO;
import com.smoothstack.lms.librarianservice.dao.GenreDAO;
import com.smoothstack.lms.librarianservice.dao.LibraryBranchDAO;
import com.smoothstack.lms.librarianservice.entity.Author;
import com.smoothstack.lms.librarianservice.entity.Book;
import com.smoothstack.lms.librarianservice.entity.BookCopy;
import com.smoothstack.lms.librarianservice.entity.BookCopy.BookCopyId;
import com.smoothstack.lms.librarianservice.entity.Genre;
import com.smoothstack.lms.librarianservice.entity.LibraryBranch;
import com.smoothstack.lms.librarianservice.entity.Publisher;
import com.smoothstack.lms.librarianservice.exception.ArgumentMissingException;
import com.smoothstack.lms.librarianservice.exception.IllegalRelationReferenceException;
import com.smoothstack.lms.librarianservice.exception.ResourceAlreadyExistsException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LibrarianServiceTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private AuthorDAO authorDAO;

    @Mock
    private GenreDAO genreDAO;

    @Mock
    private BookCopyDAO bookCopyDAO;

    @Mock
    private LibraryBranchDAO libraryBranchDAO;

    @InjectMocks
    private LibrarianService librarianService;

    @Test
    public void testSuccessfulCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookCopyDAO.save(bookCopy)).thenReturn(bookCopy);
        Mockito.when(this.bookDAO.existsById(bookCopy.getId().getBook().getId())).thenReturn(true);
        Mockito.when(this.libraryBranchDAO.existsById(bookCopy.getId().getBranch().getId())).thenReturn(true);
        Mockito.when(this.bookCopyDAO.existsById(bookCopy.getId())).thenReturn(false);
        BookCopy result = this.librarianService.createBookCopy(bookCopy);
        Long[] expecteds = new Long[] { bookId, branchId };
        Long[] actuals = new Long[] { result.getId().getBook().getId(), result.getId().getBranch().getId() };
        Assertions.assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testNullBookCopyIdCreateBookCopy() {
        Long amount = 1l;
        BookCopy bookCopy = new BookCopy();
        bookCopy.setAmount(amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullBookIdCreateBookCopy() {
        Long bookId = null;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullBranchIdCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = null;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullAmountCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = null;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testInvalidBookIdCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookDAO.existsById(bookCopy.getId().getBook().getId())).thenReturn(false);

        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testInvalidBranchIdCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookDAO.existsById(bookCopy.getId().getBook().getId())).thenReturn(true);
        Mockito.when(this.libraryBranchDAO.existsById(bookCopy.getId().getBranch().getId())).thenReturn(false);

        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testInvalidBookCopyIdCreateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookDAO.existsById(bookCopy.getId().getBook().getId())).thenReturn(true);
        Mockito.when(this.libraryBranchDAO.existsById(bookCopy.getId().getBranch().getId())).thenReturn(true);
        Mockito.when(this.bookCopyDAO.existsById(bookCopy.getId())).thenReturn(true);

        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> {
            this.librarianService.createBookCopy(bookCopy);
        });
    }

    @Test
    public void testNonZeroGetBooksNotInBookCopies() {
        Long branchId = 1l;
        List<BookCopy> bookCopies = Arrays
                .asList(new BookCopy[] { this.createBookCopy(1l, 1l, 1l), this.createBookCopy(2l, 2l, 2l) });
        List<Book> books = Arrays
                .asList(new Book[] { this.createBook(3l, "test1", 1l, new Long[] { 1l }, new Long[] { 1l }),
                        this.createBook(4l, "test2", 2l, new Long[] { 1l }, new Long[] { 1l }) });

        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(true);
        Mockito.when(this.bookCopyDAO.findBookCopiesById(branchId)).thenReturn(bookCopies);
        Mockito.when(this.bookDAO.findAll()).thenReturn(books);

        int expected = 2;
        int actual = this.librarianService.getBooksNotInBookCopies(branchId).size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testZeroGetBooksNotInBookCopies() {
        Long branchId = 1l;
        List<BookCopy> bookCopies = Arrays
                .asList(new BookCopy[] { this.createBookCopy(1l, 1l, 1l), this.createBookCopy(2l, 2l, 2l) });
        List<Book> books = Arrays
                .asList(new Book[] { this.createBook(1l, "test1", 1l, new Long[] { 1l }, new Long[] { 1l }),
                        this.createBook(2l, "test2", 2l, new Long[] { 1l }, new Long[] { 1l }) });

        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(true);
        Mockito.when(this.bookCopyDAO.findBookCopiesById(branchId)).thenReturn(bookCopies);
        Mockito.when(this.bookDAO.findAll()).thenReturn(books);

        int expected = 0;
        int actual = this.librarianService.getBooksNotInBookCopies(branchId).size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testInvalidBranchIdGetBooksNotInBookCopies() {
        Long branchId = 1l;
        List<BookCopy> bookCopies = Arrays
                .asList(new BookCopy[] { this.createBookCopy(1l, 1l, 1l), this.createBookCopy(2l, 2l, 2l) });
        List<Book> books = Arrays
                .asList(new Book[] { this.createBook(1l, "test1", 1l, new Long[] { 1l }, new Long[] { 1l }),
                        this.createBook(2l, "test2", 2l, new Long[] { 1l }, new Long[] { 1l }) });

        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(false);

        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.getBooksNotInBookCopies(branchId).size();
        });
    }

    @Test
    public void testNullBranchIdGetBooksNotInBookCopies() {
        Long branchId = null;
        List<BookCopy> bookCopies = Arrays
                .asList(new BookCopy[] { this.createBookCopy(1l, 1l, 1l), this.createBookCopy(2l, 2l, 2l) });
        List<Book> books = Arrays
                .asList(new Book[] { this.createBook(1l, "test1", 1l, new Long[] { 1l }, new Long[] { 1l }),
                        this.createBook(2l, "test2", 2l, new Long[] { 1l }, new Long[] { 1l }) });

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.getBooksNotInBookCopies(branchId);
        });
    }

    @Test
    public void testNonZeroSizeOfGetBranches() {
        List<LibraryBranch> branches = new ArrayList<>();
        branches.add(createLibraryBranch(1l, "name_1", "address_1"));
        branches.add(createLibraryBranch(2l, "name_2", "address_2"));
        Mockito.when(this.libraryBranchDAO.findAll()).thenReturn(branches);
        int expected = 2;
        int actual = this.librarianService.getLibraryBranches().size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testZeroSizeOfGetBranches() {
        List<LibraryBranch> branches = new ArrayList<>();
        Mockito.when(this.libraryBranchDAO.findAll()).thenReturn(branches);
        int expected = 0;
        int actual = this.librarianService.getLibraryBranches().size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testSuccessfullGetBranch() {
        Long branchId = 1l;
        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(true);
        Mockito.when(this.libraryBranchDAO.findById(branchId))
                .thenReturn(Optional.of(this.createLibraryBranch(branchId, "test", "test")));

        Long expected = 1l;
        Long actual = this.librarianService.getLibraryBranchById(branchId).getId();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testNullBranchIdGetBranch() {
        Long branchId = null;
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.getLibraryBranchById(branchId);
        });
    }

    @Test
    public void testInvalidBranchIdGetBranch() {
        Long branchId = 1l;
        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(false);
        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.getLibraryBranchById(branchId);
        });
    }

    @Test
    public void testSuccessfullGetBookCopies() {
        Long branchId = 1l;
        List<BookCopy> bookCopies = Arrays
                .asList(new BookCopy[] { this.createBookCopy(1l, 1l, 1l), this.createBookCopy(2l, 2l, 2l) });
        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(true);
        Mockito.when(this.bookCopyDAO.findBookCopiesById(branchId)).thenReturn(bookCopies);

        int expected = 2;
        int actual = this.librarianService.getBookCopiesById(branchId).size();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testNullBranchIdGetBookCopies() {
        Long branchId = null;
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.getBookCopiesById(branchId);
        });
    }

    @Test
    public void testInvalidBranchIdGetBookCopies() {
        Long branchId = 1l;
        Mockito.when(this.libraryBranchDAO.existsById(branchId)).thenReturn(false);
        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.getBookCopiesById(branchId);
        });
    }

    @Test
    public void testSuccessfulUpdateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookCopyDAO.existsById(bookCopy.getId())).thenReturn(true);
        Mockito.when(this.bookCopyDAO.save(bookCopy)).thenReturn(bookCopy);
        BookCopy result = this.librarianService.updateBookCopy(bookCopy);
        Long[] expecteds = new Long[] { bookId, branchId };
        Long[] actuals = new Long[] { result.getId().getBook().getId(), result.getId().getBranch().getId() };
        Assertions.assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testNullBookCopyIdUpdateBookCopy() {
        Long amount = 1l;
        BookCopy bookCopy = new BookCopy();
        bookCopy.setAmount(amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullBookIdUpdateBookCopy() {
        Long bookId = null;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullBranchIdUpdateBookCopy() {
        Long bookId = 1l;
        Long branchId = null;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateBookCopy(bookCopy);
        });
    }

    @Test
    public void testNullAmountUpdateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = null;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);

        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateBookCopy(bookCopy);
        });
    }

    @Test
    public void testInvalidBookCopyIdUpdateBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        Long amount = 1l;
        BookCopy bookCopy = createBookCopy(bookId, branchId, amount);
        Mockito.when(this.bookCopyDAO.existsById(bookCopy.getId())).thenReturn(false);

        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.updateBookCopy(bookCopy);
        });
    }

    @Test
    public void testSuccessfullUpdateLibraryBranch() {
        Long id = 1l;
        String name = "name";
        String address = "address";
        LibraryBranch branch = this.createLibraryBranch(id, name, address);
        Mockito.when(this.libraryBranchDAO.save(branch)).thenReturn(branch);
        Long expected = 1l;
        Long actual = this.librarianService.updateLibraryBranch(branch).getId();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testNullBranchIdUpdateLibraryBranch() {
        Long id = null;
        String name = "name";
        String address = "address";
        LibraryBranch branch = this.createLibraryBranch(id, name, address);
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateLibraryBranch(branch);
        });
    }

    @Test
    public void testNullNameUpdateLibraryBranch() {
        Long id = 1l;
        String name = null;
        String address = "address";
        LibraryBranch branch = this.createLibraryBranch(id, name, address);
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateLibraryBranch(branch);
        });
    }

    @Test
    public void testNullAddressUpdateLibraryBranch() {
        Long id = 1l;
        String name = "name";
        String address = null;
        LibraryBranch branch = this.createLibraryBranch(id, name, address);
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.updateLibraryBranch(branch);
        });
    }

    @Test
    public void testSuccessfullRemoveBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        BookCopyId bookCopyId = this.createBookCopyId(bookId, branchId);
        Mockito.when(this.bookCopyDAO.existsById(bookCopyId)).thenReturn(true);
        Mockito.doNothing().when(this.bookCopyDAO).deleteById(bookCopyId);
        Assertions.assertDoesNotThrow(() -> {
            this.librarianService.removeBookCopy(bookCopyId);
        });
    }

    @Test
    public void testNullBookCopyIdRemoveBookCopy() {
        BookCopyId bookCopyId = null;
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.removeBookCopy(bookCopyId);
        });
    }

    @Test
    public void testNullBookIdRemoveBookCopy() {
        Long bookId = null;
        Long branchId = 1l;
        BookCopyId bookCopyId = this.createBookCopyId(bookId, branchId);
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.removeBookCopy(bookCopyId);
        });
    }

    @Test
    public void testNullBranchIdRemoveBookCopy() {
        Long bookId = 1l;
        Long branchId = null;
        BookCopyId bookCopyId = this.createBookCopyId(bookId, branchId);
        Assertions.assertThrows(ArgumentMissingException.class, () -> {
            this.librarianService.removeBookCopy(bookCopyId);
        });
    }

    @Test
    public void testInvalidBookCopyIdRemoveBookCopy() {
        Long bookId = 1l;
        Long branchId = 1l;
        BookCopyId bookCopyId = this.createBookCopyId(bookId, branchId);
        Mockito.when(this.bookCopyDAO.existsById(bookCopyId)).thenReturn(false);
        Assertions.assertThrows(IllegalRelationReferenceException.class, () -> {
            this.librarianService.removeBookCopy(bookCopyId);
        });
    }

    private LibraryBranch createLibraryBranch(Long id, String name, String address) {
        LibraryBranch branch = new LibraryBranch();
        branch.setId(id);
        branch.setName(name);
        branch.setAddress(address);
        return branch;
    }

    private BookCopy createBookCopy(Long bookId, Long branchId, Long amount) {
        BookCopy bookCopy = new BookCopy();
        BookCopyId bookCopyId = this.createBookCopyId(bookId, branchId);
        bookCopy.setId(bookCopyId);
        bookCopy.setAmount(amount);
        return bookCopy;
    }

    private BookCopyId createBookCopyId(Long bookId, Long branchId) {
        BookCopyId bookCopyId = new BookCopyId();
        Book book = new Book();
        LibraryBranch branch = new LibraryBranch();
        book.setId(bookId);
        branch.setId(branchId);
        bookCopyId.setBook(book);
        bookCopyId.setBranch(branch);
        return bookCopyId;
    }

    private Book createBook(Long id, String title, Long publisherId, Long[] authorIds, Long[] genreIds) {
        Book book = new Book();
        Publisher publisher = new Publisher();

        publisher.setId(publisherId);
        book.setTitle(title);
        book.setPublisher(publisher);
        book.setId(id);
        for (Long authorId : authorIds) {
            Author author = new Author();
            author.setId(authorId);
            book.addAuthor(author);
        }
        for (Long genreId : genreIds) {
            Genre genre = new Genre();
            genre.setId(genreId);
            book.addGenre(genre);
        }

        return book;
    }

}

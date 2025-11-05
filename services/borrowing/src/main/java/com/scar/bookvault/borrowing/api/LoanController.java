package com.scar.bookvault.borrowing.api;

import com.scar.bookvault.borrowing.domain.Loan;
import com.scar.bookvault.borrowing.domain.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowing/v1/loans")
public class LoanController {
    private final LoanRepository loanRepository;

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @GetMapping
    public List<Loan> list() { return loanRepository.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan create(@RequestBody Loan loan) { return loanRepository.save(loan); }
}

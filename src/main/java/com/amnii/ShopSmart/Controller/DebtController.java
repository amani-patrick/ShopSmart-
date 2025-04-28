package com.amnii.ShopSmart.Controller;

import com.amnii.ShopSmart.Models.Debt;
import com.amnii.ShopSmart.Services.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/debts")
public class DebtController {

    @Autowired
    private DebtService debtService;

    @GetMapping
    public List<Debt> getAllDebts() {
        return debtService.getAllDebts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Debt> getDebtById(@PathVariable Long id) {
        return debtService.getDebtById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Debt createDebt(@RequestBody Debt debt) {
        return debtService.createDebt(debt);
    }

    @PutMapping("/{id}")
    public Debt updateDebt(@PathVariable Long id, @RequestBody Debt debt) {
        return debtService.updateDebt(id, debt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebt(@PathVariable Long id) {
        debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }
} 
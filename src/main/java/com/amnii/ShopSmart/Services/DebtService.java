package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.Debt;
import com.amnii.ShopSmart.Repository.DebtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DebtService {

    @Autowired
    private DebtRepository debtRepository;

    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    public Optional<Debt> getDebtById(Long id) {
        return debtRepository.findById(id);
    }

    public Debt createDebt(Debt debt) {
        return debtRepository.save(debt);
    }

    public Debt updateDebt(Long id, Debt debt) {
        debt.setId(id);
        return debtRepository.save(debt);
    }

    public void deleteDebt(Long id) {
        debtRepository.deleteById(id);
    }
} 
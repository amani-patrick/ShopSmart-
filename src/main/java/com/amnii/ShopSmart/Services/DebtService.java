package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.Debt;
import com.amnii.ShopSmart.Models.User;
import com.amnii.ShopSmart.Repository.DebtRepository;
import com.amnii.ShopSmart.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DebtService {

    @Autowired
    private DebtRepository debtRepository;

    private User getCurrentUser() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public List<Debt> getAllDebts() {
        return debtRepository.findByUser(getCurrentUser());
    }

    public Optional<Debt> getDebtById(Long id) {
        return debtRepository.findById(id)
                .filter(debt -> debt.getUser().getId().equals(getCurrentUser().getId()));
    }

    public Debt createDebt(Debt debt) {
        debt.setUser(getCurrentUser());
        return debtRepository.save(debt);
    }

    public Debt updateDebt(Long id, Debt debt) {
        Debt existingDebt = getDebtById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt not found"));
        
        if (!existingDebt.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Debt not found");
        }

        debt.setId(id);
        debt.setUser(getCurrentUser());
        return debtRepository.save(debt);
    }

    public void deleteDebt(Long id) {
        Debt debt = getDebtById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt not found"));
        
        if (!debt.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Debt not found");
        }

        debtRepository.deleteById(id);
    }
} 
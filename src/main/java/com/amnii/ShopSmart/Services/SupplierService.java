package com.amnii.ShopSmart.Services;

import com.amnii.ShopSmart.Models.Supplier;
import com.amnii.ShopSmart.Models.User;
import com.amnii.ShopSmart.Repository.SupplierRepository;
import com.amnii.ShopSmart.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    private User getCurrentUser() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByUser(getCurrentUser());
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .filter(supplier -> supplier.getUser().getId().equals(getCurrentUser().getId()));
    }

    public Supplier createSupplier(Supplier supplier) {
        supplier.setUser(getCurrentUser());
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existingSupplier = getSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        if (!existingSupplier.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Supplier not found");
        }

        supplier.setId(id);
        supplier.setUser(getCurrentUser());
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        
        if (!supplier.getUser().getId().equals(getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Supplier not found");
        }

        supplierRepository.deleteById(id);
    }
}

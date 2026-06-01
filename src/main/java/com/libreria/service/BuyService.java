package com.libreria.service;

import com.libreria.model.exchange.Buy;
import com.libreria.model.repositories.BuyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BuyService {

    private final BuyRepository buyRepository;

    public BuyService(BuyRepository buyRepository) {
        this.buyRepository = buyRepository;
    }

    public List<Buy> getBuys() {
        return buyRepository.findAll();
    }

    public Optional<Buy> getBuy(Long id) {
        return buyRepository.findById(id);
    }

    public Buy addBuy(Buy buy) {
        return buyRepository.save(buy);
    }

    public Optional<Buy> updateBuy(Long id, Buy buy) {
        return buyRepository.findById(id).map(existing -> {
            buy.setId(id);
            return buyRepository.save(buy);
        });
    }

    public boolean deleteBuy(Long id) {
        if (!buyRepository.existsById(id)) {
            return false;
        }
        buyRepository.deleteById(id);
        return true;
    }
}

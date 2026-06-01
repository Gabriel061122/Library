package com.libreria.api;

import com.libreria.model.exchange.Buy;
import com.libreria.model.repositories.BuyRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/buys")
@RestController
public class BuyController {

    private final BuyRepository buyRepository;

    public BuyController(BuyRepository buyRepository) {
        this.buyRepository = buyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Buy>> getBuys() {
        return ResponseEntity.ok(buyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Buy> getBuy(@PathVariable Long id) {
        return buyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Buy> addBuy(@RequestBody Buy buy) {
        return ResponseEntity.ok(buyRepository.save(buy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Buy> updateBuy(@PathVariable Long id, @RequestBody Buy buy) {
        return buyRepository.findById(id)
                .map(existing -> {
                    buy.setId(id);
                    return ResponseEntity.ok(buyRepository.save(buy));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuy(@PathVariable Long id) {
        if (!buyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        buyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

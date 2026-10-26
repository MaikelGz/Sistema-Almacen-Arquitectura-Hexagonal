package com.acme.inventory.infrastructure.rest;

import com.acme.inventory.application.services.InventoryService;
import com.acme.inventory.infrastructure.rest.dto.InventoryItemDTO;
import com.acme.inventory.infrastructure.rest.dto.MovementDTO;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InventoryViewController {

    private final InventoryService inventoryService;


    public InventoryViewController(InventoryService inventoryService) {
       this.inventoryService = inventoryService;

    }

    @GetMapping("/inventory")
    public String getInventoryList(Model model) {
        List<InventoryItemDTO> inventoryList = inventoryService.getAllItems();
        List<MovementDTO> movementsList = inventoryService.getAllMovements();
        model.addAttribute("inventoryItems", inventoryList);
        model.addAttribute("movementsList", movementsList);
        return "inventory"; // Este es el nombre de la vista Thymeleaf
    }


}

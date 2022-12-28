package com.ltp.globalsuperstore.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ltp.globalsuperstore.Constants;
import com.ltp.globalsuperstore.Item;
import com.ltp.globalsuperstore.service.StoreService;

@Controller
public class StoreController {

    StoreService storeService = new StoreService();

    @GetMapping("/")
    public String getForm(Model model, @RequestParam(required = false) String id) {
        int index = getItemIndex(id);
        model.addAttribute("item", index == Constants.NOT_FOUND ? new Item() : storeService.getItem(index));
        model.addAttribute("categories", Constants.CATEGORIES);
        return "index";
    }

    @PostMapping("/submitItem")
    public String handleSubmit(@Valid Item item, BindingResult result, RedirectAttributes redirectAttributes) {
        if (item.getDiscount() > item.getPrice()) {
            result.rejectValue("price", "", "Price cannot be less than discount!");
        }

        if (result.hasErrors())
            return "index";

        int index = getItemIndex(item.getId());
        if (index == Constants.NOT_FOUND) {
            storeService.addItem(item);
        } else {
            storeService.updateItem(index, item);
        }
        redirectAttributes.addFlashAttribute("status", Constants.SUCCESS_STATUS);
        return "redirect:/inventory";
    }

    @GetMapping("/inventory")
    public String getInventory(Model model) {
        model.addAttribute("items", storeService.getItems());
        return "inventory";
    }

    public int getItemIndex(String id) {
        for (int i = 0; i < storeService.getItems().size(); i++) {
            if (storeService.getItems().get(i).getId().equals(id))
                return i;
        }
        return Constants.NOT_FOUND;
    }
}

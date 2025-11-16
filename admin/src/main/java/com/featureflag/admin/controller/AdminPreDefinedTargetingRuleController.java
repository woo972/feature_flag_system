package com.featureflag.admin.controller;

import com.featureflag.admin.dto.CreatePreDefinedTargetingRuleRequest;
import com.featureflag.admin.dto.UpdatePreDefinedTargetingRuleRequest;
import com.featureflag.admin.service.AdminPreDefinedTargetingRuleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping("/admin/pre-defined-targeting-rules")
public class AdminPreDefinedTargetingRuleController {
    private final AdminPreDefinedTargetingRuleService service;

    private static final String VIEW_BASE_PATH = "predefined-rules";

    @GetMapping
    public String listPage(
            @RequestParam(value = "page", defaultValue = "0", required = false) @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false)
            @Min(1) @Max(100) int size,
            @RequestParam(value = "sort", defaultValue = "id", required = false)
            @Pattern(regexp = "^[a-zA-Z0-9_.]+$") String sort,
            @RequestParam(value = "direction", defaultValue = "desc", required = false)
            @Pattern(regexp = "^(?i)(asc|desc)$") String direction,
            Model model) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction), sort));
        var rules = service.listPaged(pageable);
        model.addAttribute("rulesPage", rules);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        return VIEW_BASE_PATH + "/list";
    }

    @GetMapping("/new")
    public String createPage(Model model) {
        model.addAttribute("createRequest",
                CreatePreDefinedTargetingRuleRequest.builder().build());
        return VIEW_BASE_PATH + "/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("createRequest") CreatePreDefinedTargetingRuleRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("createRequest", request);
            return VIEW_BASE_PATH + "/form";
        }
        service.create(request);
        redirectAttributes.addFlashAttribute("Success",
                "Pre-defined targeting rule created.");
        return "redirect:/admin/pre-defined-targeting-rules";
    }

    @GetMapping("/{id}")
    public String detailPage(@PathVariable @Positive long id, Model model) {
        model.addAttribute("rule", service.get(id));
        return VIEW_BASE_PATH + "/detail";
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable @Positive long id, Model model) {
        var rule = service.get(id);
        var updateRequest = UpdatePreDefinedTargetingRuleRequest.builder()
                .description(rule.getDescription())
                .operator(rule.getOperator())
                .values(rule.getValues())
                .build();
        model.addAttribute("rule", rule);
        model.addAttribute("updateRequest", updateRequest);
        return VIEW_BASE_PATH + "/edit-form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable @Positive long id,
            @Valid @ModelAttribute("updateRequest") UpdatePreDefinedTargetingRuleRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            var rule = service.get(id);
            model.addAttribute("rule", rule);
            model.addAttribute("updateRequest", request);
            return VIEW_BASE_PATH + "/edit-form";
        }
        service.update(id, request);
        redirectAttributes.addFlashAttribute("Success",
                "Pre-defined targeting rule updated.");
        return "redirect:/admin/pre-defined-targeting-rules/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable @Positive long id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("Success",
                "Pre-defined targeting rule deleted.");
        return "redirect:/admin/pre-defined-targeting-rules";
    }
}

package com.featureflag.admin.controller;

import com.featureflag.admin.service.AdminFeatureFlagService;
import com.featureflag.shared.api.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/admin")
public class AdminController {
    private final AdminFeatureFlagService adminFeatureFlagService;

    private static final String ADMIN_VIEW_BASE_PATH = "featureflags";

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute("featureFlagPage",
                adminFeatureFlagService.list(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))));
        return ADMIN_VIEW_BASE_PATH + "/dashboard";
    }

    @GetMapping("/feature-flags/new")
    public String registerPage(Model model) {
        model.addAttribute("registerFeatureFlagRequest", RegisterFeatureFlagRequest.builder().build());
        return ADMIN_VIEW_BASE_PATH + "/form";
    }

    @PostMapping("/feature-flags")
    public String register(@Valid @ModelAttribute("registerFeatureFlagRequest") RegisterFeatureFlagRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerFeatureFlagRequest", request);
            return ADMIN_VIEW_BASE_PATH + "/form";
        }
        adminFeatureFlagService.register(request);
        redirectAttributes.addFlashAttribute("Success", "Feature flag registered.");
        return "redirect:/admin";
    }

    @GetMapping("/feature-flags")
    public String listPage(@RequestParam(value = "page", defaultValue = "0", required = false) @Min(0) int page,
                           @RequestParam(value = "size", defaultValue = "20", required = false) @Min(1) @Max(100) int size,
                           @RequestParam(value = "sort", defaultValue = "id", required = false) @Pattern(regexp = "^[a-zA-Z0-9_.]+$") String sort,
                           @RequestParam(value = "direction", defaultValue = "desc", required = false) @Pattern(regexp = "^(?i)(asc|desc)$") String direction,
                           Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<FeatureFlag> featureFlags = adminFeatureFlagService.list(pageable);
        model.addAttribute("featureFlagPage", featureFlags);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        return ADMIN_VIEW_BASE_PATH + "/list";
    }

    @GetMapping("/feature-flags/{id}")
    public String detailPage(@PathVariable(value = "id", required = true) @Positive long id,
                             Model model) {
        model.addAttribute("featureFlag", adminFeatureFlagService.get(id));
        return ADMIN_VIEW_BASE_PATH + "/detail";
    }

    @PostMapping("/feature-flags/{id}/on")
    public String on(@PathVariable(value = "id", required = true) @Positive long id) {
        adminFeatureFlagService.on(id);
        return "redirect:/admin/feature-flags/" + id;
    }

    @PostMapping("/feature-flags/{id}/off")
    public String off(@PathVariable(value = "id", required = true) @Positive long id) {
        adminFeatureFlagService.off(id);
        return "redirect:/admin/feature-flags/" + id;
    }

    @PostMapping("/feature-flags/{id}/archive")
    public String archive(@PathVariable(value = "id", required = true) @Positive long id) {
        adminFeatureFlagService.archive(id);
        return "redirect:/admin/feature-flags/" + id;
    }
}

package com.featureflag.admin.controller;

import com.featureflag.admin.service.AdminFeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
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
    public String register(@ModelAttribute("registerFeatureFlagRequest") RegisterFeatureFlagRequest request,
                           RedirectAttributes redirectAttributes) {
        adminFeatureFlagService.register(request);
        redirectAttributes.addFlashAttribute("Success", "Feature flag registered.");
        return "redirect:/admin";
    }

    @GetMapping("/feature-flags")
    public String listPage(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                           @RequestParam(value = "size", defaultValue = "20", required = false) int size,
                           @RequestParam(value = "sort", defaultValue = "id", required = false) String sort,
                           @RequestParam(value = "direction", defaultValue = "desc", required = false) String direction,
                           Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort));
        Page<FeatureFlag> featureFlags = adminFeatureFlagService.list(pageable);
        model.addAttribute("featureFlagPage", featureFlags);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        return ADMIN_VIEW_BASE_PATH + "/list";
    }

    @GetMapping("/feature-flags/{id}")
    public String detailPage(@PathVariable(value = "id", required = true) Long id,
                             Model model) {
        model.addAttribute("featureFlag", adminFeatureFlagService.get(id));
        return ADMIN_VIEW_BASE_PATH + "/detail";
    }

    @PutMapping("/feature-flags/{id}/on")
    public String on(@PathVariable(value = "id", required = true) Long id, Model model) {
        adminFeatureFlagService.on(id);
        return "redirect:/admin/feature-flags/" + id;
    }

    @PutMapping("/feature-flags/{id}/off")
    public String off(@PathVariable(value = "id", required = true) Long id, Model model) {
        adminFeatureFlagService.off(id);
        return "redirect:/admin/feature-flags/" + id;
    }

    @PutMapping("/feature-flags/{id}/archive")
    public String archive(@PathVariable(value = "id", required = true) Long id, Model model) {
        adminFeatureFlagService.archive(id);
        return "redirect:/admin/feature-flags/" + id;
    }
}

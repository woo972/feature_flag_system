package com.featureflag.admin.controller;

import com.featureflag.admin.service.AdminFeatureFlagService;
import com.featureflag.core.service.RegisterFeatureFlagRequest;
import com.featureflag.shared.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminFeatureFlagService adminFeatureFlagService;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("featureFlagPage",
                adminFeatureFlagService.list(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))));
        return "admin/home";
    }

    @GetMapping("/register-feature-flag")
    public String registerPage(Model model) {
        model.addAttribute("registerFeatureFlagRequest", new RegisterFeatureFlagRequest());
        return "admin/register-feature-flag";
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
        return "admin/feature-flags";
    }

    @GetMapping("/feature-flags/{id}")
    public String detailPage(@PathVariable(value = "id", required = true) Long id,
                      Model model) {
        model.addAttribute("featureFlag", adminFeatureFlagService.get(id));
        return "admin/feature-flag-detail";
    }

    @PostMapping("/feature-flags")
    public String register(@ModelAttribute("registerFeatureFlagRequest") RegisterFeatureFlagRequest request) {
        System.out.println(request.getName());
        adminFeatureFlagService.register(request);
        return "redirect:/admin/";
    }

    @PutMapping("/flags/{id}")
    public String updateFlag(@PathVariable String id, @ModelAttribute FeatureFlag featureFlag) {
        // Update existing feature flag
        return "redirect:/dashboard";
    }

    @DeleteMapping("/flags/{id}")
    public String deleteFlag(@PathVariable String id) {
        // Delete feature flag
        return "redirect:/dashboard";
    }
}

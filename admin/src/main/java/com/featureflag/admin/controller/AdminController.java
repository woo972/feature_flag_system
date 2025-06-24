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

    @GetMapping("/feature-flags")
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "20") int size,
                       @RequestParam(value = "sort", defaultValue = "id") String sort,
                       @RequestParam(value = "direction", defaultValue = "desc") String direction,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort));
        Page<FeatureFlag> featureFlags = adminFeatureFlagService.list(pageable);
        model.addAttribute("featureFlagPage", featureFlags);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", direction);
        return "/admin/feature-flags";
    }

    @GetMapping("/feature-flags/{id}")
    public String get(@PathVariable(value = "id", required = true) Long id,
                      Model model) {
        model.addAttribute("featureFlag", adminFeatureFlagService.get(id));
        return "/admin/feature-flag-detail";
    }

    @PostMapping("/feature-flags")
    public ResponseEntity<Void> register(@RequestBody RegisterFeatureFlagRequest request) {
        adminFeatureFlagService.register(request);
        return ResponseEntity.created(null).build();
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

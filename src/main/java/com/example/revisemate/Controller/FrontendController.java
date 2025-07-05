package com.example.revisemate.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    /**
     * Forwards every request that
     *   • is **not** for a static file (no “.” in the last path segment) and
     *   • is **not** under /api/**
     * to index.html so the React router can handle it.
     */
    @RequestMapping({
            "/",                         // root
            "/{path:^(?!api$)[^\\.]*}",  // /something   (no dot, not "api")
            "/{path:^(?!api$)[^\\.]*}/**"// /something/anything/else (still not under /api)
    })
    public String forward() {
        return "forward:/index.html";
    }
}

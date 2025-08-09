window.onload = function() {
    window.ui = SwaggerUIBundle({
        url: "/v3/api-docs",
        dom_id: '#swagger-ui',
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        layout: "BaseLayout",
        requestInterceptor: (req) => {
            const token = sessionStorage.getItem("access_token");
            if (token) {
                req.headers["Authorization"] = "Bearer " + token;
            }
            return req;
        },
        responseInterceptor: (res) => {
            if (res.url.endsWith("/api/auth/verify-otp") && res.status === 200) {
                try {
                    const body = JSON.parse(res.text);
                    if (body.token) {
                        sessionStorage.setItem("access_token", body.token);
                    }
                } catch (e) {
                    console.warn("Could not parse verify-otp response");
                }
            }
            if (res.url.endsWith("/api/auth/refresh-token") && res.status === 200) {
                try {
                    const body = JSON.parse(res.text);
                    if (body.token) {
                        sessionStorage.setItem("access_token", body.token);
                    }
                } catch (e) {}
            }
            return res;
        }
    });
};

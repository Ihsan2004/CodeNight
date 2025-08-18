
package com.example.demo.service;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CheckoutService{
    public Map<String, Object> checkout(Long userId, Object selection) {
        Map<String,Object> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("order_id", "MOCK-" + UUID.randomUUID());
        resp.put("user_id", userId);
        resp.put("selection", selection);
        return resp;
    }
}


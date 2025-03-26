package com.example.net.controller;

import com.example.net.domain.Dns;
import com.example.net.domain.Result;
import com.example.net.service.DnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DnsController {
    @Autowired
    DnsService dnsService;

    @GetMapping("/dns")
    public Result queryDns() {
        List<Dns> dnses = dnsService.queryDns();
        if (dnses != null && !dnses.isEmpty()) return Result.success(dnses);
        else return Result.error("查询历史不存在");
    }

    @GetMapping("/dns1")
    public Result queryDns1(String url) {
        Dns dns = dnsService.queryDns1(url);
        if (dns != null ) return Result.success(dns);
        else return Result.error("查询历史不存在");
    }

    @PostMapping("/dns")
    public Result insertDns(String url) {
        Integer DnsId = dnsService.insertDns(url);
        Dns dns = dnsService.queryDns1(url);
        if (DnsId != null && !dns.getUrl().isEmpty()) return Result.success(DnsId);
        else return Result.error("添加失败");
    }
}


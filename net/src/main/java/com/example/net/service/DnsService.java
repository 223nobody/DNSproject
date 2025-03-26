package com.example.net.service;

import com.example.net.domain.Dns;
import com.example.net.mapper.MyDnsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.*;
import java.util.*;

@Service
public class DnsService {
    @Autowired
    MyDnsMapper dnsMapper;

    // 本地 DNS 缓存，存储已经解析的域名及其对应的 IP 地址
    private static final Map<String, String> dnsCache = new HashMap<>();

    /**
     * 查询数据库中的所有 DNS 记录
     */
    public List<Dns> queryDns() {
        return dnsMapper.queryDns();
    }

    /**
     * 根据 URL 查询数据库中的 DNS 记录
     */
    public Dns queryDns1(String url) {
        return dnsMapper.queryDns1(url);
    }

    /**
     * 解析 DNS 并存入数据库（支持本地缓存）
     */
    public Integer insertDns(String url) {
        String dnsServer = "8.8.8.8"; // 使用 Google Public DNS 服务器
        Dns dns = new Dns();
        dns.setUrl(url);

        // 先检查本地缓存，如果已经解析过，则直接返回缓存中的 IP
        if (dnsCache.containsKey(url)) {
            dns.setDnscode(dnsCache.get(url));
        } else {
            // 发送 DNS 查询
            String resolvedIP = sendDnsQuery(url, dnsServer, true);
            dns.setDnscode(resolvedIP);
            // 存入本地缓存
            dnsCache.put(url, resolvedIP);
        }
        return dnsMapper.insertDns(dns);
    }

    /**
     * DNS 查询（支持 CNAME 解析 & AAAA 记录）
     */
    public static String sendDnsQuery(String domain, String dnsServer, boolean allowCNAME) {
        try {
            // 处理 URL，提取纯域名部分
            domain = domain.replaceAll("^https?://", "").split("/")[0];

            // 先查询本地缓存
            if (dnsCache.containsKey(domain)) {
                return dnsCache.get(domain);
            }

            // 查询 IPv4 (A 记录)
            String ipv4Result = queryDns(domain, dnsServer, (byte) 0x01);
            if (!ipv4Result.equals("未找到IP地址")) {
                dnsCache.put(domain, ipv4Result);
                return ipv4Result;
            }

            // 查询 IPv6 (AAAA 记录)
            String ipv6Result = queryDns(domain, dnsServer, (byte) 0x1C);
            if (!ipv6Result.equals("未找到IP地址")) {
                dnsCache.put(domain, ipv6Result);
                return ipv6Result;
            }

            // 查询 CNAME（如果允许）
            if (allowCNAME) {
                String cname = queryDns(domain, dnsServer, (byte) 0x05);
                if (!cname.equals("未找到IP地址")) {
                    return sendDnsQuery(cname, dnsServer, false);
                }
            }

            return "未找到IP地址";
        } catch (Exception e) {
            return "DNS 查询失败";
        }
    }

    /**
     * 发送 DNS 查询（支持不同查询类型：A、AAAA、CNAME）
     */
    private static String queryDns(String domain, String dnsServer, byte recordType) {
        try {
            byte[] query = buildDnsQuery(domain, recordType);
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(dnsServer);

            // 发送 UDP DNS 查询请求
            DatagramPacket requestPacket = new DatagramPacket(query, query.length, serverAddress, 53);
            socket.send(requestPacket);

            // 接收 DNS 响应
            byte[] response = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            socket.receive(responsePacket);
            socket.close();

            // 解析 DNS 响应数据
            return parseDnsResponse(response, recordType);
        } catch (Exception e) {
            return "DNS 查询失败";
        }
    }

    /**
     * 构造 DNS 查询报文
     */
    public static byte[] buildDnsQuery(String domain, byte recordType) {
        try {
            byte[] header = new byte[12]; // DNS 头部长度 12 字节
            // 事务 ID (Transaction ID) - 标识唯一请求
            header[0] = (byte) 0xAA;
            header[1] = (byte) 0xBB;
            // 标志位 Flags - 递归查询 (0x0100)
            header[2] = 0x01;
            header[3] = 0x00;
            // 查询计数置为1
            header[4] = 0x00;
            header[5] = 0x01;
            // 其他字段设为 0
            for (int i = 6; i < 12; i++) {
                header[i] = 0x00;
            }
            // 查询域名部分
            byte[] queryBody = domainToBytes(domain);
            // 查询类型 Type (A 记录)
            byte[] qtype = { 0x00, 0x01 };
            // 查询类 Class (IN - Internet)
            byte[] qclass = { 0x00, 0x01 };
            // 组合最终查询报文
            byte[] query = new byte[header.length + queryBody.length + qtype.length + qclass.length];
            System.arraycopy(header, 0, query, 0, header.length);
            System.arraycopy(queryBody, 0, query, header.length, queryBody.length);
            System.arraycopy(qtype, 0, query, header.length + queryBody.length, qtype.length);
            System.arraycopy(qclass, 0, query, header.length + queryBody.length + qtype.length, qclass.length);

            return query;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将域名转换为 DNS 查询格式
     */
    private static byte[] domainToBytes(String domain) {
        String[] labels = domain.split("\\.");
        byte[] result = new byte[domain.length() + 2];
        int pos = 0;
        for (String label : labels) {
            result[pos++] = (byte) label.length();
            byte[] labelBytes = label.getBytes();
            System.arraycopy(labelBytes, 0, result, pos, labelBytes.length);
            pos += labelBytes.length;
        }
        result[pos] = 0x00;
        return result;
    }

    /**
     * 解析 DNS 响应
     */
    private static String parseDnsResponse(byte[] response, byte recordType) {
        int index = 12;
        while (response[index] != 0) {
            index++;
        }
        index += 5;

        while (index < response.length) {
            if ((response[index] & 0xC0) == 0xC0) {
                index += 2;
            } else {
                while (response[index] != 0) {
                    index++;
                }
                index++;
            }

            int type = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
            index += 8;
            int rdlength = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
            index += 2;

            if (type == recordType) {
                if (recordType == 0x01 || recordType == 0x1C) {
                    return String.format("%d.%d.%d.%d", response[index] & 0xFF, response[index + 1] & 0xFF,
                            response[index + 2] & 0xFF, response[index + 3] & 0xFF);
                }
            }
            index += rdlength;
        }
        return "未找到IP地址";
    }
}

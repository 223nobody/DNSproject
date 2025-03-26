package com.example.net.mapper;

import com.example.net.domain.Dns;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyDnsMapper extends DnsMapper{
    List<Dns> queryDns();
    int insertDns( Dns dns);
    Dns queryDns1(String url);
}


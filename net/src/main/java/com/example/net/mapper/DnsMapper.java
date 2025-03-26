package com.example.net.mapper;
import com.example.net.domain.Dns;


public interface DnsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Dns record);

    int insertSelective(Dns record);

    Dns selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Dns record);

    int updateByPrimaryKey(Dns record);

}

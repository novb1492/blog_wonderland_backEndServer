package com.example.demo.apis.settle.service;

import java.util.Map;

import com.example.demo.product.model.tryBuyDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class settleService {
    private static Logger logger=LoggerFactory.getLogger(settleService.class);
    @Autowired
    private cardService cardService;

    public void makeBuyInfor(tryBuyDto tryBuyDto,Map<String,Object>map) {
        logger.info("makeBuyInfor");
    }
}

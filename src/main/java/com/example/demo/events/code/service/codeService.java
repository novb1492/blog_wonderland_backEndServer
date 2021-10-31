package com.example.demo.events.code.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import com.example.demo.events.code.model.codesDao;
import com.example.demo.events.code.model.codesVo;
import com.example.demo.utill.utillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class codeService {
    private final static Logger logger=LoggerFactory.getLogger(codeService.class);

    @Autowired
    private codesDao codesDao;

    public void confrimCode(String codeName,int count,LinkedHashMap<String,LinkedHashMap<String,Object>>eventmap) {
        logger.info("confrimCode");
        boolean flag=utillService.checkBlankOrNull(codeName);
        if(flag){
            for(int i=0;i<count;i++){
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                map.put("codeaction","minus");
                map.put("codenum",0);
                eventmap.put("code"+i, map);
            }
            logger.info("할인코드이 없습니다");
            return;
        }
        logger.info("할인코드 존재");
        String[] splite=codeName.split(",");
        int size=splite.length;
        for(int i=0;i<size;i++){
            for(int ii=0;ii<size;ii++){
                if(i==ii){
                    continue;
                }
                if(splite[i].equals(splite[ii])){
                    throw utillService.makeRuntimeEX("동일코드는 사용불가능합니다", "getTotalPriceAndOther");
                }
            }
        }
        if(splite.length>count){
            throw utillService.makeRuntimeEX("주문 개수보다 할인코드 개수가 많습니다", "getTotalPriceAndOther");
        }
        int temp=0;
            for(String s:splite){
                codesVo codesVo=codesDao.findByCodeName(s).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 할인코드입니다"));
                LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                if(LocalDateTime.now().isAfter(codesVo.getCdExpired().toLocalDateTime())){
                    throw utillService.makeRuntimeEX("기간이 지난 할인코드입니다", "getTotalPriceAndOther");
                }
                map.put("codeaction",codesVo.getCdKind());
                map.put("codenum",codesVo.getCdNum());
                eventmap.put("code"+temp, map);
                temp+=1;
            }
            if(temp<count){
                for(int i=temp;i<count;i++){
                    LinkedHashMap<String,Object>map=new LinkedHashMap<>();
                    map.put("codeaction","minus");
                    map.put("codenum",0);
                    eventmap.put("code"+i, map);
                }
            }
            logger.info("코드액션 담기완료");
        logger.info("코드액션 담기완료");
    }

}

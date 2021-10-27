package com.example.demo.payment.service;



import java.util.List;
import java.util.Map;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.payment.model.getJoinProducts;
import com.example.demo.payment.model.paidProductsDao;
import com.example.demo.payment.model.paidProductsDto;
import com.example.demo.payment.model.tempOrderDao;
import com.example.demo.payment.model.tempOrderDto;
import com.example.demo.payment.model.tempOrderProudctsDao;
import com.example.demo.payment.model.tempOrderProudctsDto;
import com.example.demo.utill.utillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class paymentService {
    private static Logger logger=LoggerFactory.getLogger(paymentService.class);

    @Autowired
    private  tempOrderDao tempOrderDao;
    @Autowired
    private tempOrderProudctsDao tempOrderProudctsDao;
    @Autowired
    private paidProductsDao paidProductsDao;

    public  void insertTemp(String mchtTrdNo,int price,String email) {
        System.out.println(mchtTrdNo+price+email);
        logger.info("insertTemp");
        tempOrderDto  dto=tempOrderDto.builder()
                                        .toMchtTrdNo(mchtTrdNo)
                                        .toPrice(price)
                                        .toemail(email)
                                        .build();
                                        tempOrderDao.save(dto);
    } 
    public void insertTempOrderProducts(List<Map<String,Object>>maps,String mchtTrdNo,String email) {
        logger.info("insertTempOrderProducts");
        int temp=0;
        int size=maps.size();
        for(Map<String,Object> m:maps){
            if(temp>=size-1){
                break;
            }
            tempOrderProudctsDto dto=tempOrderProudctsDto.builder()
                                                        .topCount((int)m.get("count"))
                                                        .topMchtTrdNo(mchtTrdNo)
                                                        .topName((String)m.get("itemName"))
                                                        .topPrice((int)m.get("price"))
                                                        .topemail(email)
                                                        .build();
                                                        tempOrderProudctsDao.save(dto);
                                                        temp+=1;
        }
    }  
    public void confrim(settleDto settleDto) {
        logger.info("confrim");
        List<getJoinProducts>getJoinProducts=tempOrderDao.findJoinProducts(settleDto.getMchtTrdNo());
        if(getJoinProducts.size()==0){
            throw new RuntimeException("거래요청내역이 존재하지 않습니다");
        }
        int totalPrice=getJoinProducts.get(0).getTo_price();
        String mchtTrdNo=getJoinProducts.get(0).getTo_mcht_trd_no();
        confrim(totalPrice, mchtTrdNo, settleDto);
        insert(getJoinProducts);
    }
    private void insert( List<getJoinProducts>getJoinProducts) {
        logger.info("productsTempToMain");
        int size=getJoinProducts.size();
        for(int i=0;i<size;i++){
            paidProductsDto dto=paidProductsDto.builder()
                                                .poCount(getJoinProducts.get(i).getTop_count())
                                                .poMchtTrdNo(getJoinProducts.get(0).getTo_mcht_trd_no())
                                                .poName(getJoinProducts.get(i).getTop_name())
                                                .poPrice(getJoinProducts.get(i).getTop_price())
                                                .poemail(getJoinProducts.get(0).getTo_email())
                                                .build();
                                                paidProductsDao.save(dto);
        }
    }
    private void confrim(int totalPrice,String mchtTrdNo,settleDto settleDto) {
        logger.info("confrim");
        String message=null;
        if(!mchtTrdNo.equals(settleDto.getMchtTrdNo())){
            message="거래번호가 일치하지 않습니다 ";
        }else if(totalPrice!=Integer.parseInt(settleDto.getTrdAmt())){
            message="거래금액이 일치하지 않습니다";
        }else{
            logger.info("거래 유효성 통과");
            return;
        }
        throw utillService.makeRuntimeEX(message, "confrim");
    }
    
    
}
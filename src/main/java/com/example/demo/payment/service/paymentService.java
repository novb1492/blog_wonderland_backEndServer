package com.example.demo.payment.service;



import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.demo.apis.settle.model.settleDto;
import com.example.demo.enums.Stringenums;
import com.example.demo.events.code.model.codesDao;
import com.example.demo.events.code.model.codesVo;
import com.example.demo.events.code.model.usedCodesDao;
import com.example.demo.events.code.model.usedCodesVo;
import com.example.demo.events.coupon.model.couponsDao;
import com.example.demo.events.coupon.model.couponsVo;
import com.example.demo.events.point.model.pointsDao;
import com.example.demo.events.point.model.pointsVo;
import com.example.demo.events.point.model.usedPointDao;
import com.example.demo.events.point.model.usedPointVo;
import com.example.demo.payment.model.getJoinProducts;
import com.example.demo.payment.model.paidProductsDao;
import com.example.demo.payment.model.paidProductsDto;
import com.example.demo.payment.model.tempOrderDao;
import com.example.demo.payment.model.tempOrderDto;
import com.example.demo.payment.model.tempOrderProudctsDao;
import com.example.demo.payment.model.tempOrderProudctsDto;
import com.example.demo.product.model.productDao;
import com.example.demo.product.model.productVo;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class paymentService {
    private static Logger logger=LoggerFactory.getLogger(paymentService.class);
    private final int doneFlag=1;
    private final String cardMchtId=Stringenums.cardMchtId.getString();
    @Autowired
    private  tempOrderDao tempOrderDao;
    @Autowired
    private tempOrderProudctsDao tempOrderProudctsDao;
    @Autowired
    private paidProductsDao paidProductsDao;
    @Autowired
    private productDao productDao;
    @Autowired
    private couponsDao couponsDao;
    @Autowired
    private codesDao codesDao;
    @Autowired
    private usedCodesDao usedCodesDao;
    @Autowired
    private pointsDao pointsDao;
    @Autowired
    private usedPointDao usedPointDao;
    @Autowired
    private userService userService;


    public void updateTemp(String mchtTrdNo) {
        logger.info("updateTemp");
        Timestamp now=Timestamp.valueOf(LocalDateTime.now());
    
        if(mchtTrdNo.startsWith("product")){
            tempOrderDao.updateTempProducts(doneFlag,doneFlag,now,now,mchtTrdNo);
        }
    }
    public  void insertTemp(String mchtTrdNo,String email,String buyKind,int toCash,int toPoint,String expireDate) {
        System.out.println(mchtTrdNo+email);
        logger.info("insertTemp");
        tempOrderDto  dto=tempOrderDto.builder()
                                        .toMchtTrdNo(mchtTrdNo)
                                        .toPrice(toCash+toPoint)
                                        .toemail(email)
                                        .toDoneFlag(0)
                                        .buyKind(buyKind)
                                        .toCash(toCash)
                                        .toPoint(toPoint)
                                        .vbankDate(expireDate)
                                        .build();
                                        tempOrderDao.save(dto);
    }
    public void insertTemp(List<Map<String,Object>>maps,String mchtTrdNo,String email) {
        logger.info("insertTemp");
        if(mchtTrdNo.startsWith("product")){
            insertTempOrderProducts(maps, mchtTrdNo, email);
        }
    }
    private void insertTempOrderProducts(List<Map<String,Object>>maps,String mchtTrdNo,String email) {
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
                                                        .topDoneFlag(0)
                                                        .topUsecoupon((String)m.get("coupon"))
                                                        .topUsecode((String)m.get("code"))
                                                        .build();
                                                        tempOrderProudctsDao.save(dto);
                                                        temp+=1;
        }
    }  
    public void confrimAndInsert(settleDto settleDto) {
        logger.info("confrim");
        List<getJoinProducts>getJoinProducts=tempOrderDao.findJoinProducts(settleDto.getMchtTrdNo());
        if(getJoinProducts.size()==0){
            throw utillService.makeRuntimeEX("거래요청내역이 존재하지 않습니다","confrim");
        }
        int totalPrice=getJoinProducts.get(0).getTo_cash();
        String mchtTrdNo=getJoinProducts.get(0).getTo_mcht_trd_no();
        int point=getJoinProducts.get(0).getTo_point();
        String email=userService.sendUserInfor().getEmail();
        confrim(totalPrice, mchtTrdNo, settleDto);
        insert(getJoinProducts);
        pointsVo pointsVo=pointsDao.findByPoEmail(email);
        minusPoint(email, point,pointsVo);
        insertPoint(email, point, mchtTrdNo);
        plusPoint(settleDto,pointsVo);
    }
    private void plusPoint(settleDto settleDto,pointsVo pointsVo) {
        logger.info("plusPoint");
        String mchtId=settleDto.getMchtId();
        int poHaving= pointsVo.getPoHaving();
        if(mchtId.equals(cardMchtId)){
            poHaving+=Integer.parseInt(settleDto.getTrdAmt())*0.1;
        }
        pointsVo.setPoHaving(poHaving);
    }
    private void insert( List<getJoinProducts>getJoinProducts) {
        logger.info("productsTempToMain");
        int size=getJoinProducts.size();
        for(int i=0;i<size;i++){
            String productName=getJoinProducts.get(i).getTop_name();
            int count=getJoinProducts.get(i).getTop_count();
            String mchtTrdNo=getJoinProducts.get(0).getTo_mcht_trd_no();
            String email=getJoinProducts.get(0).getTo_email();
            String coupons=getJoinProducts.get(i).gettop_usecoupon();
            String codes=getJoinProducts.get(i).gettop_usecode();
            minusCount(productName, count);
            couponOn(coupons,mchtTrdNo,email);
            insertCode(codes, mchtTrdNo, email);
            paidProductsDto dto=paidProductsDto.builder()
                                                .poCount(count)
                                                .poMchtTrdNo(mchtTrdNo)
                                                .poName(productName)
                                                .poPrice(getJoinProducts.get(i).getTop_price())
                                                .poemail(email)
                                                .poCode(codes)
                                                .poCoupon(coupons)
                                                .build();
                                                paidProductsDao.save(dto);
        }
    }
    private void insertCode(String codes,String mchtTrdNo,String email) {
        logger.info("insertCode");
        if(utillService.checkBlankOrNull(codes)){
            return;
        }
        String[] codeNameArray=codes.split(",");
        for(String codeName:codeNameArray){
            codesVo codesVo=codesDao.findByCodeName(codeName).orElseThrow(()->new IllegalArgumentException("존재하지 않는 할인코드입니다"));
            if(codesVo.getCdExpired().toLocalDateTime().isBefore(LocalDateTime.now())){
                throw new RuntimeException(codeName+"만료된 할인쿠폰 입니다");
            }
            usedCodesVo vo=usedCodesVo.builder()    
                                    .ucdEmail(email)
                                    .ucdcreated(Timestamp.valueOf(LocalDateTime.now()))
                                    .ucdmchtTrdNo(mchtTrdNo)
                                    .ucodeName(codeName)
                                    .ucdCancleFlag(0)
                                    .build();
                                    usedCodesDao.save(vo);
        }
    }
    private void couponOn(String coupons,String mchtTrdNo,String email) {
        logger.info("couponOn");
        if(utillService.checkBlankOrNull(coupons)){
            return;
        }
        String[] couponNameArray=coupons.split(",");
        for(String couponName:couponNameArray){
            couponsVo couponsVo=couponsDao.findByCouponName(couponName).orElseThrow(()->new IllegalArgumentException("존재하지 않는 쿠폰입니다"));
            if(couponsVo.getCoExpired().toLocalDateTime().isBefore(LocalDateTime.now())){
                throw new RuntimeException(couponName+"만료된 쿠폰 입니다");
            }else if(couponsVo.getUsedFlag()==1){
                throw new RuntimeException(couponName+"이미 사용된 쿠폰입니다");
            }
            couponsVo.setUsedDate(Timestamp.valueOf(LocalDateTime.now()));
            couponsVo.setUsedFlag(1);
            couponsVo.setCoUsedEmail(email);
            couponsVo.setCoMchtTrdNo(mchtTrdNo);
        }
    }
    private void minusCount(String productName,int count){
        logger.info("minusCount");
        productVo productVo=productDao.findByProductName(productName).orElseThrow(()->new IllegalArgumentException("메시지 : 존재하지 않는 상품결제 시도입니다"));
        int afterMinus=productVo.getCount()-count;
        if(afterMinus<0){
            throw utillService.makeRuntimeEX(productName+"의 재고가 부족합니다 ", "minusCount");
        }
        productVo.setCount(afterMinus);
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
    private void minusPoint(String email,int point,pointsVo pointsVo) {
        logger.info("minusPoint");
        int dbPoint=pointsVo.getPoHaving();
        if(point<0){
            return;
        }
        if(point>dbPoint){
            throw new RuntimeException("보유포인트가 부족합니다 보유포인트 "+dbPoint+"지불 요청 포인트 "+point);
        }
        pointsVo.setPoHaving(dbPoint-point);
      
    }
    private void insertPoint(String email,int point,String mchtTrdNo) {
        usedPointVo vo=usedPointVo.builder()
                                .upCancelFlag(0)
                                .upoEmail(email)
                                .upoMchtTrdNo(mchtTrdNo)
                                .upoint(point)
                                .build();
        usedPointDao.save(vo);
    }

    
    
}

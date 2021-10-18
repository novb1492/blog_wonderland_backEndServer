package com.example.demo.send;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.confrim.model.sendInter;
import com.example.demo.confrim.model.sendRandNumInter;
import com.example.demo.confrim.model.phone.getRequestAndusersInter;
import com.example.demo.confrim.model.phone.phoneDao;
import com.example.demo.confrim.model.phone.phoneVo;
import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.confrim.service.confrimService;
import com.example.demo.enums.Stringenums;
import com.example.demo.enums.intEnums;
import com.example.demo.find.service.findService;
import com.example.demo.user.model.tryUpadateDto;
import com.example.demo.user.service.userService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class phoneService {

    private final int noDoneNum=intEnums.noDoneNum.getInt();
    private final int doneNum=intEnums.doneNum.getInt();
    private final String confrim=Stringenums.confrim.getString();
    private final String find=Stringenums.find.getString();
    private final String update=Stringenums.update.getString();

    @Autowired
    private phoneDao phoneDao;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private findService findService;
    @Autowired
    private userService userService;
    
    @Transactional(rollbackFor = Exception.class)
    public void sendPhone(trySendSmsDto sendSmsDto) {
        System.out.println("sendPhone");
        String phone=sendSmsDto.getUnit();
        confrim(phone);
        System.out.println(phone);
        String detail=sendSmsDto.getDetail();
        phoneVo phoneVo=new phoneVo();
        if(detail.equals(confrim)){
            System.out.println("회원가입시 핸드폰 번호 요청");
            phoneVo=phoneDao.findByPphoneNative(phone,detail).orElseGet(() -> new phoneVo().builder().pcount(0).pcreated(Timestamp.valueOf(LocalDateTime.now())).pphoneNum(phone).build());
        }else if(detail.equals(find)||detail.equals(update)){
            System.out.println("이미 회원가입 되어있는 휴대폰 번호 찾기");
            phoneVo=getPhoneVo(sendSmsDto);
        }else{
            throw new RuntimeException("detail이 유요하지 않습니다");
        }
        System.out.println("문자메세지 전송"+phoneVo);
        sendRandNumInter sendRandNumInter=new sendInter(phoneVo.getPcount(),phoneVo.getPphoneNum(),phoneVo.getPcreated(),utillService.getRandomNum(intEnums.randNumLength.getInt()),phoneVo.getDonePhone(),sendSmsDto.getScope(),detail);
        String result=confrimService.sendRandNum(sendRandNumInter);
        if(result.equals(Stringenums.first.getString())){
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.noFirst.getString())){
            update(sendRandNumInter);
        }else if(result.equals(Stringenums.reset.getString())){
            delete(phoneVo.getPphoneNum());
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.tooMany.getString())){
            throw new RuntimeException("하루 "+intEnums.maxRequest.getInt()+"회 제한입니다");
        }else{
            throw new RuntimeException("알 수없는 오류발생");
        }
        //sendMessage.sendMessege(sendRandNumInter.getEmailOrPhone(),sendRandNumInter.getRandNum()); 
    }
    private phoneVo getPhoneVo(trySendSmsDto sendSmsDto) {
        System.out.println("getGetRequestAndusersInter");
        phoneVo phoneVo=new phoneVo();
        getRequestAndusersInter getRequestAndusersInter=null;
        String phone=sendSmsDto.getUnit();
        String detail=sendSmsDto.getDetail();
        if(detail.equals(update)){
            System.out.println("change입니다");
            getRequestAndusersInter=phoneDao.findPhoneJoinUsers2(phone,detail,phone,detail,phone,detail,phone,detail,phone,detail);
            if(utillService.checkEquals(getRequestAndusersInter.getAlready(), 1)){
                throw new RuntimeException("이미존재 하는 핸드폰입니다");
            }
        }else{
            getRequestAndusersInter=phoneDao.findPhoneJoinUsers(detail,sendSmsDto.getUnit());
            confrimService.confrimAlready(getRequestAndusersInter.getAlready());
        }
        if(Optional.ofNullable(getRequestAndusersInter.getPphone_num()).orElseGet(()->"emthy").equals("emthy")){
            System.out.println("첫요청");
            phoneVo.setDonePhone(noDoneNum);
            phoneVo.setPcount(0);
            phoneVo.setPcreated(Timestamp.valueOf(LocalDateTime.now()));
        }else{
            System.out.println("요청 내역존재");
            phoneVo.setDonePhone(getRequestAndusersInter.getDone_phone());
            phoneVo.setPcount(getRequestAndusersInter.getPcount());
            phoneVo.setPcreated(getRequestAndusersInter.getPcreated());
        }
        phoneVo.setPphoneNum(sendSmsDto.getUnit());
        return phoneVo;
    }
    private void confrim(String phone) {
        System.out.println("confrim");
        String reult=utillService.checkLength(11, 11, phone);
        if(!reult.equals(Stringenums.collect.getString())){
            throw new RuntimeException("핸드폰 번호를 확인해주세요");
        }
    }
    private void insert(sendRandNumInter sendRandNumInter){
        System.out.println("insert");
        phoneVo vo=phoneVo.builder()
        .pcount(1)
        .pphoneNum(sendRandNumInter.getEmailOrPhone())
        .randNum(sendRandNumInter.getRandNum())
        .detail(sendRandNumInter.getDetail())
        .donePhone(noDoneNum)
        .build();
        phoneDao.save(vo);
    }
    private void update(sendRandNumInter sendRandNumInter) {
        System.out.println("update");
        phoneDao.updatePhoneNative(sendRandNumInter.getCount()+1,sendRandNumInter.getRandNum(),Timestamp.valueOf(LocalDateTime.now()),sendRandNumInter.getEmailOrPhone(),sendRandNumInter.getDetail());
    }
    public void delete(String phone) {
        System.out.println("delete"+phone);
        phoneDao.deleteByPphoneNum(phone);
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject checkNum(tryConfrimRandNumDto tryConfrimRandNumDto) {
        System.out.println("checkNum");
        String phone=tryConfrimRandNumDto.getPhoneOrEmail();
        phoneVo phoneVo=phoneDao.findByPphoneNative(phone,tryConfrimRandNumDto.getScope()).orElseThrow(()->new IllegalArgumentException("인증 요청 내역이 존재 하지 않습니다"));
        confrimService.confrimNum(tryConfrimRandNumDto.getRandNum(), phoneVo.getRandNum(),phoneVo.getPcreated());
        phoneVo.setDonePhone(doneNum);
        return ifFind(tryConfrimRandNumDto.getScope(), phone,phoneVo);
    }
    private JSONObject ifFind(String scope,String phone,phoneVo phoneVo) {
        System.out.println("ifFind");
        String message="인증이 완료되었습니다";
        if(scope.equals(confrim)){
            System.out.println("인증서비스 요청");
        }else if(scope.equals(find)){
            System.out.println("이메일 찾기 요청");
            findService.findEmail(phone);
            message="핸드폰으로 이메일을 전송했습니다";
        }else if(scope.equals(update)){
            System.out.println("핸드폰 번호 변경요청");
            confrimService.confrimDone(phoneVo.getDonePhone());
            tryUpadateDto tryUpadateDto=new tryUpadateDto();
            tryUpadateDto.setPhone(phone);
            tryUpadateDto.setScope("phone");
            userService.update(tryUpadateDto);
            delete(phone);
            message="휴대폰 번호가 변경되었습니다";
        }else{
            return utillService.makeJson(true, message);
        }
        return utillService.makeJson(true, message);
    }
    

   
}

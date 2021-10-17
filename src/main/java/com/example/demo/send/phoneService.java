package com.example.demo.send;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.apis.coolsms.sendMessage;
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
import com.example.demo.user.model.uservo;
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
        confrim(sendSmsDto.getUnit());
        System.out.println(sendSmsDto.getUnit());
        phoneVo phoneVo=new phoneVo();
        if(sendSmsDto.getDetail().equals("confrim")){
            System.out.println("회원가입시 핸드폰 번호 요청");
            phoneVo=phoneDao.findByPhoneNum(sendSmsDto.getUnit()).orElseGet(() -> new phoneVo().builder().pcount(0).pcreated(Timestamp.valueOf(LocalDateTime.now())).phoneNum(sendSmsDto.getUnit()).build());
        }else if(sendSmsDto.getDetail().equals("find")){
            System.out.println("이미 회원가입 되어있는 휴대폰 번호 찾기");
            getRequestAndusersInter getRequestAndusersInter=phoneDao.findPhoneJoinUsers(sendSmsDto.getUnit());
            confrimService.confrimAlready(getRequestAndusersInter.getAlready());
            if(Optional.ofNullable(getRequestAndusersInter.getPhone_num()).orElseGet(()->"emthy").equals("emthy")){
                System.out.println("첫요청");
                phoneVo.setDonePhone(noDoneNum);
                phoneVo.setPcount(0);
                phoneVo.setPcreated(Timestamp.valueOf(LocalDateTime.now()));
            }else{
                System.out.println("요청 내역존재");
                phoneVo.setPid(getRequestAndusersInter.getPid());
                phoneVo.setDonePhone(getRequestAndusersInter.getDone_phone());
                phoneVo.setPcount(getRequestAndusersInter.getPcount());
                phoneVo.setPcreated(getRequestAndusersInter.getPcreated());
            }
            phoneVo.setPhoneNum(sendSmsDto.getUnit());
        }else{
            throw new RuntimeException("detail이 유요하지 않습니다");
        }
        System.out.println("문자메세지 전송"+phoneVo);
        sendRandNumInter sendRandNumInter=new sendInter(phoneVo.getPcount(),phoneVo.getPhoneNum(),phoneVo.getPcreated(),utillService.getRandomNum(intEnums.randNumLength.getInt()),phoneVo.getDonePhone(),sendSmsDto.getScope());
        String result=confrimService.sendRandNum(sendRandNumInter);
        if(result.equals(Stringenums.first.getString())){
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.noFirst.getString())){
            update(sendRandNumInter);
        }else if(result.equals(Stringenums.reset.getString())){
            delete(phoneVo.getPhoneNum());
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.tooMany.getString())){
            throw new RuntimeException("하루 "+intEnums.maxRequest.getInt()+"회 제한입니다");
        }else{
            throw new RuntimeException("알 수없는 오류발생");
        }
        sendMessage.sendMessege(sendRandNumInter.getEmailOrPhone(),sendRandNumInter.getRandNum()); 
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
        .phoneNum(sendRandNumInter.getEmailOrPhone())
        .randNum(sendRandNumInter.getRandNum())
        .donePhone(noDoneNum)
        .build();
        phoneDao.save(vo);
    }
    private void update(sendRandNumInter sendRandNumInter) {
        System.out.println("update");
        phoneDao.updatePhoneNative(sendRandNumInter.getCount()+1,sendRandNumInter.getRandNum(),Timestamp.valueOf(LocalDateTime.now()),sendRandNumInter.getEmailOrPhone());
    }
    public void delete(String phone) {
        System.out.println("delete"+phone);
        phoneDao.deleteByPhoneNum(phone);
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject checkNum(tryConfrimRandNumDto tryConfrimRandNumDto) {
        System.out.println("checkNum");
        String phone=tryConfrimRandNumDto.getPhoneOrEmail();
        phoneVo phoneVo=phoneDao.findByPhoneNum(phone).orElseThrow(()->new IllegalArgumentException("인증 요청 내역이 존재 하지 않습니다"));
        confrimService.confrimNum(tryConfrimRandNumDto.getRandNum(), phoneVo.getRandNum(),phoneVo.getPcreated());
        phoneVo.setDonePhone(doneNum);
        return ifFind(tryConfrimRandNumDto.getScope(), phone);
    }
    private JSONObject ifFind(String scope,String phone) {
        System.out.println("ifFind");
        String message="인증이 완료되었습니다";
        if(scope.equals(Stringenums.confrim.getString())){
            System.out.println("인증서비스 요청");
        }else if(scope.equals(Stringenums.find.getString())){
            System.out.println("이메일 찾기 요청");
            findService.findEmail(phone);
            message="핸드폰으로 이메일을 전송했습니다";
        }else if(scope.equals(Stringenums.update.getString())){
            System.out.println("핸드폰 번호 변경요청");
            phoneVo phoneVo=phoneDao.findByPhoneNum(phone).orElseThrow(()->new IllegalArgumentException("요청 내역이 존재 하지 않습니다"));
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

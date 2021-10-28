package com.example.demo.send;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.apis.mailSender.sendMailService;
import com.example.demo.confrim.model.sendInter;
import com.example.demo.confrim.model.sendRandNumInter;
import com.example.demo.confrim.model.email.emailDao;
import com.example.demo.confrim.model.email.emailVo;
import com.example.demo.confrim.model.email.getUserJoinInter;
import com.example.demo.confrim.model.phone.tryConfrimRandNumDto;
import com.example.demo.confrim.model.phone.trySendSmsDto;
import com.example.demo.confrim.service.confrimService;
import com.example.demo.enums.Stringenums;
import com.example.demo.enums.intEnums;
import com.example.demo.find.service.findService;
import com.example.demo.utill.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class emailService {
    private final int maxReuqest=10;
    private final int noDoneNum=intEnums.noDoneNum.getInt();
    private final int doneNum=intEnums.doneNum.getInt();
    private final static Logger LOGGER=LoggerFactory.getLogger(emailService.class);
    @Autowired
    private emailDao emailDao;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private sendMailService sendMailService;
    @Autowired
    private findService findService;

    @Transactional(rollbackFor = Exception.class)
    public void sendEmail(trySendSmsDto trySendSmsDto) {
        LOGGER.info("sendEmail");
        LOGGER.info(trySendSmsDto.getDetail());
    
        emailVo emailVo=new emailVo();
        if(trySendSmsDto.getDetail().equals("confrim")){
            LOGGER.info("회원가입시 이메일 요청");
        }else if(trySendSmsDto.getDetail().equals("find")){
            LOGGER.info("이미 회원가입 되어있는 이메일 찾기");
            LOGGER.info(trySendSmsDto.getUnit());
            getUserJoinInter getUserJoinInter=emailDao.findByEemailJoinUsers(trySendSmsDto.getUnit());
            confrimService.confrimAlready(getUserJoinInter.getAlready());
            if(Optional.ofNullable(getUserJoinInter.getEemail()).orElseGet(()->"emthy").equals("emthy")){
                LOGGER.info("첫요청");
                emailVo.setDoneemail(0);
                emailVo.setEcount(0);
                emailVo.setEcreated(Timestamp.valueOf(LocalDateTime.now()));
            }else{
                LOGGER.info("요청 내역존재");
                emailVo.setEid(getUserJoinInter.getEid());
                emailVo.setDoneemail(getUserJoinInter.getDoneemail());
                emailVo.setEcount(getUserJoinInter.getEcount());
                emailVo.setEcreated(getUserJoinInter.getEcreated());
            }
            emailVo.setEemail(trySendSmsDto.getUnit());
        }else{
            throw utillService.makeRuntimeEX("detail이 유요하지 않습니다","sendEmail");
        }
        LOGGER.info("이메일 전송"+emailVo);
        sendRandNumInter sendRandNumInter=new sendInter(emailVo.getEcount(),emailVo.getEemail(),emailVo.getEcreated(), utillService.getRandomNum(intEnums.randNumLength.getInt()),emailVo.getDoneemail(),trySendSmsDto.getScope(),trySendSmsDto.getDetail());
        String result=confrimService.sendRandNum(sendRandNumInter);
        if(result.equals(Stringenums.first.getString())){
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.noFirst.getString())){
            update(sendRandNumInter);
        }else if(result.equals(Stringenums.reset.getString())){
            delete(emailVo.getEemail());
            insert(sendRandNumInter);
        }else if(result.equals(Stringenums.tooMany.getString())){
            throw utillService.makeRuntimeEX("하루 "+maxReuqest+"회 제한입니다","sendEmail");
        }else{
            throw utillService.makeRuntimeEX("알 수없는 오류발생","sendEmail");
        }
        sendMailService.sendEmail(sendRandNumInter.getEmailOrPhone(),"안녕하세요 wonderland입니다","인증번호는 "+sendRandNumInter.getRandNum()+"입니다");
    }
    private void insert(sendRandNumInter sendRandNumInter) {
        LOGGER.info("insert");
        emailVo vo=emailVo.builder()
                            .ecount(1)
                            .eemail(sendRandNumInter.getEmailOrPhone())
                            .erandNum(sendRandNumInter.getRandNum())
                            .doneemail(noDoneNum)
                            .detail(sendRandNumInter.getDetail())
                            .build();
                            emailDao.save(vo);
    }
    private void update(sendRandNumInter sendRandNumInter){
        LOGGER.info("update");
        LOGGER.info("이메일 요청 횟수 증가");
        emailDao.updateEmailNative(sendRandNumInter.getCount()+1, sendRandNumInter.getRandNum(),Timestamp.valueOf(LocalDateTime.now()),sendRandNumInter.getEmailOrPhone());
    }
    private void delete(String email) {
        LOGGER.info("delete");
        emailDao.deleteByEemail(email);
    }
    @Transactional
    public JSONObject checkNum(tryConfrimRandNumDto tryConfrimRandNumDto) {
        LOGGER.info("checkNum");
        String email=tryConfrimRandNumDto.getPhoneOrEmail();
        emailVo emailVo=emailDao.findByEemail(email).orElseThrow(()->new IllegalArgumentException("메세지: 인증요청 내역이 존재하지 않습니다"));
        confrimService.confrimNum(tryConfrimRandNumDto.getRandNum(), emailVo.getErandNum(),emailVo.getEcreated());
        emailVo.setDoneemail(doneNum);
        return  ifFind(tryConfrimRandNumDto.getScope(),email);
    }
    private JSONObject ifFind(String scope,String phoneOrEmail) {
        LOGGER.info("ifFind");
        String message="인증이 완료되었습니다";
        if(scope.equals(Stringenums.confrim.getString())){
            LOGGER.info("인증서비스 요청");
        }else if(scope.equals("find")){
            LOGGER.info("비밀번호 찾기 요청");
            findService.findPwd(phoneOrEmail);
            message="이메일로 링크가 전송되었습니다";
        }else{
            return utillService.makeJson(false,"유효하지 않는 스코프 혹은 유닛입니다");
        }
        return utillService.makeJson(true, message);
    }

}

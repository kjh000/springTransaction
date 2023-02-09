package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService     @Transaction:OFF
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON
     */
    @Test
    void outTxOff_success() {
        //given
        String username = "outTxOff_success";
        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent()); // 여기서는 junit.jupiter.api 의 Assertion 사용
        assertTrue(logRepository.find(username).isPresent());
    }


    /**
     * memberService     @Transaction:OFF
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void outTxOff_fail() {
        //given
        String username = "로그예외_outTxOff_fail";
        //when
        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then
        assertTrue(memberRepository.find(username).isPresent()); // 여기서는 junit.jupiter.api 의 Assertion 사용
        assertTrue(logRepository.find(username).isEmpty()); // 예외가 터졌으므로 로그는 저장이 안됨
    }


    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:OFF
     * logRepository     @Transaction:OFF
     */
    @Test
    void singleTx() {
        //given
        String username = "singleTx";
        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON
     */
    @Test
    void outerTxOn_success() {
        //given
        String username = "outerTxOn_success";
        //when
        memberService.joinV1(username);

        //then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void outTxOn_fail() {
        //given
        String username = "로그예외_outTxOn_fail";
        //when
        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then 모든 데이터가 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";
        //when
        assertThatThrownBy(()->memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //then 모든 데이터가 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_success";
        //when
        memberService.joinV2(username);

        //then member, 저장 log 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


}
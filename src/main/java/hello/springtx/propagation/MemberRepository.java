package hello.springtx.propagation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    @Transactional
    public void save(Member member){
        log.info("member saved");
        em.persist(member);
    }

    public Optional<Member> find(String username){
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username",username)
                .getResultList().stream().findAny(); // getResult()는 값 없을때 예외를 터뜨려서 getResultList사용

    }



}

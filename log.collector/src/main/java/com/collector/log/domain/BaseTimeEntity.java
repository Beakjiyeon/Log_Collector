package com.collector.log.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass // Jpa entity 클래스가 아래 클래스를 상속할 경우 createAt 도 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class) // Auditing 기능
public class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createAt; // 엔티티가 생성되어 저장될 때 시간이 자동 저장

}

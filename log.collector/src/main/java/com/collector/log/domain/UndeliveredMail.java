package com.collector.log.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@Getter
@NoArgsConstructor
@Entity
@Table(name="undelivered_mail")
public class UndeliveredMail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(name = "cause_keyword", length = 100)
    private String causeKeyword;

    @Column(name = "cause_content", length = 65535)
    private String causeContent;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traced_log_id", nullable = false)
    private TracedLog tracedLog;

    @Builder
    public UndeliveredMail (Long id, String causeKeyword, String causeContent, TracedLog tracedLog) {
        this.id = id;
        this.causeKeyword = causeKeyword;
        this.causeContent = causeContent;
        this.tracedLog = tracedLog;
    }
}

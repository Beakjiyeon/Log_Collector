package com.collector.log.hateoas;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

public class MemberModel extends RepresentationModel<MemberModel> {

    public MemberModel(Member member) {
        this.member = member;
    }

    @JsonUnwrapped // member 반환 시 member depth를 없애고 member의 멤버변수 출력
    private final Member member;

}